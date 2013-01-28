package com.taobao.b2c.neaten.javascript;

import com.taobao.b2c.neaten.BaseTokenizer;
import com.taobao.b2c.neaten.Token;
/**
 * @author xiaoxie
 */
public class SubScriptTokenizer extends BaseTokenizer {

	public SubScriptTokenizer(Token token) {
		super(token);
	}
//	public Node seek(WorkingBufferSeeker wb) throws IOException {
//		//check token validate
//		Node node = super.seek(wb);
//		if(node != null && !(node instanceof OmitNode) ){
		  
//			处理node的format类型
			//完整tag <script xx />
//			String source = node.getOriginalSource().trim();
//			if(source.startsWith("<") && source.endsWith(">")){
//				node.setContentType(Node.CONTENT_TYPE_TAG);
//				if(source.endsWith("/>") && source.startsWith("<") ){
//					 node.setFormatType(Node.FORMAT_TYPE_INTACT);
//					 
//				}
//	//			关闭tag </script>
//				
//				else if(source.startsWith("</") && source.endsWith(">")){
//					node.setFormatType(Node.FORMAT_TYPE_CLOSE);
//				}
//	//			开始tag <sript>
//				else {
//					node.setFormatType(Node.FORMAT_TYPE_OPEN);
//				}
//			}else{
				//code 和注释 和内容
	 
//				if(source.startsWith("/")){
//					 node.setName("//");
//					node.setFormatType(Node.FORMAT_TYPE_INTACT);
//					node.setContentType(Node.CONTENT_TYPE_COMMENT);
//				}else{
//					node.setContentType(Node.CONTENT_TYPE_CODE);
//				}
				 
				
		//	}
//		}
//		return node;
//	}
}
