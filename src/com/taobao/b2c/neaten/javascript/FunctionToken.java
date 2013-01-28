package com.taobao.b2c.neaten.javascript;

import java.io.IOException;

import com.taobao.b2c.neaten.DefaultToken;
import com.taobao.b2c.neaten.Node;
import com.taobao.b2c.neaten.WorkingBufferSeeker;

/**   
 * @author xiaoxie   
 * @create time：2008-5-21 下午01:37:18   
 * @description  处理script等特殊情况,if(true)必须被识别为if条件,而var if不允许被识别
 * DefaultToken不能提供这个要求.只能识别开始和结束标记的token
 */
public class FunctionToken extends DefaultToken {
	private static String[] END_DELIMITER =  {" ","("};
	private String startDelimiterEndToken = "(";
	public FunctionToken(String startDelimiter,String endDelimieter) {
		super(startDelimiter,endDelimieter);
		super.setEndDelimiterPartner("(");
		super.setPartnerLoopCount(1);
		super.setDefaultParnerLoopCount(1);
	
		
	}
	public Node seekNode(WorkingBufferSeeker wb ) throws IOException{
		Node node = createNodeInstance();//new TagNode();
		String[] startDelimiter = this.getStartDelimiter();
		boolean sw = wb.startsWith(startDelimiter[0]);
		if(sw){
			sw = false;
			//创建临时内容
			wb.createTempContent();
			wb.move(startDelimiter[0].length());
			String rs = wb.startsWith(END_DELIMITER);
			 while(rs != null){
					wb.move(rs.length());
					if(!rs.equals(startDelimiterEndToken)){
						rs = wb.startsWith(END_DELIMITER);
					}else{
						sw = true;
						break;
					}
			}
			 //释放临时内容
			 if(!sw){
				 wb.cleanTempContent();
			 }
			
		}
		
		if(sw){
			//获取内容节点
			wb.createContentNodesByTempContent();
			 
			//wb.move(sw.length());
			if(this.getEndDelimiter() == null){
				 wb.wrapNode(node,true);
				 //node.setStartDelimiter("");
				 return node;
			}
			//当前startDelimieter为sw
			//搜索节点
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
					
					if(isNeedDealEndDelimiterPartner()){
						if(wb.startsWith(getEndDelimiterPartner())){
							this.increasePartnerLoopCount();
						}
					}
					if (wb.startsWith(value)) {
						if(isNeedDealEndDelimiterPartner()){
							this.reducePartnerLoopCount();
							if(this.getPartnerLoopCount() == 0){
								//找到最后的节点
								wb.move(value.length());
								wb.wrapNode(node,true);
								return node;
							}
						}else{
							
							wb.move(value.length());
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
