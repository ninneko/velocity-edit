package com.taobao.b2c.neaten;

import java.io.IOException;

public class OmitToken implements Token {

	public static final String[] WHITESPACE = {" ", "\n", "\r", "\t", "\f","\u200B" }; 

	private String startDelimiter[];
	private String endDelimiter[];
	private String endDelimiterPartner = null;
	private int partnerLoopCount = 0;			//表示至少有一个终结符,否则就是全部内容作为tag
	private int defaultParnerLoopCount = 0;
 
	/* (non-Javadoc)
	 * @see com.taobao.b2c.neaten.IToken#seekNode(com.taobao.b2c.neaten.WorkingBufferSeeker)
	 */
	public Node seekNode(WorkingBufferSeeker wb ) throws IOException{
		 Node node = new OmitNode();
		String sw = wb.startsWith(startDelimiter);
	
		if(sw != null){

			wb.move(sw.length());
			if(this.endDelimiter == null){
				 return node;
			}
			//搜索节点
			while (!wb.isAllRead()) {
				for (String value : endDelimiter) {
					int valueLen = value.length();
					wb.readIfNeeded(valueLen);
					if(wb.isToEnd(valueLen)){
						wb.moveToEnd();
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
								return node;
							}
						}else{
							
							wb.move(value.length());
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


	public OmitToken(String startDelmiter, String endDelimiter) {
		this.startDelimiter = new String[]{startDelmiter};
	 
		this.endDelimiter = new String[]{endDelimiter};
 
	}
 

	public OmitToken(String[] startDelimiter, String[] endDelimiter) {
		this(startDelimiter,endDelimiter,null);
		 
	}

	public OmitToken(String[] startDelimiter) {
		this(startDelimiter,null,null);
		 
	}
 
	public OmitToken(String  startDelimiter) {
		this(new String[]{startDelimiter});
		 
	}
	public OmitToken(String[] startDelimiter, String[] endDelimiter,String endDelimiterPartner) {

		this(startDelimiter,endDelimiter,endDelimiterPartner,0);
	}
 
	public OmitToken(String[] startDelimiter, String[] endDelimiter,String endDelimiterPartner,int partnerLoopCount) {
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
 

}
