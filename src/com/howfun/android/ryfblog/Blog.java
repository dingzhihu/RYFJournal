package com.howfun.android.ryfblog;

public class Blog {
	private String mTitle = "";
	private String mSummary = "";
	private String mPublished = "";
	private String mContent = "";
	
	public Blog(String title,String summary,String published,String content){
		mTitle = title;
		mSummary = summary;
		mPublished = published;
		mContent = content;
	}
	
	public String getTitle(){
		return mTitle;
	}
	
	public String getSummary(){
		return mSummary;
	}
	
	public String getPublished(){
		return mPublished;
	}
	
	public String getContent(){
		return mContent;
	}
	
	public String toString(){
		return "title:"+mTitle+"\n"+"summary:"+mSummary+"\n"+"published:"+mPublished
		+"\ncontent len:"+mContent.length();
	}

}
