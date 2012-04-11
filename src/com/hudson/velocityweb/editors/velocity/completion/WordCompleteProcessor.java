package com.hudson.velocityweb.editors.velocity.completion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.hudson.velocityweb.Plugin;


public class WordCompleteProcessor implements IContentAssistProcessor {
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		proposals.add(new CompletionProposal(
				"FUCK",
				offset,
				"FUCK".length(),
				"FUCK".length(),
				Plugin.getDefault().getImage("tag_li"), "FUCKKK", null, null));
		
		return proposals.toArray(new ICompletionProposal[1]);
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		char[] chars = {'a','b','c','d','e'};
		return chars;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		char[] chars = {'a','b','c','d','e'};
		return chars;
	}

	@Override
	public String getErrorMessage() {
		return "Sorry, you're wong!";
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}
}