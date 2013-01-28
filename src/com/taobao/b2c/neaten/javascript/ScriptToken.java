package com.taobao.b2c.neaten.javascript;

import java.io.IOException;

import com.taobao.b2c.neaten.Node;
import com.taobao.b2c.neaten.DefaultToken;
import com.taobao.b2c.neaten.WorkingBufferSeeker;
import com.taobao.b2c.neaten.html.TagNode;
/**
 * @author xiaoxie
 */
public class ScriptToken extends DefaultToken {
	private static String[] END_DELIMITER =  {" ", "\n", "\r", "\t", "\f","\u200B",">"};
	public ScriptToken(){
		super(new String[]{"<script"},new String[]{"</script"});
	}
	//<script language="javascript" type="text/javascript"/>
	//<script language="javascript" type="text/javascript"></script>
//	public ScriptToken(String[] startDelimiter,String[] endDelimiter){
//		super( startDelimiter , endDelimiter );
//		
//	}
//	public ScriptToken(String startDelmiter, String endDelimiter) {
//		super(startDelmiter, endDelimiter);
//	}//注意bug 当前的delimieter记录问题
	 
	public Node seekNode(WorkingBufferSeeker wb) throws IOException{
		Node node = new TagNode();
		String sw = wb.startsWith(this.getStartDelimiter());
		//如果出现/>表示结束,否则出现>表示页面包含script
		//boolean srcipt = false;
		if(sw != null){
			wb.move(sw.length());
			//当前startDelimieter为sw
			//搜索节点
			while (!wb.isAllRead()) {
				
				//String end = ">";
				wb.readIfNeeded(1);
				if(wb.isToEnd(1)){
					wb.moveToEnd();
					wb.wrapNode(node,false);
					return node;
					//结束该循环
				}
				//表示页面包含script
				if(wb.startsWith(">")){
					//srcipt = true;
					wb.move(1);
					while (!wb.isAllRead()) {
					for (String value : this.getEndDelimiter()) {
						int valueLen = value.length();
						wb.readIfNeeded(valueLen);
						if(wb.isToEnd(valueLen)){
							wb.moveToEnd();
							 wb.wrapNode(node,false);
							return node;
							//结束该循环
						}
						if (wb.startsWith(value)) {
								wb.move(value.length());
								if(value.equals("</script")){
									//获取最后一个>
									 String rs = wb.startsWith(END_DELIMITER);
									 while(rs != null){
										wb.move(rs.length());
										if(!rs.equals(">")){
											rs = wb.startsWith(END_DELIMITER);
										}else{
											//return true;
											//node = wb.wrapNode(node,true);
											break;
										}
									}
								}
								//this.endDelimiter = value;
								 wb.wrapNode(node,true);
								//this.endDelimiter = value;
								
								return node;
						 
						}
					}
					wb.move();
					}
				}else{
					wb.readIfNeeded(2);
					if(wb.isToEnd(2)){
						wb.moveToEnd();
						wb.wrapNode(node,false);
						return node;
						//结束该循环
					}
					//如果出现/>表示结束
					if (wb.startsWith("/>")) {
						wb.move(2);
						wb.wrapNode(node,true);
						return node;
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
