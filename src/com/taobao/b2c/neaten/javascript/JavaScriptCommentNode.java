package com.taobao.b2c.neaten.javascript;

import com.taobao.b2c.neaten.BaseNode;
/**
 * @author xiaoxie
 */
public class JavaScriptCommentNode extends BaseNode {
	public void setOriginalSource(String source){
		super.setOriginalSource(source);
		this.setName("//");
		this.setFormatType(FORMAT_TYPE_INTACT);
		this.setContentType(CONTENT_TYPE_COMMENT);
	}

 
}
