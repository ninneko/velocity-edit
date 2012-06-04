package com.hudson.velocityweb.preferences;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.hudson.velocityweb.Plugin;
import com.hudson.velocityweb.editors.velocity.ColorManager;
import com.hudson.velocityweb.util.UIUtil;

/**
 * @author Joe Hudson
 * @author xuanyin.zy
 */
public class MainPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String AUTO_COMPLETE_DEPLAY = "auto_complete_delay";
	
	public static final String MACRO_TEMPLATE_PATH = "macro_template_path";
	
	public MainPreferences() {
		super(GRID);
		setMessage("All changes will be applied on new editors.");
		setImageDescriptor(null);
		setDescription("");
		setPreferenceStore(Plugin.getDefault().getPreferenceStore());
		initializeDefaults();
	}

	public void init(IWorkbench workbench) {
	}

	/**
	 * Initialize the default values
	 */
	private void initializeDefaults() {
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(new ColorFieldEditor(ColorManager.COLOR_FOREACH_DIRECTIVE,
				UIUtil.getResourceLabel("ForeachDirective"), getFieldEditorParent()));
		addField(new ColorFieldEditor(ColorManager.COLOR_IF_DIRECTIVE, UIUtil.getResourceLabel("IfDirective"),
				getFieldEditorParent()));
		addField(new ColorFieldEditor(ColorManager.COLOR_MACRO_DIRECTIVE, UIUtil.getResourceLabel("MacroDirective"),
				getFieldEditorParent()));
		addField(new ColorFieldEditor(ColorManager.COLOR_SET_DIRECTIVE, UIUtil.getResourceLabel("SetDirective"),
				getFieldEditorParent()));
		addField(new ColorFieldEditor(ColorManager.COLOR_VARIABLE, UIUtil.getResourceLabel("VelocityVariable"),
				getFieldEditorParent()));
		addField(new ColorFieldEditor(ColorManager.COLOR_COMMENT, UIUtil.getResourceLabel("VelocityComment"),
				getFieldEditorParent()));

		addField(new ColorFieldEditor(ColorManager.COLOR_PROC_INSTR, UIUtil.getResourceLabel("XMLInstruction"),
				getFieldEditorParent()));
		addField(new ColorFieldEditor(ColorManager.COLOR_STRING, UIUtil.getResourceLabel("XMLString"),
				getFieldEditorParent()));
		addField(new ColorFieldEditor(ColorManager.COLOR_TAG, UIUtil.getResourceLabel("XMLTag"), getFieldEditorParent()));
		addField(new ColorFieldEditor(ColorManager.COLOR_XML_COMMENT, UIUtil.getResourceLabel("XMLComment"),
				getFieldEditorParent()));
		{
			IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(AUTO_COMPLETE_DEPLAY, "Auto Completion Delay(ms):",
					getFieldEditorParent());
			integerFieldEditor.setStringValue("80");
			integerFieldEditor.setValidRange(0, 9999);
			addField(integerFieldEditor);
		}
		{
			StringFieldEditor stringFieldEditor = new StringFieldEditor(MACRO_TEMPLATE_PATH, "Macro Template Path:", 40, StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent());
			stringFieldEditor.setEmptyStringAllowed(false);
			stringFieldEditor.setStringValue("/src/main/webapp/home/templates/");
			addField(stringFieldEditor);
		}
	}

}