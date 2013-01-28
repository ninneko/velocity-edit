package com.taobao.b2c.neaten.velocity;

import com.taobao.b2c.neaten.Area;
import com.taobao.b2c.neaten.DefaultToken;
import com.taobao.b2c.neaten.OnePointToken;
import com.taobao.b2c.neaten.Token;

/**
 * @author xiaoxie
 */
public class VelocityArea extends Area {
boolean isSetup = false;
	private static final String[] ENDDELIMITER_BRACKET = {")"};
	public VelocityArea(){
		this("VELOCITY-AREA");
	}
	public VelocityArea(String name) {
		super(name);

 	}
	@Override
	public void setup() {
		if(!isSetup){
 		super.addTokenizer(new VelocityTokenizer(new DefaultToken(new String[]{"##"},new String[]{"\n"}).setNodeClzz(VelocityCommentNode.class)));
 		super.addTokenizer(new VelocityTokenizer(new DefaultToken(new String[]{"#*"},new String[]{"*#"}).setNodeClzz(VelocityCommentNode.class)));
		Token token1= new DefaultToken(new String[]{"#if"},ENDDELIMITER_BRACKET,"(").setNodeClzz(VelocityNode.class);
		super.addTokenizer(new VelocityTokenizer(token1));
 
		super.addTokenizer(new VelocityTokenizer(new DefaultToken(new String[]{"#macro"},ENDDELIMITER_BRACKET,"(").setNodeClzz(VelocityNode.class)));
		super.addTokenizer(new VelocityTokenizer(new DefaultToken(new String[]{"#elseif"},ENDDELIMITER_BRACKET,"(").setNodeClzz(VelocityNode.class)));
		super.addTokenizer(new VelocityTokenizer(new OnePointToken("#end").setNodeClzz(VelocityNode.class)));
		super.addTokenizer(new VelocityTokenizer(new OnePointToken("#else").setNodeClzz(VelocityNode.class)));
		super.addTokenizer(new VelocityTokenizer(new DefaultToken(new String[]{"#foreach"},ENDDELIMITER_BRACKET,"(").setNodeClzz(VelocityNode.class)));
		super.addTokenizer(new VelocityTokenizer(new DefaultToken(new String[]{"#set"},ENDDELIMITER_BRACKET,"(").setNodeClzz(VelocityNode.class)));
		isSetup = true;
		}
	}
}
