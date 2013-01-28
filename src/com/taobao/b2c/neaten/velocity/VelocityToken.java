package com.taobao.b2c.neaten.velocity;

import java.io.IOException;

import com.taobao.b2c.neaten.DefaultToken;
import com.taobao.b2c.neaten.Node;
import com.taobao.b2c.neaten.WorkingBufferSeeker;

/**
 * @author xiaoxie
 */
public class VelocityToken extends DefaultToken {
	public VelocityToken(String[] startDelimiter, String[] endDelimiter) {
		super(startDelimiter, endDelimiter);
	}

	public Node seekNode(WorkingBufferSeeker wb) throws IOException{
		Node node = createNodeInstance();
		String sw = wb.startsWith(this.getStartDelimiter());
		
		if(sw != null){
			wb.createContentNodes();
			wb.move(sw.length());
			if(wb.isAllRead()){
				 wb.wrapNode(node,true);
				 return node;
			}
			//当前startDelimieter为sw
			//搜索节点
			while (!wb.isAllRead()) {
				for (String value : this.getEndDelimiter()) {
					int valueLen = value.length();
					wb.readIfNeeded(valueLen);
					if(wb.isAllRead()){
						 wb.wrapNode(node,true);
						 return node;
					}
					if(wb.isToEnd(valueLen)){
						wb.moveToEnd();
						 wb.wrapNode(node,false);
						return node;
						//结束该循环
					}

					if (wb.startsWith(value)) {
							wb.move(value.length());
//							//获取最后一个>
//							 String rs = wb.startsWith(END_DELIMITER);
//							 while(rs != null){
//								wb.move(rs.length());
//								if(!rs.equals(">")){
//									rs = wb.startsWith(END_DELIMITER);
//								}else{
//									//return true;
//									//node = wb.wrapNode(node,true);
//									break;
//								}
//							}
							//this.endDelimiter = value;
							 wb.wrapNode(node,true);
							//this.endDelimiter = value;
							
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
