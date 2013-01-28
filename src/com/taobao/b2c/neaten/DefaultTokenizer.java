package com.taobao.b2c.neaten;



/**   
 * @author xiaoxie   
 * @create time：2008-5-8 下午03:33:09   
 * @description  标记解析器
 */
public class DefaultTokenizer extends BaseTokenizer {
	public DefaultTokenizer(DefaultToken token) {
		super(token);
	}
	//可以重载 public Node seek(WorkingBufferSeeker wb) 方法实现特殊的解析需要;
}
