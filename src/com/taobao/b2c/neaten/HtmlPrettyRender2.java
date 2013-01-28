package com.taobao.b2c.neaten;

import java.util.Properties;

/**
 * <p>
 * Pretty Html Render - creates resulting Html with strict indenting lines.
 * </p>
 * <p>
 * Html格式输出Render,对统一行关闭的tag不做换行输出
 * </p>
 * <code>&lt;div&gt;&lt;div&gt;&lt;/div&gt;&lt;/div&gt;<code>
 * <p>output:<p>
 * <code>&lt;div&gt;&lt;div&gt;&lt;/div&gt;&lt;/div&gt;<code>
 * @author xiaoxie
 */

public class HtmlPrettyRender2 implements Render {
	private static final String SINGLE_ENTER = "\n";
	private static final String ENTER = "\r\n";
	private static final String INDENT = "	";
	private Node openCloseNode;

	public String render(Node node) {

		if (node instanceof OmitNode) {
			return "";
		}
		// 主要处理在同一行的时候不换行
		if (openCloseNode != null) {
			if (node.getOpenNode() == openCloseNode) {
				openCloseNode = null;
			}
			return node.getOriginalSource();
		}
		StringBuffer tmp = new StringBuffer();
		String source = node.getOriginalSource().trim();
		if ("".equals(source)) {
			return "";
		}
		String indent = getFullIndent(node);
		// if node is '{' then
		if ("{".equals(node.getName())&& Node.FORMAT_TYPE_OPEN.equals(node.getFormatType())) {
			tmp.append(source);
		} else if (Node.CONTENT_TYPE_COMMENT.equals(node.getContentType())) {
			//if node is comment then ...
			tmp.append(adjustCommentIndent(node.getOriginalSource(), indent));
		} else {
			// 如果source包含了回车"\r\n",那么对\r\n进行拆分
			String[] arraySource = source.split(SINGLE_ENTER);
			if (arraySource != null && arraySource.length > 0) {

				for (String s : arraySource) {
					s = s.trim();

					tmp.append(ENTER + indent + s);

				}
			}
		}
		if (openCloseNode == null && node.getCloseNode() != null && node.getLine() == node.getCloseNode().getLine()) {
			openCloseNode = node;

		}
		return tmp.toString();// + "["+node.getFormatType()+" "+node.getName()
								// + node.getContentType()+"]";
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
			if (indent_str == null) {
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

	private String adjustCommentIndent(String comment, String indent) {
		StringBuffer sb = new StringBuffer();
		int indentLen = indent.length() * 4;
		String[] arraySource = comment.split(SINGLE_ENTER);
		if (arraySource != null && arraySource.length > 0) {
			int spaceLen = -1;
			// 偏移
			int moveLen = 0;
	 
			for (int n = 0;n < arraySource.length; n ++ ) {
				String s = arraySource[n];
				if(n == 0){
					s = ENTER + indent + s.trim();
				}else
				// 计算空格的长度
				if (spaceLen == -1 && arraySource.length > 1 && n == 1) {
					spaceLen = 0;
					for (int i = 0; i < s.length(); i++) {
						if (s.charAt(i) == ' ') {
							spaceLen += 1;
						}else
						if (s.charAt(i) == '\t') {
							spaceLen += 4;
						}else{
							break;
						}
					}
					s = numToIndent(spaceLen) + s.trim();
					moveLen = spaceLen - indentLen;

				} else {
					int tmpLen = 0;
					for (int i = 0; i < s.length(); i++) {
						if (s.charAt(i) == ' ') {
							tmpLen += 1;
						}else
						if (s.charAt(i) == '\t') {
							tmpLen += 4;
						}else
							break;
					}
					s = numToIndent(tmpLen) + s.trim();
				}
				if(n != 0){
					if (moveLen > 0) {
						// remove spaceLen - indentLen
						sb.append(ENTER + removeIndent(moveLen, s));
					} else {
						sb.append(ENTER + numToIndent(moveLen) + s);
	
					}
				}else{
					sb.append(s);
				}

			}
		}
		return sb.toString();
	}

	private String removeIndent(int moveLen, String s) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != ' ') {
				if (moveLen <= i) {
					s = s.substring(moveLen);
				} else {
					s = s.substring(i);
				}
				break;
			}
		}
		return s;
	}

	public static void main(String[] args) {
		HtmlPrettyRender2 render = new HtmlPrettyRender2();
		System.out.println(render.adjustCommentIndent("#*xxxx\r\nxx   x\r\nxxxx*#", "\t\t"));
	}

	private String numToIndent(int num) {
		num = Math.abs(num);
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < num; i++) {
			buffer.append(' ');
		}
		return buffer.toString();
	}

}
