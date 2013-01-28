package com.taobao.b2c.neaten.velocity;

import com.taobao.b2c.neaten.BaseNode;
import com.taobao.b2c.neaten.Node;
/**
 * @author xiaoxie
 */
public class VelocityNode extends BaseNode {
	private static String[] END_DELIMITER =  {" ", "\n", "\r", "\t", "\f","\u200B","("};

 
	public void setOriginalSource(String source){
		super.setOriginalSource(source);
		super.setContentType(Node.CONTENT_TYPE_CODE);
		parseName(source);
	}
	private void parseName(String tagSource) {
		tagSource = tagSource.toLowerCase();
		int endPos = -1;
		for(String delimiter : END_DELIMITER){
			int pos = tagSource.indexOf(delimiter);
			if(pos != -1){
				if(endPos == -1){
					endPos = pos;
				}else{
					if(endPos > pos){
						endPos = pos;
					}
				}
			}
		}
	    if(endPos == -1){
	    	this.setName(tagSource.substring(1));
	    }else{
			try {
				this.setName(tagSource.substring(1,endPos));
			} catch (RuntimeException e) {
				System.out.println(endPos);
				System.out.println(tagSource);
	 
				
				throw new RuntimeException();
			}
	    }
	    if("if".equals(this.getName())){
	    	this.setFormatType(Node.FORMAT_TYPE_OPEN);
	    }
	    if("macro".equals(this.getName())){
	    	this.setFormatType(Node.FORMAT_TYPE_OPEN);
	    }
	    if("end".equals(this.getName())){
	    	this.setFormatType(Node.FORMAT_TYPE_CLOSE);
	    }
	    if("elseif".equals(this.getName())){
	    	this.setFormatType(Node.FORMAT_TYPE_CLOSE_OPEN);
	    }
	    if("foreach".equals(this.getName())){
	    	this.setFormatType(Node.FORMAT_TYPE_OPEN);
	    }
	    if("else".equals(this.getName())){
	    	this.setFormatType(Node.FORMAT_TYPE_CLOSE_OPEN);
	    }
	    if("set".equals(this.getName())){
	    	this.setFormatType(Node.FORMAT_TYPE_INTACT);
	    }
//		if("#if".equals(tagSource)){
//			 
//			this.setName("#script");
//		}else
//		if("#end".equals(tagSource)){
//			 
//			this.setName("#script");
//		}else
//		if("#elseif".equals(tagSource)){
//			 
//			this.setName("#script");
//		}else
//			if("#else".equals(tagSource)){
//				 
//				this.setName("#script");
//			}else
//			if("#foreach".equals(tagSource)){
//				 
//				this.setName("#script");
//			}
		
	}
	public boolean isMatch(Node node) {
		if(node == null || node.getName() == null){
			return false;
		}
		//System.out.println(node.getType());
		//if ∫Õ elseif,end∆•≈‰
		if(this.getName().equals("end")){
			if(node.getName().equals("foreach") ||node.getName().equals("if") ||node.getName().equals("else")||node.getName().equals("elseif") ||node.getName().equals("macro"))
				return true;
		} else
		//elseif,else∫Õend
		if(this.getName().equals("else")){
			if(node.getName().equals("elseif")||node.getName().equals("if"))
				return true;
		} else
		//foreach ∫Õend∆•≈‰
		if(this.getName().equals("elseif")){
			if(node.getName().equals("if"))
				return true;
		}
	 
		return false;
		
	}
}
