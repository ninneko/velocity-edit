package com.taobao.b2c.neaten;

import java.util.Properties;

/**
 * <p>Pretty Html Render - creates resulting Html with strict indenting lines.</p>
 * <p>严格的HTML样式打印Render所有标签全部换行输出</p>
 * <code>&lt;div&gt;&lt;div&gt;&lt;/div&gt;&lt;/div&gt;<code>
 * <p>output:</p>
 * <code>
 * 	&lt;div&gt;<br>
 * 		&nbsp;&nbsp;&lt;div&gt;<br>
 * 		&nbsp;&nbsp;&lt;/div&gt;<br>
 * 	&lt;/div&gt;
 * </code>
 * @author xiaoxie
 */
public class HtmlPrettyRender implements Render {

	private static final String SINGLE_ENTER = "\n";
	private static final String ENTER = "\r\n";
	private static final String INDENT = "	";

	public String render(Node node) {
		if(node  instanceof OmitNode){
			return "";
		}
		StringBuffer tmp = new StringBuffer();
		String source = node.getOriginalSource().trim();
		if ("".equals(source)) {
			return "";
		}
		String[] arraySource = source.split(SINGLE_ENTER);
		if (arraySource != null && arraySource.length > 0) {
			String indent = getFullIndent(node);
			for (String s : arraySource) {
				s = s.trim();
				tmp.append(ENTER + indent + s);
			}
		}
		return tmp.toString();// + "["+node.getFormatType()+" "+node.getName() + node.getContentType()+"]";
	}

	private String getFullIndent(Node node) {

		int rank = 0;
		while (node.getParentNode() != null) {
			if ("html".equals(node.getName()) || "body".equals(node.getName())) {
				break;
			}
			rank++;
			node = node.getParentNode();
		}
		StringBuffer indent = new StringBuffer();
		for (int i = 0; i < rank; i++) {
			String indent_str = this.getProperty("indent");
			if(indent_str == null){
				indent_str = INDENT;
			}
			indent.append(indent_str);
		}
		return indent.toString();

	}

	private Properties property = new Properties();
	public String getProperty(String key) {
		return property.getProperty(key);
		 
	}

	public void setProperty(String key, String value) {
		property.put(key, value);
		
	}

}
