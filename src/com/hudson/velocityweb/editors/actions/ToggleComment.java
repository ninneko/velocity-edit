package com.hudson.velocityweb.editors.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.hudson.velocityweb.editors.velocity.VelocityEditor;
import com.hudson.velocityweb.editors.velocity.VelocityMultiPageEditor;

/**
 * @author xuanyin.zy E-mail:xuanyin.zy@taobao.com
 * @version 1.0
 * @since Aug 6, 2012 5:50:19 PM
 */
public class ToggleComment  implements IEditorActionDelegate {

	private VelocityEditor editor;
	
	public void setActiveEditor(IAction arg0, IEditorPart arg1) {
		if (editor != null) {
			return;
		}
		
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

	public void run (IAction action) {
		try {
			IAction toggleCommentAction = editor.getAction("Velocity.ToggleComment");
			toggleCommentAction.run();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction arg0, ISelection arg1) {
	}
}
