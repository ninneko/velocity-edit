package com.taobao.b2c.neaten.velocity;

import java.io.IOException;

import com.taobao.b2c.neaten.BaseTokenizer;
import com.taobao.b2c.neaten.Node;
import com.taobao.b2c.neaten.NodeList;
import com.taobao.b2c.neaten.Token;
import com.taobao.b2c.neaten.WorkingBufferSeeker;
/**
 * @author xiaoxie
 */
public class VelocityTokenizer extends BaseTokenizer {

	public VelocityTokenizer(Token token) {
		super(token);
	}
	public Node seek(WorkingBufferSeeker wb, NodeList nodeList) throws IOException {
		Node node = super.seek(wb);
		//对node进行二次处理
		return node;
	}
}
