package com.taobao.b2c.neaten;

import java.io.IOException;
/**
 * @author xiaoxie
 */
public interface Token {

	public abstract Node seekNode(WorkingBufferSeeker wb) throws IOException;

	public abstract void init();
	
	public boolean validate();

}