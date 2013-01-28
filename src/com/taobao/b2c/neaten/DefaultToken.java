package com.taobao.b2c.neaten;

import java.io.IOException;

import com.taobao.b2c.neaten.html.TagNode;

/**
 * @author xiaoxie
 * @create time：2008-5-8 下午03:34:03
 * @description 默认提供两种类型的token匹配,起止符匹配 单一字符串匹配
 */
public class DefaultToken implements Token {
	public static final String[] WHITESPACE = {" ", "\n", "\r", "\t", "\f","\u200B" }; 

	private String startDelimiter[];
	private String endDelimiter[];
	private String endDelimiterPartner = null;
	private int partnerLoopCount = 0;			//表示至少有一个终结符,否则就是全部内容作为tag
	private int defaultParnerLoopCount = 0;
	private Class<? extends Node> nodeClzz;
	protected Node createNodeInstance(){
		try {
			if(nodeClzz == null){
				nodeClzz = TagNode.class;
			}
			return (Node)nodeClzz.newInstance();
		} catch (InstantiationException e) {
		 
			e.printStackTrace();
		} catch (IllegalAccessException e) {
	 
			e.printStackTrace();
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see com.taobao.b2c.neaten.IToken#seekNode(com.taobao.b2c.neaten.WorkingBufferSeeker)
	 */
	public Node seekNode(WorkingBufferSeeker wb ) throws IOException{
		Node node = createNodeInstance();//new TagNode();
		String sw = wb.startsWith(startDelimiter);
	
		if(sw != null){
			//获取内容节点
			wb.createContentNodes();
			 
			wb.move(sw.length());
			if(this.endDelimiter == null){
				 wb.wrapNode(node,true);
				 node.setStartDelimiter(sw);
				 return node;
			}
			//当前startDelimieter为sw
			//搜索节点
			while (!wb.isAllRead()) {
				for (String value : endDelimiter) {
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
								//this.endDelimiter = value;
								node.setEndDelimiter(sw);
								return node;
							}
						}else{
							
							wb.move(value.length());
							//this.endDelimiter = value;
							 wb.wrapNode(node,true);
							node.setEndDelimiter(sw);
							//this.endDelimiter = value;
							
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
	/* (non-Javadoc)
	 * @see com.taobao.b2c.neaten.IToken#init()
	 */
	public void init(){
		partnerLoopCount = defaultParnerLoopCount;
	}
	public void increasePartnerLoopCount(){
		partnerLoopCount ++;
	}
	public void reducePartnerLoopCount(){
		partnerLoopCount --;
	}
	
	public String getEndDelimiterPartner() {
		return endDelimiterPartner;
	}

	public boolean isNeedDealEndDelimiterPartner(){
		if(endDelimiterPartner == null){
			return false;
		}else{
			return true;
		}
	}
	
	public void setEndDelimiterPartner(String endDelimiterPartner) {
		this.endDelimiterPartner = endDelimiterPartner;
	}


	public int getPartnerLoopCount() {
		return partnerLoopCount;
	}


	public void setPartnerLoopCount(int partnerLoopCount) {
		this.partnerLoopCount = partnerLoopCount;
	}


	public DefaultToken(String startDelmiter, String endDelimiter) {
		this.startDelimiter = new String[]{startDelmiter};
	 
		this.endDelimiter = new String[]{endDelimiter};
 
	}
 

	public DefaultToken(String[] startDelimiter, String[] endDelimiter) {
		this(startDelimiter,endDelimiter,null);
		 
	}

	public DefaultToken(String[] startDelimiter) {
		this(startDelimiter,null,null);
		 
	}
 
	public DefaultToken(String  startDelimiter) {
		this(new String[]{startDelimiter});
		 
	}
	public DefaultToken(String[] startDelimiter, String[] endDelimiter,String endDelimiterPartner) {

		this(startDelimiter,endDelimiter,endDelimiterPartner,0);
	}
 
	public DefaultToken(String[] startDelimiter, String[] endDelimiter,String endDelimiterPartner,int partnerLoopCount) {
		this.startDelimiter = startDelimiter;
		this.endDelimiter = endDelimiter;
		this.endDelimiterPartner = endDelimiterPartner;
		this.defaultParnerLoopCount = partnerLoopCount;
		//this.nodeClzz = nodeClzz;
	}
	public boolean validate() {
		return (startDelimiter != null && startDelimiter.length > 0);
	}

	public String[] getStartDelimiter() {
		return startDelimiter;
	}

	public void setStartDelimiter(String[] startDelimiter) {
		this.startDelimiter = startDelimiter;
	}

	public String[] getEndDelimiter() {
		return endDelimiter;
	}

	public void setEndDelimiter(String[] endDelimiter) {
		this.endDelimiter = endDelimiter;
	}
	public Class<? extends Node> getNodeClzz() {
		return nodeClzz;
	}
	public Token setNodeClzz(Class<? extends Node> nodeClzz) {
		this.nodeClzz = nodeClzz;
		return this;
	}
	public int getDefaultParnerLoopCount() {
		return defaultParnerLoopCount;
	}
	public void setDefaultParnerLoopCount(int defaultParnerLoopCount) {
		this.defaultParnerLoopCount = defaultParnerLoopCount;
	}
 
}
