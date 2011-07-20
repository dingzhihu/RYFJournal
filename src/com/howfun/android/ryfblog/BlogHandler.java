package com.howfun.android.ryfblog;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BlogHandler extends DefaultHandler {
	private static final int PUBLISHED_LEN = 10;

	private StringBuilder mBuilder = new StringBuilder();
	
	private List<Blog> mBlogList = new ArrayList<Blog>();
	private Blog blog = null;
	private String title = "";
	private String summary = "";
	private String published = "";
	private String content = "";
	
	public List<Blog> getBlogList(){
		return mBlogList;
	}

	@Override
	public void startDocument() throws SAXException {
		
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if(localName.equals("entry")){
			
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if(localName.equals("entry")){
			blog = new Blog(title, summary, getPublished(published), getContent(content).trim());
			mBlogList.add(blog);
			title = "";
			summary = "";
			published = "";
			content = "";
			blog = null;
		}
		if(localName.equals("title")){
			title = mBuilder.toString().trim();
		}
		if(localName.equals("summary")){
			summary = mBuilder.toString().trim();
		}
		if(localName.equals("published")){
			published = mBuilder.toString().trim();
		}
		if(localName.equals("content")){
			content = mBuilder.toString().trim();
		}
		
		mBuilder.setLength(0);
	}

	@Override
	public void characters(char ch[], int start, int length) {
		mBuilder.append(ch, start, length);
	}

	private String getPublished(String str){
		String published = "";
		if(str != null && str.length() >= PUBLISHED_LEN){
			published = str.substring(0, PUBLISHED_LEN);
		}
		return published;
	}
	
	private String getContent(String str){
	   String content = "";
	   if(str != null){
	      int index = 0;
	      index = str.indexOf("<div");
	      if(index != -1){
	         content = str.substring(0, index);
	      }
	   }
	   return content;
	}
}
