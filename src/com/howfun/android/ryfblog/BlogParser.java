package com.howfun.android.ryfblog;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class BlogParser {
	
	public List<Blog> getBlogs(InputStream in){
		
		SAXParserFactory spf = SAXParserFactory.newInstance();
		BlogHandler blogHandler = new BlogHandler();
	      try {
	         SAXParser sp = spf.newSAXParser();
	         XMLReader xr = sp.getXMLReader();
	         xr.setContentHandler(blogHandler);
	         xr.parse(new InputSource(in));
	      } catch (Exception e) {
	         e.printStackTrace();
	      }

		return blogHandler.getBlogList();
	}

}
