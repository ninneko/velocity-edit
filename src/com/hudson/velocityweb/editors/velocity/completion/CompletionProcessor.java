package com.hudson.velocityweb.editors.velocity.completion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;

import com.hudson.velocityweb.Plugin;
import com.hudson.velocityweb.editors.velocity.Editor;
import com.hudson.velocityweb.editors.velocity.completion.xml.XMLCompletionProcessor;
import com.hudson.velocityweb.editors.velocity.parser.VelocityFile;
import com.hudson.velocityweb.editors.velocity.parser.VelocityMacro;
import com.hudson.velocityweb.editors.velocity.parser.VelocityMacroParser;
import com.hudson.velocityweb.manager.ConfigurationManager;

public class CompletionProcessor extends TemplateCompletionProcessor implements IContentAssistProcessor {

	private Editor editor;
	private XMLCompletionProcessor xmlCompletionProcessor;

	public CompletionProcessor(Editor editor) {
		this.editor = editor;
		this.xmlCompletionProcessor = new XMLCompletionProcessor(editor.getFile());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		try {
			IDocument doc = viewer.getDocument();
			List<Integer> typedOffsets = new ArrayList<Integer>();
			String[] categories = doc.getPositionCategories();

			for (int i = 0; i < categories.length; i++) {

				Position[] positions = doc.getPositions(categories[i]);
				for (int j = 0; j < positions.length; j++) {
					typedOffsets.add(new Integer(positions[j].getOffset()));
				}
			}
			Collections.sort(typedOffsets);

			Stack<IDirective> directiveStack = new Stack<IDirective>();
			List<IDirective> noStackDirectives = new ArrayList<IDirective>();
			ITypedRegion region = null;
			IDirective lastDirective = null;
			for (Iterator<Integer> i = typedOffsets.iterator(); i.hasNext();) {
				int tOffset = ((Integer) i.next()).intValue();
				if (tOffset > offset)
					break;
				region = doc.getPartition(tOffset);
				if (DirectiveFactory.isEndDirective(region.getType())) {
					// remove from the directiveStack
					if (directiveStack.size() > 0) {
						directiveStack.pop();
					}
				} else {
					IDirective directive = DirectiveFactory.getDirective(region.getType(), region, doc);
					if (null != directive) {
						if (directive.requiresEnd())
							directiveStack.push(directive);
						else {
							noStackDirectives.add(directive);

						}
						lastDirective = directive;
					}
				}
			}
			
			List<ICompletionProposal> proposals = null;
			Map variableAdditions = new HashMap();
			for (Iterator<IDirective> i = noStackDirectives.iterator(); i.hasNext();) {
				IDirective directive = (IDirective) i.next();
				if (offset > directive.getOffset() + directive.getLength()) {
					directive.addVariableAdditions(editor.getFile(), Thread.currentThread().getContextClassLoader(),
							variableAdditions);
				}
			}
			
			for (Iterator i = directiveStack.iterator(); i.hasNext();) {
				IDirective directive = (IDirective) i.next();
				if (offset > directive.getOffset() + directive.getLength()) {
					directive.addVariableAdditions(editor.getFile(), Thread.currentThread().getContextClassLoader(),
							variableAdditions);
				}
			}
			
			if (null != lastDirective && lastDirective.isCursorInDirective(offset)) {
				proposals = lastDirective.getCompletionProposals(editor.getFile(), offset, variableAdditions, Thread
						.currentThread().getContextClassLoader());
				proposals = AbstractDirective.getCompletionProposals(editor.getFile(), doc, offset, variableAdditions,
						Thread.currentThread().getContextClassLoader(), false);
			} else {
				// process completions for the variable methods and properties
				proposals = AbstractDirective.getCompletionProposals(editor.getFile(), doc, offset, variableAdditions,
						Thread.currentThread().getContextClassLoader(), false);
			}

			if (null == proposals)
				proposals = new ArrayList();
			if (isDirectiveCommand(viewer.getDocument(), offset)) {
				proposals.addAll(getVelocityDirectiveProposals(editor.getFile(), viewer.getDocument(), offset));
			} else {
				ICompletionProposal[] computeCompletionProposals = xmlCompletionProcessor.computeCompletionProposals(
						viewer, offset, editor.getFile());
				if (computeCompletionProposals != null) {
					proposals.addAll(Arrays.asList(computeCompletionProposals));
				}
			}

			proposals.addAll(getWordCompletionProposals(editor.getFile(), doc, offset));

			Collections.sort(proposals, new CompletionProposalComparator());
			return proposals.toArray(new ICompletionProposal[proposals.size()]);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	private Collection<ICompletionProposal> getWordCompletionProposals(IFile file, IDocument doc, int offset) {
		int startPos = offset;
		try {
			while (Character.isLetterOrDigit(doc.getChar(startPos - 1)))
				startPos--;
		} catch (BadLocationException e) {
			// do nothing
		}

		String prefix = StringUtils.EMPTY;
		try {
			prefix = doc.get(startPos, offset - startPos);
		} catch (BadLocationException e) {
			// do nothing
		}

		return WordCompleteEngine.getInstance().computeProposals(file, doc, prefix, offset, startPos);

		// List<ICompletionProposal> proposals = new
		// ArrayList<ICompletionProposal>();
		// for (String string : suggestStrings) {
		// proposals.add(new CompletionProposal(
		// string,
		// startPos,
		// offset - startPos,
		// string.length(),
		// Plugin.getDefault().getImage("wc"), string, null, null));
		// }
		//
		// return proposals;
	}

	public boolean isDirectiveCommand(IDocument doc, int offset) {
		try {
			char c = doc.getChar(--offset);
			while (!Character.isWhitespace(c)) {
				if (c == '#')
					return true;
				c = doc.getChar(--offset);
			}
			return false;

		} catch (BadLocationException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private List<ICompletionProposal> getVelocityDirectiveProposals(IFile file, IDocument doc, int anOffset) {
		if (anOffset > 1) {
			try {
				if (doc.getChar(anOffset - 1) == '#') {
					char c = doc.getChar(anOffset - 2);
					if (c == '#' || c == '*')
						return Collections.EMPTY_LIST;
				}
			} catch (BadLocationException e) {
			}
		}
		
		try {
			List<VelocityFile> projectMacros = ConfigurationManager.getInstance(file.getProject()).getMacroFiles();
			List<VelocityFile> macroFiles = new ArrayList<VelocityFile>(projectMacros.size() + 1);

			// if the current file is macro library file, ignore
			for (VelocityFile velocityFile : projectMacros) {
				if (!velocityFile.file.getPath().equals(file.getLocation().toOSString())) {
					macroFiles.add(velocityFile);
				}
			}

			// macroFiles.addAll(macroFiles);

			addLocalVelocityMacros(doc, macroFiles, anOffset);

			String testPrefix = "";
			int start = -1;
			int end = -1;
			try {
				for (int i = anOffset - 1; i >= 0; i--) {
					char c = doc.getChar(i);
					if (c == '#') {
						start = i + 1;
						break;
					}
				}
			} catch (BadLocationException e) {
				start = anOffset;
			}

			try {
				int textEnd = -1;
				int stackSize = 0;
				for (int i = anOffset; i < doc.getLength(); i++) {
					char c = doc.getChar(i);
					if (c == '(') {
						stackSize++;
						if (textEnd == -1)
							textEnd = i;
					} else if (c == ')') {
						if (stackSize == 1) {
							end = i + 1;
							break;
						}
						stackSize--;
					} else if (c == '\n') {
						end = anOffset;
						if (textEnd == -1)
							textEnd = i;
						break;
					} else if (Character.isWhitespace(c)) {
						if (textEnd == -1)
							textEnd = i;
					} else
						break;
				}
				if (textEnd == -1)
					textEnd = anOffset;
				if (textEnd > start) {
					if (textEnd > anOffset)
						testPrefix = doc.get(start, anOffset - start);
					else
						testPrefix = doc.get(start, textEnd - start);
					if (end < textEnd)
						end = textEnd;
				}
			} catch (BadLocationException e) {
				end = anOffset;
			}
			
			if (end == -1)
				end = anOffset;
			if (start == -1)
				start = anOffset;
			
			testPrefix = testPrefix.toUpperCase();

			List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

			proposals.addAll(buildMacroProposals(macroFiles, testPrefix, start, end));
			proposals.addAll(buildKeywordsProposals(testPrefix, start, end));

			Collections.sort(proposals, PROPOSAL_COMPARATOR);
			return proposals;
		} catch (Exception e) {
			return null;
		}
	}

	private List<ICompletionProposal> buildMacroProposals(List<VelocityFile> macroFiles, String testPrefix, int start, int end) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		
		for (Iterator<VelocityFile> i = macroFiles.iterator(); i.hasNext();) {
			VelocityFile macroFile = (VelocityFile) i.next();
			for (int j = 0; j < macroFile.getMacros().length; j++) {
				VelocityMacro macro = macroFile.getMacros()[j];
				String name = macro.name.toUpperCase();
				if (name.startsWith(testPrefix)) {
					StringBuffer insertString = new StringBuffer();
					insertString.append(macro.name);
					insertString.append("(");
					
					int cursorPosition = insertString.length();
					for (int k = 0; k < macro.parameters.length; k++) {
						if (k > 0)
							insertString.append(" ");
						insertString.append(macro.parameters[k]);
					}
					insertString.append(")");

					String displayString = insertString.toString() + " - " + macroFile.file.getName();
					
//					StringBuffer buffer = new StringBuffer();
//					buffer.append(macro.name);
//					buffer.append('(');
//
//					if (macro.parameters.length == 0) {
//						buffer.append(')');
//					} else {
//						for (int k = 0; k < macro.parameters.length; k++) {
//							buffer.append(macro.parameters[k]);
//							if (k < (macro.parameters.length - 1)) {
//								buffer.append(" ");
//							}
//						}
//
//						buffer.append(')');
//						if (vf.file != null) {
//							buffer.append(" - ");
//							buffer.append(vf.file.getName());
//						}
//					}

					Position position = new Position(start, end - start);
					proposals.add(new VelocityTemplateProposal(insertString.toString(), position,
							cursorPosition, Plugin.getDefault().getImage("macro"), displayString, null,
							null, null, macro));
				}
			}
		}
		
		return proposals;
	}

	private List<ICompletionProposal> buildKeywordsProposals(String testPrefix, int start, int end) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		
		String[] directives = { "foreach", "if", "else", "end", "macro", "parse", "include", "stop", "elseif" };
		for (int i = 0; i < directives.length; i++) {
			if (directives[i].toUpperCase().startsWith(testPrefix)) {
				if (directives[i].equals("else") || directives[i].equals("end") || directives[i].equals("stop")
						|| directives[i].equals("elseif"))
					proposals.add(new CompletionProposal(directives[i], start, end - start, directives[i].length(),
							Plugin.getDefault().getImage(directives[i]), directives[i], null, null));
				else
					proposals.add(new CompletionProposal(directives[i] + "()", start, end - start, directives[i]
							.length() + 1, Plugin.getDefault().getImage(directives[i]), directives[i], null, null));
			}
		}
		
		return proposals;
	}

	private void addLocalVelocityMacros(IDocument doc, List<VelocityFile> macroFiles, int anOffset) {
		try {
			VelocityMacro[] macros = VelocityMacroParser.parse(doc.get());
			if (macros.length > 0) {
				macroFiles.add(new VelocityFile(macros));
			}
		} catch (Exception e) {
		}
	}

	protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
		return null;
	}

	protected Image getImage(Template template) {
		return null;
	}

	protected Template[] getTemplates(String contextTypeId) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new String("QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm.$</#").toCharArray();
	}

	private static Comparator<Object> PROPOSAL_COMPARATOR = new Comparator<Object>() {
		public int compare(Object aProposal1, Object aProposal2) {
			String text1 = ((ICompletionProposal) aProposal1).getDisplayString();
			String text2 = ((ICompletionProposal) aProposal2).getDisplayString();

			return text1.compareTo(text2);
		}

		public boolean equals(Object aProposal) {
			return false;
		}
	};

	public class CompletionProposalComparator implements Comparator<Object> {

		public int compare(Object o1, Object o2) {
			if (o1 instanceof JavaCompletionProposal && o2 instanceof JavaCompletionProposal) {
				return ((JavaCompletionProposal) o2).getRelevance() - ((JavaCompletionProposal) o1).getRelevance();
			}

			if (null == o1)
				return -1;
			else if (null == o2)
				return 1;
			else
				return (((ICompletionProposal) o1).getDisplayString().compareTo(((ICompletionProposal) o2)
						.getDisplayString()));
		}
	}
}