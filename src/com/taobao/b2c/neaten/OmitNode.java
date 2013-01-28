package com.taobao.b2c.neaten;
/**
 * <p>the node which should be omited,like \"..\" , \'..\'</p>
 * @author xiaoxie
 */
public class OmitNode extends BaseNode {
	OmitNode(){
		this.setName("OMIT-NODE");
	}
	public String getFormatType() {
		return Node.FORMAT_TYPE_INTACT;
	}

}
