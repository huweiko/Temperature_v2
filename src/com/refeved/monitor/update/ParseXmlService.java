package com.refeved.monitor.update;

import java.io.StringReader;
import java.util.HashMap;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 *@author coolszy
 *@date 2012-4-26
 *@blog http://blog.92coding.com
 */
public class ParseXmlService
{
	public HashMap<String, String> parseXml(String resXml) throws Exception
	{
		HashMap<String, String> hashMap = new HashMap<String, String>();
		SAXBuilder builder = new SAXBuilder();
		StringReader sr = new StringReader(resXml);   
		InputSource is = new InputSource(sr); 
		Document Doc = builder.build(is);
		Element rootElement = (Element) Doc.getRootElement();

		Element e = null;
		 if((e =rootElement.getChild("version")) != null)
		 {
			hashMap.put("version",e.getValue());
		 }  
		 if((e =rootElement.getChild("name")) != null)
		 {
			 hashMap.put("name",e.getValue());
		 } 
		 if((e =rootElement.getChild("url")) != null)
		 {
			 hashMap.put("url",e.getValue());
		 } 
		 if((e =rootElement.getChild("upgrade")) != null)
		 {
			 hashMap.put("upgrade",e.getValue());
		 } 
		 if((e =rootElement.getChild("content")) != null)
		 {
			 hashMap.put("content",e.getValue());
		 } 

		return hashMap;
	}
}
