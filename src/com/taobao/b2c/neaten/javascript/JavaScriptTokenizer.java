package com.taobao.b2c.neaten.javascript;

import java.io.IOException;

import com.taobao.b2c.neaten.BaseTokenizer;
import com.taobao.b2c.neaten.Node;
import com.taobao.b2c.neaten.Token;
import com.taobao.b2c.neaten.WorkingBufferSeeker;
/**
 * @author xiaoxie
 */
public class JavaScriptTokenizer extends BaseTokenizer {

	public JavaScriptTokenizer(Token token) {
		super(token);
	}

	public Node seek(WorkingBufferSeeker wb) throws IOException {
		//check token validate
		Node node = super.seek(wb);
		if(node != null  ){
		  
//			处理node的format类型
			//完整tag <script xx />
			String source = node.getOriginalSource();
			if(source.endsWith("/>") && source.startsWith("<") ){
				 node.setFormatType(Node.FORMAT_TYPE_INTACT);
			}
			//开始tag <sript>
			else if(source.endsWith(">")){
				node.setFormatType(Node.FORMAT_TYPE_OPEN);
			}
			//关闭tag </script>
			else {
				node.setFormatType(Node.FORMAT_TYPE_CLOSE);
			}
		}
		return node;
	}

}
