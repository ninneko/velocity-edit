package com.taobao.b2c.neaten.html;

import com.taobao.b2c.neaten.Area;
import com.taobao.b2c.neaten.DefaultToken;
import com.taobao.b2c.neaten.Token;
import com.taobao.b2c.neaten.javascript.SubScriptArea;

/**
 * @author xiaoxie
 * @create time：2008-5-8 下午03:30:51
 * @description
 */
public class HtmlArea extends Area {
	boolean isSetup = false;
	public HtmlArea() {
		this("HTML-AREA");
	}

	public HtmlArea(String name) {
		super(name);

	}

	@Override
	public void setup() {
		if(!isSetup){

		Token token8 = new DefaultToken(new String[] { "<" },
				new String[] { ">" }, "<", 1).setNodeClzz(HtmlTagNode.class);

		super.addTokenizer(new HtmlTokenizer(token8).setSubArea(new SubScriptArea()));
		//html 中不不能忽略' " 符号，如果忽略导致<a >x'</a>无法被解析
// 		super.addTokenizer(new HtmlTokenizer(new OmitToken(new String[]{"\""},new String[]{"\""})));
// 		super.addTokenizer(new HtmlTokenizer(new OmitToken(new String[]{"\'"},new String[]{"\'"})));
	 	  
		isSetup = true;
		}

	}

}
