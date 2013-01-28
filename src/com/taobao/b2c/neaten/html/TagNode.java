package com.taobao.b2c.neaten.html;

import java.util.HashMap;
import java.util.Map;

import com.taobao.b2c.neaten.BaseNode;
import com.taobao.b2c.neaten.Node;

/**   
 * @author xiaoxie   
 * @create time：2008-5-9 下午05:44:09   
 * @description  
 */
public class TagNode extends BaseNode {
	public static Map<String,String> HTML_TOKEN = new HashMap<String,String>();
	static{
		HTML_TOKEN.put("br",Node.FORMAT_TYPE_INTACT);
		HTML_TOKEN.put("meta",Node.FORMAT_TYPE_INTACT);
		HTML_TOKEN.put("link",Node.FORMAT_TYPE_INTACT);
		HTML_TOKEN.put("p",Node.FORMAT_TYPE_INTACT);
		HTML_TOKEN.put("hr",Node.FORMAT_TYPE_INTACT);
		HTML_TOKEN.put("!--",Node.FORMAT_TYPE_INTACT);
		/**
		 * form
		 */
		HTML_TOKEN.put("input",Node.FORMAT_TYPE_INTACT);
		HTML_TOKEN.put("button",Node.FORMAT_TYPE_INTACT);
		//HTML_TOKEN.put("textarea",Node.FORMAT_TYPE_INTACT);
	 
		HTML_TOKEN.put("fieldset",Node.FORMAT_TYPE_INTACT);
		 
	}
	public TagNode(){
		
	}
	public TagNode(String tagBody){
		this(tagBody,true);
	}
	public TagNode(String tagSource,boolean closed){
		setOriginalSource(tagSource);
		super.setClosed(closed);
		
	}
	public void setOriginalSource(String source){
		super.setOriginalSource(source);
		parseName(source);

	}
	/**
	 * 解析tagname
	 * <code><html></code>:html
	 * <code><input/></code>:input
	 * <code></body></code>:body
	 * @param tagSource
	 */
	private void parseName(String tagSource) {
		tagSource = tagSource.toLowerCase().trim();
		//解析出tagName
		int pos = -1;
		String[] WHITESPACE = {" ", "\n", "\r", "\t", "\f","\u200B", ">" };
		for(String token : WHITESPACE){
			int tmpPos = tagSource.indexOf(token);
			if(pos == -1){
				pos = tmpPos;
			}else{
				if(tmpPos != -1 && tmpPos < pos){
					pos = tmpPos;
				}
			}
		}
	 
		if(pos != -1){
		 
				if(tagSource.startsWith("</"))
					this.setName(tagSource.substring(2,pos));
				 else
					 this.setName(tagSource.substring(1,pos));
		}
 
		//coments
		if(this.getName().startsWith("!--")){
			this.setName("!--");
		}
	
		
		if(getName() != null){
			//处理node的format类型
			//完整tag
			if(this.getOriginalSource().endsWith("/>") || isHtmlFullTag(this)){
				this.setFormatType(Node.FORMAT_TYPE_INTACT);
			}else
			if(this.getOriginalSource().startsWith("<?") && this.getOriginalSource().endsWith(">")){
				this.setFormatType(Node.FORMAT_TYPE_INTACT);
			}
			//关闭tag
			else if(this.getOriginalSource().startsWith("</")){
				this.setFormatType(Node.FORMAT_TYPE_CLOSE);
			}
			//开始tag
			else {
				this.setFormatType(Node.FORMAT_TYPE_OPEN);
			}
			//处理node的content类型
			this.setContentType(Node.CONTENT_TYPE_TAG);
			if(this.getName().equals("!--")){
				this.setContentType(Node.CONTENT_TYPE_COMMENT);
			}
		}
	}
	public String toString(){
		if(this.isClosed()){
			return ""+	super.toString()+"[T]";
		}else{
			return super.toString()+"[TC]";
		}
	}

 
 
	public boolean isMatch(Node node) {
		if(this.getName()  != null && node.getName() != null){
			if(this.getName().equals(node.getName())){
				return true;
			}
		}
		return false;
		
	}
	public boolean isHtmlFullTag(Node node){
		String type = HTML_TOKEN.get(node.getName());
		if(Node.FORMAT_TYPE_INTACT.equals(type))
		{
			return true;
		}else{
			return false;
		}
	}	
}
