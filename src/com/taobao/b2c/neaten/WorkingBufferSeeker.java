package com.taobao.b2c.neaten;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

 

/**   
 * @author xiaoxie   
 * @create time：2008-5-8 下午02:44:58   
 */
public class WorkingBufferSeeker {
	private final static int WORKING_BUFFER_SIZE = 1024;

	public static final char[] WHITESPACE = {' ', '\n', '\r', '\t', '\f','\u200B' };	
	
	
	private char[] working = new char[WORKING_BUFFER_SIZE];
	//下标的位置
	private int pos;

	private int len = -1;

	private StringBuffer moveBuffer;

	private StringBuffer tokenBody;
	
	private BufferedReader reader;

	private DefaultToken saveToken;
	
	private Node contentNode;

	private StringBuffer tempContent;
	
	private List<Node> contentNodes = new ArrayList<Node>();
	
	private int line;
	
	
	private Class<? extends Node> contentNodeClzz;
	protected Node createNodeInstance(){
		try {
			if(contentNodeClzz == null){
				contentNodeClzz = ContentNode.class;
			}
			return (Node)contentNodeClzz.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public int getLine(){
		return line;
	}
	public WorkingBufferSeeker(Reader reader) {
		this.reader = new BufferedReader(reader);
		pos = WORKING_BUFFER_SIZE;

	}

	public void init() throws IOException {
		readIfNeeded(0);
	}
	public boolean isToEnd(int valueLen){
		return (len >= 0 && pos + valueLen > len)  ;
	}
	public void readIfNeeded(int neededChars) throws IOException {
		if (len == -1 && pos + neededChars >= WORKING_BUFFER_SIZE) {
			int numToCopy = WORKING_BUFFER_SIZE - pos;
			System.arraycopy(working, pos, working, 0, numToCopy);
			pos = 0;
			int expected = WORKING_BUFFER_SIZE - numToCopy;
			int size = 0;
			int charsRead = 0;
			int offset = numToCopy;
			do {
				charsRead = reader.read(working, offset, expected);
				if (charsRead >= 0) {
					size += charsRead;
					offset += charsRead;
					expected -= charsRead;
				}
			} while (charsRead >= 0 && expected > 0);

			if (expected > 0) {
				len = size + numToCopy;
			}

		}
	}

	//判断pos所在位置的working是否以value开头
	public boolean startsWith(String value) throws IOException {
		int valueLen = value.length();
		readIfNeeded(valueLen);
		if (len >= 0 && pos + valueLen > len) {
			return false;
		}

		for (int i = 0; i < valueLen; i++) {
			char ch1 = Character.toLowerCase(value.charAt(i));
			char ch2 = Character.toLowerCase(working[pos + i]);
			if (ch1 != ch2) {
				return false;
			}
		}
		return true;
	}


	/**
	 * 返回匹配的value关键字
	 * @param values
	 * @return
	 * @throws IOException
	 */
	public String startsWith(String[] values) throws IOException {
		for (String value : values) {
			if (startsWith(value)) {
				return value;
			}
		}
		return null;

	}
	/**
	 * match string by filter the white space
	 * <p>if match(from start to end),return the match string,then return null</p>
	 * @param start
	 * @param end
	 * @return
	 * @throws IOException
	 */
	public String startsWith(String start,String end) throws IOException{
		
		int valueLen = start.length();
		readIfNeeded(valueLen);
		if (len >= 0 && pos + valueLen > len) {
			return null;
		}
		StringBuffer buffer = null;
		for (int i = 0; i < valueLen; i++) {
			
			char ch1 = Character.toLowerCase(start.charAt(i));
			char ch2 = Character.toLowerCase(working[pos + i]);
			if (ch1 != ch2) {
				return null;
			}
			 
		}
		buffer = new StringBuffer();
		buffer.append(start);
		//start match then ? public static final String[] WHITESPACE = {" ", "\n", "\r", "\t", "\f","\u200B" };
		int cpos = valueLen;
		boolean skip = true;
		while(skip){
			readIfNeeded(1);
			if (len >= 0 && pos + cpos + 1 > len) {
				return null;
			}
			char ch2 = working[pos+cpos];
			for(int i = 0;i < WHITESPACE.length;i ++){
				 
				char ch1 = WHITESPACE[i];
				
				if(ch1 == ch2){
					cpos ++;
					buffer.append(ch1);
					skip = true;
					break;
				}else{
					if(i ==  WHITESPACE.length -1){
						skip = false;
					}
				}
			}
		}
		 
		
		int valueLen2 = end.length();
		readIfNeeded(cpos + valueLen2);
		if (len >= 0 && pos + cpos + valueLen2 > len) {
			return null;
		}
		for (int i = 0; i <  valueLen2; i++) {
			char ch1 = Character.toLowerCase(end.charAt(i));
			char ch2 = Character.toLowerCase(working[pos + cpos + i]);
			if (ch1 != ch2) {
				return null;
			} 
		}
		buffer.append(end);
		return buffer.toString();
	}
	public boolean isAllRead() {
		return len >= 0 && pos >= len;
	}

//	/**
//	 * 需要移动pos下标,pos停留在找到的位置
//	 * @param value
//	 * @return
//	 * @throws IOException
//	 */
//	private String seekValue(String value) throws IOException {
//		return seekValue(new String[]{value});
//	}

	public String seekValue(String[] values) throws IOException {
		while (!isAllRead()) {
			for (String value : values) {
				int valueLen = value.length();
				readIfNeeded(valueLen);
				if (len >= 0 && pos + valueLen > len) {
					//move(pos + valueLen - len);
					move(len - pos);
					return tokeTake();
				}
				if(saveToken.isNeedDealEndDelimiterPartner()){
					if(equals(this.saveToken.getEndDelimiterPartner())){
						this.saveToken.increasePartnerLoopCount();
					}
				}
				if (equals(value)) {
					if(saveToken.isNeedDealEndDelimiterPartner()){
						this.saveToken.reducePartnerLoopCount();
						if(this.saveToken.getPartnerLoopCount() == 0){
							move(value.length());
							return tokeTake();
						}
					}else{
						
						move(value.length());
						return tokeTake();
					}
				}
			}
			move();
		}
		 
		return tokeTake(false);
	}
	private String tokeTake(boolean closed){
		//		记录tokenbody
		this.tokenBody = this.moveBuffer;
		//重新清理moveBuffer
		this.moveBuffer = new StringBuffer();
		return tokenBody.toString();
	}
	private String tokeTake() {
		return tokeTake(true);
	}

	private boolean equals(String value) {
		int valueLen = value.length();
		for (int i = 0; i < valueLen; i++) {
			char ch1 = Character.toLowerCase(value.charAt(i));
			char ch2 = Character.toLowerCase(working[pos + i]);
			if (ch1 != ch2) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 移动pos一位
	 * @throws IOException
	 */
	public void move() throws IOException {
		move(1);
	}
	
	/**
	 * pos 移动 size的位置 ，同时记录moveBuffer
	 * @param size
	 * @throws IOException
	 */
	public void move(int size) throws IOException {
		if(!isAllRead()){
			if (moveBuffer == null) {
				moveBuffer = new StringBuffer();
			}
			for (int i = 0; i < size; i++) {
				char wk = working[pos + i];
				if(wk == '\n'){
					line ++;
				}
				moveBuffer.append(wk);
			}
			pos += size;
			readIfNeeded(size);
		}
	}
	public void moveToEnd() throws IOException{
		while(!isAllRead()){
			move();
		}
	}


//	private Node findNode(String[] values) throws IOException {
//		String content = seekValue(values);
//		if (content != null) {
//			TagNode node = new TagNode(content,this.tokenClosed);
//			if(endDelimiter != null){
//				node.setEndDelimiter(this.endDelimiter);
//			}
//			if(startDelimiter != null){
//				node.setStartDelimiter(this.startDelimiter);
//			}
//			this.endDelimiter = null;
//			this.startDelimiter = null;
//			return new TagNode(content,this.tokenClosed);
//		} else {
//			return null;
//		}
//	}

	public boolean defaultDelmiterSeek(String[] endDelimiter) throws IOException{
		while (!isAllRead()) {
			for (String value : endDelimiter) {
				int valueLen = value.length();
				readIfNeeded(valueLen);
				if (len >= 0 && pos + valueLen > len) {
					//move(pos + valueLen - len);
					move(len - pos);
					return false;
				}
				if(saveToken.isNeedDealEndDelimiterPartner()){
					if(equals(this.saveToken.getEndDelimiterPartner())){
						this.saveToken.increasePartnerLoopCount();
					}
				}
				if (equals(value)) {
					if(saveToken.isNeedDealEndDelimiterPartner()){
						this.saveToken.reducePartnerLoopCount();
						if(this.saveToken.getPartnerLoopCount() == 0){
							move(value.length());
							return true;
						}
					}else{
						
						move(value.length());
						return true;
					}
				}
			}
			move();
		}
		 
		return false;
	}


	public void wrapNode(Node node,boolean b) {
		

		if(this.moveBuffer != null && this.moveBuffer.length() > 0){
			node.setOriginalSource(this.moveBuffer.toString());
			node.setSourceClosed(b);
			node.setLine(this.getLine());
			cleanMoveBuffer();
		}

	}
	private Node wrapContentNode(String content){
		Node node = createNodeInstance();
		node.setLine(this.getLine());
		node.setOriginalSource(content);
		node.setSourceClosed(true);
		return node;
	}
 

	//内容是seek到node之前保留的字符
	public Node getContentNode() {
		
		return contentNode;
	}
	public Node createContentNode(){
		Node node = null;
		if(this.moveBuffer != null &&  this.moveBuffer.length() > 0){
			node = createNodeInstance();
			this.wrapNode(node,true);
			this.contentNode =  node;
		}
		return node;
	}
	/**
	 * 将所有的内容根据回车符来拆分,每一行作为一个独立的ConentNode
	 */
	public void createContentNodes(){
		
		
		if(this.moveBuffer != null &&  this.moveBuffer.length() > 0){
			String tmp = this.moveBuffer.toString();
			//String[] contents = tmp.split("\r\n");
			String[] contents = tmp.split("\n");
			for(int i = 0;i < contents.length; i ++){
				String content = contents[i];
				Node node = wrapContentNode(content);
				node.setLine(this.getLine() - (contents.length - i - 1));
				this.contentNodes.add(node);
				
			}
			//清理moveBuffer
			cleanMoveBuffer();
			 
		}
		 
	}
	public void createContentNodesByTempContent(){
		if(this.tempContent != null &&  this.tempContent.length() > 0){
			String tmp = this.tempContent.toString();
			//String[] contents = tmp.split("\r\n");
			String[] contents = tmp.split("\n");
			for(String content : contents){
				this.contentNodes.add(wrapContentNode(content));
			}
			//清理moveBuffer
			//cleanMoveBuffer();
			this.tempContent = null;
			 
		}
	}
	public void createTempContent(){
		this.tempContent = this.moveBuffer;
		cleanMoveBuffer();
	}
	public void cleanTempContent(){
		this.moveBuffer = this.tempContent.append(this.moveBuffer);
	}
	private void cleanMoveBuffer(){
		this.moveBuffer = new StringBuffer();
	}
	public List<Node> getContentNodes(){
		if(this.contentNodes.size() > 0){
			return this.contentNodes;
		}else{
			return null;
		}
		
	}
	public void cleanContentNode(){
		this.contentNode = null;
	}
	public void cleanContentNodes(){
		this.contentNodes.clear();
	}
	public Node getEndContentNode() {
		if (this.moveBuffer != null && this.moveBuffer.length() > 0) {
			return new ContentNode(this.moveBuffer.toString());
		} else {
			return null;
		}
	}

	public WorkingBufferSeeker setContentNodeClzz(Class<? extends Node> contentNodeClzz) {
		this.contentNodeClzz = contentNodeClzz;
		return this;
	}
	public static void main(String[] args) throws IOException{
		Reader reader = new StringReader("</script\r\n     >");
		WorkingBufferSeeker wb = new WorkingBufferSeeker(reader);
		System.out.print(wb.startsWith("</script", ">"));
	}
}
