package com.hudson.velocityweb.editors.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.googlecode.veloeclipse.vaulttec.ui.editor.actions.Formatter;
import com.hudson.velocityweb.editors.velocity.VelocityEditor;
import com.hudson.velocityweb.editors.velocity.VelocityMultiPageEditor;

/**
 * @author xiaoxie
 * @author xuanyin.zy
 */
public class FormatCode implements IEditorActionDelegate {
	private Formatter formatter = new Formatter();
	
	private VelocityEditor editor;

	public void selectionChanged(IAction action, ISelection selection) {
		formatter.selectionChanged(action, selection);
	}

	@Override
	public void setActiveEditor(IAction arg0, IEditorPart arg1) {
		if (arg1 instanceof VelocityMultiPageEditor) {
			this.editor = ((VelocityMultiPageEditor) arg1).getEditor();
			arg0.setEnabled(true);

			return;
		}

		if (arg1 instanceof VelocityEditor) {
			this.editor = (VelocityEditor) arg1;
			arg0.setEnabled(true);

			return;
		}

		arg0.setEnabled(false);
	}

	@Override
	public void run(IAction action) {
		final IDocument document = editor.getDocumentProvider()
				.getDocument(editor.getEditorInput());

		formatter.format(document);
	}
}