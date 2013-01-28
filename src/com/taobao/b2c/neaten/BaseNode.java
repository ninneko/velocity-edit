package com.taobao.b2c.neaten;

import java.util.ArrayList;
import java.util.List;

 

 

/**   
 * @author xiaoxie   
 * @create time：2008-5-9 下午03:26:28   
 * @description  the dafault implement of Node
 */
public abstract class BaseNode implements Node {
	private String startDelimiter;		//the node's start Delimiter
	private String endDelimiter;		//the node's end Delimiter
	private String originalSource;		//the node's all content,example:<a href='' target=''>
	private boolean closed = false; 
	private Node parentNode;
	private Node nextNode;
	private Node previousNode;
	private List<Node> childrenNodes;
	private String indend = "";
	private boolean sourceClosed = true;//默认关闭,非关闭为特殊情况，在解析过程中指出
	private String name;				//the node's name,example:<a href='' target=''> the name is "a"
	private String formatType ;			//see Node.java
	private String contentType;			//see Node.java
	private Node closeNode;				//<a ..> the close Node is the nearest </a>
	private Node openNode;				//</a> the openNode is the front  nearest <a ..>
	private int line;					//line number of the source 
	public int getLine(){
		return line;
	}
	
	public void setLine(int line){
		this.line = line;
	}
	public Node getOpenNode() {
		return openNode;
	}
	
	public boolean isEmpty(){
		if(this.originalSource == null || this.originalSource.trim().equals("")){
			return true;
		}else{
			return false;
		}
	}

	public void setOpenNode(Node openNode) {
		this.openNode = openNode;
	}


	public Node getCloseNode() {
		return closeNode;
	}
	

	public void setCloseNode(Node closeNode) {
		this.closeNode = closeNode;
	}


	public String getContentType() {
		return contentType;
	}


	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	public String getFormatType() {
		return formatType;
	}


	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}


	public String getName() {
 
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}


	public String getOriginalSource() {
	 
		return originalSource;
	}

 
	public String toString(){
		return this.originalSource;
	}
	public String getStartDelimiter(){
		return this.startDelimiter;
	}
	
	public String getEndDelimiter(){
		return this.endDelimiter;
	}
	public void setStartDelimiter(String startDelimiter){
		this.startDelimiter = startDelimiter;
	}
	
	public void setEndDelimiter(String endDelimiter){
		this.endDelimiter = endDelimiter;
	}

 

	public Node getParentNode() {

		return parentNode;
	}

	public void setParentNode(Node node) {

		this.parentNode = node;
	}

	public Node getNextNode() {

		return nextNode;
	}

	public void setNextNode(Node node) {

		this.nextNode = node;
	}

	public Node getPreviousNode() {

		return previousNode;
	}

	public void setPreviousNode(Node node) {

		this.previousNode = node;
	}

	public List<Node> getChildrenNodes() {

		return childrenNodes;
	}

	public void setChildrenNodes(List<Node> children) {

		this.childrenNodes = children;
		
	}

	public void addChildrenNode(Node child) {
		
		if(childrenNodes == null){
			childrenNodes = new ArrayList<Node>();
		}
		childrenNodes.add(child);
		child.setParentNode(this);

	}

 

	public void setClosed(boolean status) {
		this.closed = status;
		
	}

	public boolean isClosed() {
		return this.closed;
	}

	public void setOriginalSource(String originalSource) {
		this.originalSource = originalSource;
		
	}


	public String getIndend() {

		return this.indend;
	}


	public boolean isSourceClosed() {
		return sourceClosed;
	}


	public void setSourceClosed(boolean sourceClosed) {
		this.sourceClosed = sourceClosed;
	}


	public void setIndend(String indend) {
		this.indend = indend;
	}

	public boolean isMatch(Node node) {
		 
		return false;
		
	}
 


 
}
