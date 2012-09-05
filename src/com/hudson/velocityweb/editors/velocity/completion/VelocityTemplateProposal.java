package com.hudson.velocityweb.editors.velocity.completion;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.hudson.velocityweb.editors.velocity.parser.VelocityMacro;

public class VelocityTemplateProposal extends FuzzyMachCompletionProposal {
	private VelocityMacro macro;
	
	private IRegion fSelectedRegion; // initialized by apply()
	
	private Position position;
	
	private String replaceString;
	
	private String displayString;
	
	private Image image;

	public VelocityTemplateProposal(String replacementString, Position replacementPosition, int cursorPosition,
			Image image, String displayString, VelocityMacro macro) {
		super(replacementString, replacementPosition.offset, replacementPosition.length, image, displayString, 10000);
		
		this.image = image;
		this.replaceString = replacementString;
		this.displayString = displayString;
		this.position = replacementPosition;
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
		apply(viewer.getDocument());
		
		try {
			LinkedModeModel model = new LinkedModeModel();

			StringBuffer insert = new StringBuffer();
			insert.append(macro.name);
			insert.append("(");

			int parameterOffset = position.offset + insert.length();
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

	@Override
	public String getAdditionalProposalInfo() {
		return null;
	}

	@Override
	public String getDisplayString() {
		return displayString;
	}

	@Override
	public Image getImage() {
		return image;
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

	@Override
	public void selected(ITextViewer viewer, boolean smartToggle) {
	}

	@Override
	public void unselected(ITextViewer viewer) {
	}

	@Override
	public boolean validate(IDocument document, int offset, DocumentEvent event) {
		try {
			String content= document.get(position.getOffset(), offset - position.getOffset());
			
			position.setLength(offset - position.getOffset());
			
			if (replaceString.startsWith(content))
				return true;
		} catch (BadLocationException e) {
			// ignore concurrently modified document
		}
		
		return false;
	}
}
