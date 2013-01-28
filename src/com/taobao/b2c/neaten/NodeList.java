package com.taobao.b2c.neaten;

import java.util.ArrayList;
import java.util.List;

/**   
 * <p>the container of the node,conatainer the result of neatencleaner.clean</p>
 * @author xiaoxie   
 * @create time£º2008-5-8 ÏÂÎç03:09:08   
 */
public class NodeList {
	private List<Node> nodeList = new ArrayList<Node>();
	public List<Node> getNodeList(){
		return nodeList;
	}
	public void addNode(Node node){
		nodeList.add(node);
	}
	public void addNodes(List<Node>  nodes){
		nodeList.addAll(nodes);
	}
	public void addNodeArray(Node[] nodes){
		if(nodes != null){
			for(Node node : nodes){
				addNode(node);
			}
		}
	}
}
