package com.taobao.b2c.neaten;

import java.io.File;
import java.io.IOException;

import com.taobao.b2c.neaten.html.HtmlArea;
import com.taobao.b2c.neaten.javascript.JavaScriptArea;
import com.taobao.b2c.neaten.velocity.VelocityArea;
/**
 * @author xiaoxie
 */
public class Test {
	public static void main(String[] args) throws IOException{
		 //NeatenCleaner nc = new NeatenCleaner("<BOdy <<.>>\"<div>.\" >x<div>x</body>sdfsdf<div>a</div>a<h>#if(x())aa\rs #foreach(asdf in $sfs.()) #end <!--xxx<xxx/>-->");
		// NeatenCleaner nc = new NeatenCleaner("<script>if(a=b){	var a=0;    var b = 0;{var c=0;}}</script><script>s</script></script><style></style><Body><table><tr><td>sdf</td></tr></table></body>");
		 //NeatenCleaner nc = new NeatenCleaner("<INPUT ><INPUT ><INPUT ><INPUT >");
		//NeatenCleaner nc = new NeatenCleaner("#if(xx in sss()) sdffd #else sdfsfd #end");
		 //NeatenCleaner nc = new NeatenCleaner("<script language=> <body>if(1==1){}{}{{}}</script >ss<div>\r\n<body></body></div>sdsdf");
		//NeatenCleaner nc = new NeatenCleaner(new File("D:/TECH_PROJECT_CODE_DOC/PROJECT_JAVAEE/ark/shop/web/src/webroot/templates/layout/adminLayout.vm"),"GBK");
		 //NeatenCleaner nc = new NeatenCleaner(new File("D:\\javascript\\vm.vm"));
		 //NeatenCleaner nc = new NeatenCleaner(new File("D:\\javascript\\favorite.source.js") );
		 //NeatenCleaner nc = new NeatenCleaner(" #** -------------------------------------------\r\n<script>if(()()){\r\n} \r\n var if=0;()</script>");
		//NeatenCleaner nc = new NeatenCleaner("<option></option><div></div>");
		  //NeatenCleaner nc = new NeatenCleaner(new File("D:\\TECH_PROJECT_CODE_DOC\\PROJECT_JAVAEE\\ark\\admin\\web\\src\\webroot\\templates\\control\\product\\brandChanager.vm"));
		  //NeatenCleaner nc = new NeatenCleaner(new File("D:\\javascript\\Noname3.html"));
			 
		 //NeatenCleaner nc = new NeatenCleaner("#if(xx.xx())\r\n xxx\r\n xxx\r\n#end");
		 //NeatenCleaner nc = new NeatenCleaner("<script>x if(true){ var elif=0;if(){} var elseif=0;var else = 0;}</script>");
		 //NeatenCleaner nc = new NeatenCleaner("<script>if(x){x}</script>");
		 //NeatenCleaner nc = new NeatenCleaner("<script>if(x){x\r\nx}if()if()xx\r\nxxfor()if()if(){}xxx\r\nss</script>");
		 //NeatenCleaner nc = new NeatenCleaner("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"><HTML><HEAD><TITLE> New Document </TITLE><META NAME=\"Generator\" CONTENT=\"EditPlus\"><META NAME=\"Author\" CONTENT=\"\"><META NAME=\"Keywords\" CONTENT=\"\"><META NAME=\"Description\" CONTENT=\"\"><script>//xx\r/*xxx*/\rvar a='//';if(){ss}else{xx}for()xxx\r\naaif(xxx)if(xxx)if(xxx){xx}xxx</script></HEAD><BODY>asdf\r\nsdfsf<!--comments--><div><dd></dd></div></BODY></HTML>");
		
		File file = new File("d://a1.vm"); 
		
		NeatenCleaner nc = new NeatenCleaner(file);
		 try {
				
				nc.addArea(new HtmlArea());
				nc.addArea(new VelocityArea());
				nc.addArea(new JavaScriptArea());
				nc.setOmitFormField(false);
				nc.setRender(new BrowserCompactRender());
		 
			//nc.addArea(new VelocityArea());
			long t1= System.currentTimeMillis();
			// nc.removeComment("##");
			nc.clean();
			
			//NodeList nlist = nc.getNodeList();
//			//打印输出结果
//			List<Node> list = nlist.getNodeList();
//			
//			for(Node resultNode : list){
//				System.out.print(resultNode);
//			}
		 // nc.setRender(new HtmlPrettyRender2());
		   nc.setRender(new BrowserCompactRender());
//			   StringBufferOutputStrem sb = new StringBufferOutputStrem();
			 StringBuffer b = new StringBuffer();
			nc.write(b);
//			 System.
			 System.out.println(b.toString());
			 System.out.println(System.currentTimeMillis()-t1);
		// nc.write(new File("D:\\javascript\\xxx.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
