package com.taobao.b2c.neaten;

import java.io.IOException;

/**   
 * <p>根据绑定的Token,来进行Node的搜索//node的区分 tagNode->htmlTagNode</p>
 * @author xiaoxie   
 * @create time：2008-5-8 下午02:24:57   
 */
public interface Tokenizer {
	Token getToken();
	void setToken(DefaultToken token);
	Node seek(WorkingBufferSeeker wb) throws IOException;
	Area getSubArea();
	Tokenizer setSubArea(Area area);
}
