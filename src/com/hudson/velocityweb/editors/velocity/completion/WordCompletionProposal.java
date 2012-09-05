package com.hudson.velocityweb.editors.velocity.completion;


import org.eclipse.swt.graphics.Image;

public class WordCompletionProposal extends FuzzyMachCompletionProposal {
	public WordCompletionProposal(String replacementString, int replacementOffset, int replacementLength, Image image,
			String displayString, int relevance) {
		super(replacementString, replacementOffset, replacementLength, image, displayString, relevance);
	}
}
