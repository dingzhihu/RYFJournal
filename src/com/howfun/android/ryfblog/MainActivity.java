package com.howfun.android.ryfblog;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private DBAdapter mDbAdapter = null;
	private ProgressBar mProgressBar = null;
	private ImageView mRefreshView = null;
	private ListView mListView = null;
	private TextView mEmptyView = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        findViews();
        setupListeners();
        init();
    }
    
    private void findViews(){
    	mProgressBar = (ProgressBar) findViewById(R.id.progress);
    	mRefreshView = (ImageView) findViewById(R.id.refresh);
    	mListView = (ListView) findViewById(R.id.blog_list);
    	mEmptyView = (TextView) findViewById(R.id.empty_blog);
    }
    
    private void setupListeners(){
    	if(mRefreshView != null){
    		mRefreshView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					refresh();
				}
			});
    	}
    }
    
    private void init(){
    	mDbAdapter = new DBAdapter(this);
    	mDbAdapter.open();
    	List<Blog> blogs = mDbAdapter.getAllBlogs();
    	mDbAdapter.close();
    	if(blogs != null && blogs.size()==0){
    		mEmptyView.setVisibility(View.VISIBLE);
    	}else{
    		BlogAdapter adapter = new BlogAdapter(this, R.layout.blog_item, blogs);
    		mListView.setAdapter(adapter);
    	}
    	
    }
    
    private void refresh(){
    	BlogTask task = new BlogTask();
    	task.execute(Utils.BLOG_URL);
    }
    class BlogTask extends AsyncTask<String, Integer, List<Blog>>{

		@Override
		protected List<Blog> doInBackground(String... params) {
			InputStream in =null;
			List<Blog> blogs= null;
			try {
				in = Utils.getXml(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			blogs = new BlogParser().getBlogs(in);
			Iterator<Blog> it = blogs.iterator();
//			while(it.hasNext()){
//			   System.out.println(it.next().getContent());
//			}
			System.out.println("========================"+blogs.get(0).getContent());
			
			return blogs;
		}
		
		@Override
		protected void onPreExecute(){
			mRefreshView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onPostExecute(List<Blog> blogs){
			mProgressBar.setVisibility(View.INVISIBLE);
			mRefreshView.setVisibility(View.VISIBLE);
			
//			if(blogs.size() > 0){
//				mDbAdapter.open();
//				mDbAdapter.addBlogs(blogs);
//				mDbAdapter.close();
//				BlogAdapter adapter = new BlogAdapter(MainActivity.this, R.layout.blog_item, blogs);
//				mListView.setAdapter(adapter);
//			}
			
		}

       
    }
    
    class BlogAdapter extends ArrayAdapter<Blog> {
    	   private LayoutInflater mInflater;
    	   private Context mContext;
    	   private int mResource;

    	   public BlogAdapter(Context context, int resource, List<Blog> objects) {
    	      super(context, resource, objects);
    	      mContext = context;
    	      mResource = resource;
    	      mInflater = (LayoutInflater) context
    	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	   }

    	   @Override
    	   public View getView(int position, View convertView, ViewGroup parent) {
    	      ViewHolder holder;

    	      Blog item = getItem(position);

    	      if (convertView == null) {
    	         convertView = mInflater.inflate(mResource, parent, false);
    	         holder = new ViewHolder();
    	         holder.title = (TextView) convertView.findViewById(R.id.title);
    	         holder.published = (TextView) convertView.findViewById(R.id.published);
    	         holder.summary = (TextView) convertView.findViewById(R.id.summary);
    	         convertView.setTag(holder);
    	      } else {
    	         holder = (ViewHolder) convertView.getTag();
    	      }

    	      holder.title.setText(item.getTitle());
    	      holder.published.setText(item.getPublished());
    	      holder.summary.setText(item.getSummary());

    	      return convertView;
    	   }

    	   private  class ViewHolder {
    	      TextView title;
    	      TextView published;
    	      TextView summary;
    	   }

    	}
}