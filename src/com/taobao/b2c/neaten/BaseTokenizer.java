package com.taobao.b2c.neaten;

import java.io.IOException;
/**
 * @author xiaoxie
 */
public abstract class BaseTokenizer implements Tokenizer {

	private Token token;	//标记
	private Area area;
	public BaseTokenizer(Token token){
		this.token = token;
	}
	/**
	 * 返回true,该tokenizer绑定的token,seek到相应的node
	 * 
	 */
	public Node seek(WorkingBufferSeeker wb) throws IOException {
		//check token validate
		if(token != null && token.validate()){
			token.init();
			Node  node = token.seekNode(wb);//wb.seekNode(token);
			if(node != null){
				node.setLine(wb.getLine());
			}
			return node;
		}
		return null;
	}
	public Token getToken() {
		return token;
	}
	public void setToken(DefaultToken token) {
		this.token = token;
	}
	public Area getSubArea() {
		return this.area;
	}
	public Tokenizer setSubArea(Area area) {
		this.area = area;
		return this;
	}

}
