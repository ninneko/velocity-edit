package com.hudson.velocityweb.editors.velocity.completion;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

public class FuzzyMachCompletionProposal extends JavaCompletionProposal {

	private static final int MATCHING_DEPTH = 2;
	private static final int PERFECT_PREFIX_BONUS = 10;
	private static final int PREFIX_BONUS = 400;
	private static final int FUZZY_MATCH_BONUS = 5;
	private Map<Integer, Integer> matchPositions = new HashMap<Integer, Integer>();
	private int originalRevelance = -1;
	private Integer textStartPos = 0;
	private static Styler NO_MATCH_COLOR = new Styler() {
			@Override
			public void applyStyles(final TextStyle textStyle) {
	//			textStyle.font = new Font(null, "Arial", 10, SWT.NORMAL);
				textStyle.foreground = new Color(null, 0,0,0);
			}
		};
	private static Styler MATCH_COLOR = new Styler() {
			@Override
			public void applyStyles(final TextStyle textStyle) {
	//			textStyle.font = new Font(null, "Arial", 10, SWT.BOLD);
				textStyle.foreground = new Color(null, 255, 127, 39);
	//			textStyle.underline = true;
			}
		};

	public FuzzyMachCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, Image image,
			String displayString, int relevance) {
		super(replacementString, replacementOffset, replacementLength, image,
				displayString, relevance);
	}

	public FuzzyMachCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, Image image,
			StyledString displayString, int relevance) {
		super(replacementString, replacementOffset, replacementLength, image,
				displayString, relevance);
	}

	public Integer getTextStartPos() {
		return textStartPos;
	}

	public void setTextStartPos(Integer textStartPos) {
		this.textStartPos = textStartPos;
	}

	public FuzzyMachCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, Image image,
			StyledString displayString, int relevance, boolean inJavadoc) {
		super(replacementString, replacementOffset, replacementLength, image,
				displayString, relevance, inJavadoc);
	}

	public FuzzyMachCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, Image image,
			StyledString displayString, int relevance, boolean inJavadoc,
			JavaContentAssistInvocationContext invocationContext) {
		super(replacementString, replacementOffset, replacementLength, image,
				displayString, relevance, inJavadoc, invocationContext);
	}

	protected boolean isPrefix(String prefix, String string) {
	        if (originalRevelance == -1) {
				originalRevelance = getRelevance();
			}
			
			if (prefix == null || string == null || prefix.length() > string.length())
				return false;
			
			matchPositions.clear();
			if (getDisplayString() != null) {
				super.getStyledDisplayString().setStyle(0, getDisplayString().length(), NO_MATCH_COLOR);
			}
			
			string = Pattern.compile("[(:-].*").matcher(string).replaceFirst("");
			
			// perfect match
			if (string.startsWith(prefix)) {
				matchPositions.put(0, prefix.length());
				setRelevance(originalRevelance + PERFECT_PREFIX_BONUS*prefix.length() + PREFIX_BONUS);
				
				return true;
			}
			
			if (string.toLowerCase().startsWith(prefix)) {
				matchPositions.put(0, prefix.length());
				setRelevance(originalRevelance + PREFIX_BONUS);
				
				return true;
			}
			
			string = string.toLowerCase();
			prefix = prefix.toLowerCase();
			
			int lastMatchPosition = -1;
			int lastMatchStartPos = -1;
			int endBoundry = -1;
			for (int startBoundry = 0; startBoundry < prefix.length(); startBoundry++) {
				endBoundry = startBoundry + MATCHING_DEPTH > prefix.length() ? prefix.length() : startBoundry + MATCHING_DEPTH;
				while (endBoundry > startBoundry && lastMatchStartPos == -1) {
					lastMatchStartPos = string.indexOf(prefix.substring(startBoundry, endBoundry), lastMatchPosition);
					endBoundry--;
				}
				
				if (lastMatchStartPos == -1) {
					return false;
				}
				
				matchPositions.put(lastMatchStartPos, endBoundry - startBoundry + 1);
				lastMatchPosition = lastMatchStartPos + endBoundry - startBoundry + 1;
				
	//			setRelevance(originalRevelance + FUZZY_MATCH_BONUS*(endBoundry - startBoundry + 1) - (string.length() - lastMatchStartPos));
				
				startBoundry = endBoundry;
				lastMatchStartPos = -1;
			}
			
			setRelevance(originalRevelance + FUZZY_MATCH_BONUS*matchPositions.size() - (string.length() - lastMatchStartPos));
			
			return true;
		}

	public String getDisplayString() {
		return super.getDisplayString();
	}

	@Override
	public StyledString getStyledDisplayString() {
		if (matchPositions.isEmpty() || super.getStyledDisplayString() == null) {
			super.getStyledDisplayString().setStyle(0, 1, MATCH_COLOR);
			return super.getStyledDisplayString();
	    }
		
		for (Integer startPos : matchPositions.keySet()) {
			super.getStyledDisplayString().setStyle(textStartPos  + startPos, matchPositions.get(startPos), MATCH_COLOR);
		}
		
		return super.getStyledDisplayString();
	}

}