package com.taobao.b2c.neaten;
/**
 * @author xiaoxie
 */
public interface Render {
	String render(Node node);
	String getProperty(String key);
	void setProperty(String key,String value);
	
}
