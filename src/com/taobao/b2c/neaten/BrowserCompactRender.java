package com.taobao.b2c.neaten;
/**   
 * <p>
 *  Browser Compact Render - creates resulting Source by stripping whitespaces.
 * </p>
 * @author xiaoxie   
 * @create time：2008-5-22 上午09:32:04   
 */
public class BrowserCompactRender implements Render {
  
	public String render(Node node) {
		if(node == null || node.isEmpty() || Node.CONTENT_TYPE_COMMENT.equals(node.getContentType())){
			return "";
		}
		if(Node.CONTENT_TYPE_CODE.equals(node.getContentType())){
			return  cleanwhiteSpace(node.getOriginalSource()) + " " ;
		}
		String source = node.getOriginalSource();
		source = cleanComment(source);
		 
		//去除多余的whitespaces
		source = cleanwhiteSpace(source);
		return source;
	}
	/** 处理velocity ##的注释问题*/
	private String cleanComment(String source) {
		StringBuffer buffer = new StringBuffer();
		int begain = 0;
		int s = source.indexOf("##", begain);
		while (s != -1) {

			buffer.append(source.substring(begain, s));
			begain = s;
			int e = source.indexOf("\n", begain);
			if (e == -1) {
				e = source.indexOf("\r", begain);
				if (e == -1) {
					e = source.length();
				}
			}
			begain = e;
			s = source.indexOf("##", begain);

		}
		buffer.append(source.substring(begain, source.length()));
		return buffer.toString();
	}

	private String cleanwhiteSpace(String source){
		StringBuffer buffer = new StringBuffer();
		boolean hastwowhitespace = false;
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (!Character.isWhitespace(c)) {
				if (hastwowhitespace) {
					buffer.append(' ');
					hastwowhitespace = false;
				}
				buffer.append(c);
			} else {
				hastwowhitespace = true;
				if (source.length() == (i + 1)) {
					buffer.append(' ');
				}
			}
		}
		return buffer.toString();
	}
	
 
	public String getProperty(String key) {
		return null;
	}
	public void setProperty(String key, String value) {
	}
	
	public static void main(String[] args) {
		BrowserCompactRender r = new BrowserCompactRender();
		String[] a = {"<a ##>", "<a ",
					  "<a c=\"##sdfsf\n\">", "<a c=\"\n\">",
					  "<a ##b\n>", "<a \n>",
					  "<a ##b\r>", "<a \r>"};
		for (int i = 0; i < a.length; i += 2) {
			System.out.println(r.cleanComment(a[i]));
			System.out.println(a[i + 1].equals(r.cleanComment(a[i])));
		}
		
		
	}
}
