package com.hudson.velocityweb.editors.velocity.completion;

import org.eclipse.jdt.internal.ui.text.template.contentassist.PositionBasedCompletionProposal;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

public class WordCompletionProposal extends PositionBasedCompletionProposal {
	private int fRelevance;

	public WordCompletionProposal(String replacementString, Position replacementPosition, int cursorPosition,
			Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo,
			char[] triggers) {
		super(replacementString, replacementPosition, cursorPosition, image, displayString, contextInformation,
				additionalProposalInfo, triggers);
	}

	@Override
	public int getRelevance() {
		return fRelevance;
	}

	public void setRelevance(int relevance) {
		fRelevance = relevance;
	}
}
