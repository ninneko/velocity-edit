package com.hudson.velocityweb.editors.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.hudson.velocityweb.editors.velocity.VelocityEditor;
import com.hudson.velocityweb.editors.velocity.VelocityMultiPageEditor;
import com.taobao.b2c.neaten.NeatenCleaner;
import com.taobao.b2c.neaten.Render;
import com.taobao.b2c.neaten.html.HtmlArea;
import com.taobao.b2c.neaten.javascript.JavaScriptArea;
import com.taobao.b2c.neaten.velocity.VelocityArea;

/**
 * @author xiaoxie
 * @author xuanyin.zy
 */
public class FormatCode implements IEditorActionDelegate {
	private static final String DEFAULT_INDENT = "    ";

	private VelocityEditor editor;

	public void dispose() {
		// nothing to do
	}

	public void init(IWorkbenchWindow window) {
		// nothing to do
	}

	public void run(IAction action) {
		try {
			IDocument doc = editor.getDocumentProvider().getDocument(
					editor.getEditorInput());
			
			if (doc != null) {
				String docs = doc.get();
				doc.set(formatSource(docs));
			}
		} catch (Exception e) {
		}
	}

	private String formatSource(String htmlContent) throws Exception {
		NeatenCleaner nc = new NeatenCleaner(htmlContent);
		nc.addArea(new JavaScriptArea());
		nc.addArea(new HtmlArea());
		nc.addArea(new VelocityArea());

		nc.setOmitFormField(false);

		nc.clean();

		Render render = (Render) Class.forName(
				"com.taobao.b2c.neaten.HtmlPrettyRender2").newInstance();
		render.setProperty("indent", DEFAULT_INDENT);
		nc.setRender(render);
		
		StringBuffer bos = new StringBuffer();
		nc.write(bos);

		return bos.toString();
	}

	@Override
	public void setActiveEditor(IAction arg0, IEditorPart arg1) {
		// if (editor != null) {
		// return;
		// }

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
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}