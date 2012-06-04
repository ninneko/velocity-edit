package com.hudson.velocityweb.editors.velocity.completion;

import org.eclipse.jdt.internal.ui.text.template.contentassist.PositionBasedCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.hudson.velocityweb.editors.velocity.parser.VelocityMacro;

public class VelocityTemplateProposal extends PositionBasedCompletionProposal {
	private VelocityMacro macro;

	public VelocityTemplateProposal(String replacementString, Position replacementPosition, int cursorPosition,
			Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo,
			char[] triggers, VelocityMacro macro) {
		super(replacementString, replacementPosition, cursorPosition, image, displayString, contextInformation,
				additionalProposalInfo, triggers);

		this.macro = macro;
	}

	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		super.apply(viewer, trigger, stateMask, offset);

		try {
			LinkedModeModel model = new LinkedModeModel();

			StringBuffer insert = new StringBuffer();
			insert.append(macro.name);
			insert.append("(");

			for (int k = 0; k < macro.parameters.length; k++) {
				LinkedPositionGroup group = new LinkedPositionGroup();

				if (k > 0)
					insert.append(" ");

				group.addPosition(new LinkedPosition(viewer.getDocument(), offset + insert.length(), macro.parameters[k].length(),
						LinkedPositionGroup.NO_STOP));
				insert.append(macro.parameters[k]);

				model.addGroup(group);
			}
			
			insert.append(")");

			model.forceInstall();

			LinkedModeUI ui = new EditorLinkedModeUI(model, viewer);
			ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
			ui.setDoContextInfo(true);
			ui.enter();
		} catch (Exception e) {
		}
	}
}
