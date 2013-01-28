package com.taobao.b2c.neaten.javascript;

import java.io.IOException;

import com.taobao.b2c.neaten.DefaultToken;
import com.taobao.b2c.neaten.Node;
import com.taobao.b2c.neaten.WorkingBufferSeeker;
 

/**   
 * @author xiaoxie   
 * @create time：2008-5-22 上午11:47:52   
 * @description  处理//</script>的特殊情况
 */
public class JavaScriptCommentToken extends DefaultToken {

 
	//private static String[] END_DELIMITER =  {" ", "\n", "\r", "\t", "\f","\u200B",">"};
	public JavaScriptCommentToken(){
		super(new String[]{"//"},new String[]{"\n","</script"});
	}
	public JavaScriptCommentToken(String[] startDelimiter,String[] endDelimiter){
		super( startDelimiter , endDelimiter );
		
	}
	//修改startswitch方法，返回回匹配到的字符,如果没有匹配到则返回null
	public Node seekNode(WorkingBufferSeeker wb) throws IOException{
		Node node = createNodeInstance();
		String sw = wb.startsWith(this.getStartDelimiter());
		
		if(sw != null){
			//获取内容节点
			wb.createContentNodes();
			wb.move(sw.length());
			//当前startDelimieter为sw
			//搜索节点
			while (!wb.isAllRead()) {
				//getEndDelimiter必须有相等length
				for (String value : this.getEndDelimiter()) {
					int valueLen = value.length();
					wb.readIfNeeded(valueLen);
					if(wb.isToEnd(valueLen)){
						wb.moveToEnd();
						wb.wrapNode(node,false);
						return node;
						//结束该循环
					}
					if(value.equals("\n")){
						if (wb.startsWith(value)) {
								node.setEndDelimiter(value);
								wb.wrapNode(node,true);
	 
								return node;
						 
						}
					}
					if(value.equals("</script")){
						String ss = wb.startsWith(value,">");
						if (ss != null) {
							node.setEndDelimiter(value);
							wb.wrapNode(node,true);
 
							return node;
					 
						}
					}
				}
				wb.move();
			}
			 
		}else{
			 return null;
		}
		return null;
	}
}
