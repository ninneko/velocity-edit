package com.hudson.velocityweb.editors.velocity.completion;

import org.eclipse.jdt.internal.ui.text.template.contentassist.PositionBasedCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.hudson.velocityweb.editors.velocity.parser.VelocityMacro;

public class VelocityTemplateProposal extends PositionBasedCompletionProposal {
	private VelocityMacro macro;
	
	private IRegion fSelectedRegion; // initialized by apply()

	public VelocityTemplateProposal(String replacementString, Position replacementPosition, int cursorPosition,
			Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo,
			char[] triggers, VelocityMacro macro) {
		super(replacementString, replacementPosition, cursorPosition, image, displayString, contextInformation,
				additionalProposalInfo, triggers);

		this.macro = macro;
	}
	
	@Override
	public Point getSelection(IDocument document) {
		if (fSelectedRegion == null)
			return new Point(0, 0);

		return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
	}

	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		super.apply(viewer, trigger, stateMask, offset);

		try {
			LinkedModeModel model = new LinkedModeModel();

			StringBuffer insert = new StringBuffer();
			insert.append(macro.name);
			insert.append("(");

			int parameterOffset = offset + insert.length();
			for (int k = 0; k < macro.parameters.length; k++) {
				LinkedPositionGroup group = new LinkedPositionGroup();

				if (k > 0)
					// space between parameters
					parameterOffset++;

				group.addPosition(new LinkedPosition(viewer.getDocument(), parameterOffset, macro.parameters[k].length(),
						LinkedPositionGroup.NO_STOP));
				model.addGroup(group);
				
				parameterOffset += macro.parameters[k].length();
			}
			
			model.forceInstall();

			LinkedModeUI ui = new EditorLinkedModeUI(model, viewer);
			ui.setExitPosition(viewer, parameterOffset + 1, 0, Integer.MAX_VALUE);
			ui.setCyclingMode(LinkedModeUI.CYCLE_ALWAYS);
			ui.enter();
			
			fSelectedRegion = ui.getSelectedRegion();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
