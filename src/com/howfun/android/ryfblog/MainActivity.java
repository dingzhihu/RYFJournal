package com.howfun.android.ryfblog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

public class MainActivity extends Activity {
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 1:
				List<Blog> list = (List<Blog>)msg.obj;
				Utils.showMessageDlg(MainActivity.this, ""+list.get(0).getContent());
				break;
			}
		}
	};
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        new Thread(){
        	public void run(){
        		String s = "";
        		List<Blog> blogs = new ArrayList<Blog>();
        		InputStream in = null;
        		try {
					in = Utils.getXml(Utils.BLOG_URL);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					s = "connection error";
					e.printStackTrace();
				}
				blogs = new BlogParser().getBlogs(in);
				Message msg = new Message();
				msg.what = 1;
				msg.obj = blogs;
				mHandler.sendMessage(msg);
        	}
        }.start();
    }
}