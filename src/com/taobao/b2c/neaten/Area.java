package com.taobao.b2c.neaten;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**   
 * <p>
 * <code>Area</code> contains many tokenizer,tokenizer returns <code>Node</code>,
 * <code>Area</code> add the <code>Node</code> to the <code>NodeList</code>
 * </p>
 * @author xiaoxie   
 * @create time：2008-5-8 下午02:10:56   
 */
public abstract class Area {
	private String name;
	private NeatenCleaner neatenCleaner;
	private List<Tokenizer> tokenizerList = new ArrayList<Tokenizer>();
	private Class<? extends Node> conentNodeClzz = ContentNode.class;

	public Area(String name){
		this.name = name;
	}
	public Area addTokenizer(Tokenizer tokenizer){
		tokenizerList.add(tokenizer);
		return this;
	}
	abstract public void setup();
	
	public boolean start(WorkingBufferSeeker wb,NodeList tlist) throws IOException {
		wb.setContentNodeClzz(this.getConentNodeClzz());
		for(Tokenizer tok : this.getTokenizerList() ){
			Node node = tok.seek(wb);
			if(node != null){
				//处理内容
				 List<Node>  cnodes = wb.getContentNodes();
				 if(cnodes != null){
					 //继续处理cnode为特定的node
					 tlist.addNodes(cnodes);
					 wb.cleanContentNodes();
				 }
				 //处理获取的node
				 //如果当前的token还包含了，子area,那么继续
				  if(tok.getSubArea() != null){
					  	Reader reader =  new StringReader(node.getOriginalSource());
						WorkingBufferSeeker cwb  = new WorkingBufferSeeker(reader);
						cwb.init();
						//cwb.seekNode()
						//System.out.println(node.getOriginalSource());
						 
						Area area = tok.getSubArea();
						area.setup();
						cwb.setContentNodeClzz(area.getConentNodeClzz());
						 while(!cwb.isAllRead() ){
							//驱动每一个area,如果area返回结果为true,表示当前pos的搜寻结束
							//每个tokenizer找到token之后pos都要移动到token的下一个pos
							
							
							if(area.start(cwb, tlist)){
								 continue ;//结束当前pos的搜索
							}
							
							cwb.move();
						}
						//处理最后的内容
						if(cwb.getEndContentNode() != null){
							tlist.addNode(cwb.getEndContentNode());
						}
					 
					 
				  }else{
				 	tlist.addNode(node);
				  }
				return true; //返回true,neatenCleaner结束搜索
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public List<Tokenizer> getTokenizerList() {
		return tokenizerList;
	}
	public NeatenCleaner getNeatenCleaner() {
		return neatenCleaner;
	}
	public void setNeatenCleaner(NeatenCleaner neatenCleaner) {
		this.neatenCleaner = neatenCleaner;
	}
	public Class<? extends Node> getConentNodeClzz() {
		return conentNodeClzz;
	}
	public void setConentNodeClzz(Class<? extends Node> conentNodeClzz) {
		this.conentNodeClzz = conentNodeClzz;
	}
}
