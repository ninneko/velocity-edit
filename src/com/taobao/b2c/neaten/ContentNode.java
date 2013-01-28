package com.taobao.b2c.neaten;

 

/**   
 * @author xiaoxie   
 * @create time£º2008-5-9 ÉÏÎç11:49:06   
 * @description  
 */
public class ContentNode extends BaseNode {
	 
	public ContentNode(){
		this.setName("_NEATEN-CONTENT");
		this.setFormatType(Node.FORMAT_TYPE_CONTENT);
		this.setContentType(Node.CONTENT_TYPE_CONTENT);
	}
	public ContentNode(String body){
		super.setOriginalSource(body);
	}


 
}
