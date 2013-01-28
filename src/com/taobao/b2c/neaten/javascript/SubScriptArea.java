package com.taobao.b2c.neaten.javascript;

import com.taobao.b2c.neaten.Area;
import com.taobao.b2c.neaten.DefaultToken;
import com.taobao.b2c.neaten.OmitToken;
import com.taobao.b2c.neaten.OnePointToken;
import com.taobao.b2c.neaten.Token;
import com.taobao.b2c.neaten.html.HtmlTagNode;
import com.taobao.b2c.neaten.velocity.VelocityCommentNode;
import com.taobao.b2c.neaten.velocity.VelocityNode;
import com.taobao.b2c.neaten.velocity.VelocityTokenizer;
/**
 * @author xiaoxie
 */
public class SubScriptArea extends Area {
	private static final String[] ENDDELIMITER_BRACKET = {")"};
	boolean isSetup = false;
	public SubScriptArea(String name) {
		super(name);
		super.setConentNodeClzz(JavaScriptNode.class);
	}
	public SubScriptArea(){
		this("SUB-SCRIPT-AREA");
	}
	@Override
	public void setup() {

		if(!isSetup){
 		//处理在js中嵌套了vm脚本的情况
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
 
 		
 		
 		
 		
 		//js脚本
 		super.addTokenizer(new SubScriptTokenizer(new DefaultToken(new String[]{"<script"},new String[]{">"}).setNodeClzz(HtmlTagNode.class)));
 		super.addTokenizer(new SubScriptTokenizer(new DefaultToken(new String[]{"</script"},new String[]{">"}).setNodeClzz(HtmlTagNode.class)));
 		// //---<script>,\n
 		super.addTokenizer(new SubScriptTokenizer(new JavaScriptCommentToken().setNodeClzz(JavaScriptCommentNode.class)));
 		//FIXME
 		super.addTokenizer(new SubScriptTokenizer(new DefaultToken(new String[]{"<!--"},new String[]{"\n"}).setNodeClzz(JavaScriptCommentNode.class)));
 		
 		super.addTokenizer(new SubScriptTokenizer(new DefaultToken(new String[]{"/*"},new String[]{"*/"}).setNodeClzz(JavaScriptCommentNode.class)));
 		super.addTokenizer(new SubScriptTokenizer(new OmitToken(new String[]{"\""},new String[]{"\""})));
 		super.addTokenizer(new SubScriptTokenizer(new OmitToken(new String[]{"\'"},new String[]{"\'"})));
 	 	  		
 		super.addTokenizer(new SubScriptTokenizer(new FunctionToken("if",")").setNodeClzz(JavaScriptNode.class)));
 		//super.addTokenizer(new SubScriptTokenizer(new OnePointToken(" else ").setNodeClzz(JavaScriptNode.class)));
 		//super.addTokenizer(new SubScriptTokenizer(new OnePointToken(" else ").setNodeClzz(JavaScriptNode.class)));
 		super.addTokenizer(new SubScriptTokenizer(new OnePointToken("{").setNodeClzz(JavaScriptNode.class)));
 		super.addTokenizer(new SubScriptTokenizer(new OnePointToken("}").setNodeClzz(JavaScriptNode.class)));
 		super.addTokenizer(new SubScriptTokenizer(new FunctionToken("for",")").setNodeClzz(JavaScriptNode.class)));
		isSetup = true;
		}
		
	}
}
