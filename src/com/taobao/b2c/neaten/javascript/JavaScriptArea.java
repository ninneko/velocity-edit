package com.taobao.b2c.neaten.javascript;

import java.io.IOException;

import com.taobao.b2c.neaten.Area;
import com.taobao.b2c.neaten.NodeList;
import com.taobao.b2c.neaten.WorkingBufferSeeker;

/**
 * @author xiaoxie
 */
public class JavaScriptArea extends Area {
	boolean isSetup = false;
	public JavaScriptArea() {
		this("JAVASCRIPT-AREA");
	}
	public JavaScriptArea(String name) {
		super(name);

		
	}
	public boolean start(WorkingBufferSeeker wb,NodeList tlist) throws IOException {
		return super.start(wb,tlist);
	}
	@Override
	public void setup() {
		if(!isSetup){
			super.addTokenizer(new JavaScriptTokenizer(new ScriptToken().setNodeClzz(JavaScriptNode.class))
					.setSubArea(new SubScriptArea()));
			isSetup = true;
		} 
		
	}
	
}
