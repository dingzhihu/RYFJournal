package com.howfun.android.ryfblog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

   public static final String TAG = "MainActivity";

   private DBAdapter mDbAdapter = null;
   private List<Blog> mBlogList = null;
   private BlogAdapter mAdapter = null;

   private BlogTask mTask;

   private ProgressBar mProgressBar = null;
   private ImageView mRefreshView = null;
   private ListView mListView = null;
   private TextView mEmptyView = null;

   private TextView mInfoText = null;

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

   private void findViews() {
      mProgressBar = (ProgressBar) findViewById(R.id.progress);
      mRefreshView = (ImageView) findViewById(R.id.refresh);
      mListView = (ListView) findViewById(R.id.blog_list);
      mEmptyView = (TextView) findViewById(R.id.empty_blog);
      mInfoText = (TextView) findViewById(R.id.info);
      mListView.setCacheColorHint(0);
   }

   private void setupListeners() {
      if (mRefreshView != null) {
         mRefreshView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
               refresh();
            }
         });

         if (mListView != null) {
            mListView.setOnItemClickListener(new OnItemClickListener() {

               @Override
               public void onItemClick(AdapterView<?> parent, View view,
                     int position, long id) {
                  // TODO browse the blog
                  Blog blog = (Blog) parent.getAdapter().getItem(position);

                  Intent intent = new Intent(MainActivity.this,
                        BlogActivity.class);
                  intent.putExtra(Utils.BLOG_TITLE_REF, blog.getTitle());
                  intent
                        .putExtra(Utils.BLOG_PUBLISHED_REF, blog.getPublished());
                  intent.putExtra(Utils.BLOG_CONTENT_REF, blog.getContent());
                  startActivityForResult(intent, Utils.REQUEST_BLOG_CONTENT);

               }

            });
         }
      }
   }

   private void init() {
      mDbAdapter = new DBAdapter(this);

   }

   private void refresh() {
      mTask = new BlogTask(this);
      mTask.execute(Utils.BLOG_URL);
   }

   private void killTask() {
      if (mProgressBar.getVisibility() == View.VISIBLE) {
         Utils.log(TAG, "progress bar is running");
         if (mTask != null) {
            mTask.kill();
         }
      }
   }

   // clean web view buffer
   private void cleanBuffer() {

      SharedPreferences pref = getSharedPreferences(Utils.PREF, 0);
      boolean clean = pref.getBoolean(Utils.PREF_CLEAN_BUFFER, false);
      boolean webviewCacheDbExists = pref.getBoolean(Utils.PREF_READ_BLOG,
            false);
      if (clean && webviewCacheDbExists) {
         Utils.log(TAG, "clean web view buffer");
         WebviewCacheDBAdapter adapter = new WebviewCacheDBAdapter(this);
         adapter.open();
         adapter.cleanCache();
         adapter.close();
      } else {
         Utils.log(TAG, "do not clean web view buffer");
      }
   }

   private void finishActivity() {
      killTask();
      cleanBuffer();
      finish();
   }

   private void setInfo(String info) {
      if (mInfoText != null) {
         mInfoText.setText(info);
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == Utils.REQUEST_BLOG_CONTENT) {
         if (resultCode == Utils.RESULT_EXIT) {
            finishActivity();
         }
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu, menu);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      super.onOptionsItemSelected(item);
      switch (item.getItemId()) {
      case R.id.pref:
         Intent intent = new Intent(MainActivity.this, PrefActivity.class);
         startActivity(intent);
         break;
      case R.id.about:
         Utils.about(MainActivity.this);
         break;
      case R.id.exit:
         finishActivity();
         break;
      }
      return true;
   }

   @Override
   public void onResume() {
      SharedPreferences pref = getSharedPreferences(Utils.PREF, 0);
      int count = pref.getInt(Utils.PREF_BLOG_COUNT, 0);
      String lastUpdated = pref.getString(Utils.PREF_LAST_UPDATED, "");
      mDbAdapter.open();
      if (count == 0) {
         mBlogList = mDbAdapter.getAllBlogs();
      } else {
         mBlogList = mDbAdapter.getBlogs(count);
      }
      mDbAdapter.close();
      mAdapter = new BlogAdapter(this, R.layout.blog_list_item, mBlogList);
      mListView.setAdapter(mAdapter);
      if (mBlogList == null || mBlogList.size() == 0) {
         mEmptyView.setVisibility(View.VISIBLE);
      }

      if (!"".equals(lastUpdated)) {
         setInfo(getResources().getString(R.string.last_updated) + lastUpdated);
      }
      super.onResume();
   }

   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
      switch (keyCode) {
      case KeyEvent.KEYCODE_BACK:
         killTask();
         cleanBuffer();
      default:
         break;
      }

      return super.onKeyDown(keyCode, event);
   }

   class BlogTask extends AsyncTask<String, Integer, List<Blog>> {

      private Context mCtx;

      public BlogTask(Context ctx) {
         mCtx = ctx;
      }

      private boolean kill = false;

      public void kill() {
         Utils.log(TAG, "kill task");
         kill = true;
      }

      @Override
      protected List<Blog> doInBackground(String... params) {
         InputStream in = null;
         List<Blog> blogs = null;
         List<Blog> newBlogList = new ArrayList<Blog>();
         try {
            in = Utils.getXml(params[0]);
         } catch (Exception e) {
            e.printStackTrace();
         }
         blogs = new BlogParser().getBlogs(in);
         Iterator<Blog> it = blogs.iterator();
         Utils.log(TAG, "blog size:" + blogs.size());
         if (!kill) {
            while (it.hasNext()) {
               mDbAdapter.open();
               Blog blog = it.next();
               if (!mDbAdapter.blogExists(blog.getTitle(), blog.getPublished())) {
                  Utils.log(TAG, "add one blog,blog title:" + blog.getTitle());
                  mDbAdapter.addBlog(blog);
                  newBlogList.add(blog);
               }
               mDbAdapter.close();
            }
         }

         return newBlogList;
      }

      @Override
      protected void onPreExecute() {
         mRefreshView.setVisibility(View.INVISIBLE);
         mProgressBar.setVisibility(View.VISIBLE);
         setInfo(getResources().getString(R.string.updating));
      }

      @Override
      protected void onPostExecute(List<Blog> blogs) {
         mProgressBar.setVisibility(View.INVISIBLE);
         mRefreshView.setVisibility(View.VISIBLE);
         setInfo(getResources().getString(R.string.num_updated) + blogs.size());

         String updated = Utils.getDate();
         SharedPreferences pref = mCtx.getSharedPreferences(Utils.PREF, 0);
         pref.edit().putString(Utils.PREF_LAST_UPDATED, updated).commit();
         // TODO update
         if (blogs.size() > 0) {
            List<Blog> temp = new ArrayList<Blog>();
            temp.addAll(mBlogList);

            mBlogList.clear();
            mBlogList.addAll(blogs);
            mBlogList.addAll(temp);
            mAdapter.notifyDataSetChanged();
         }
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
            holder.published = (TextView) convertView
                  .findViewById(R.id.published);
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

      private class ViewHolder {
         TextView title;
         TextView published;
         TextView summary;
      }

   }
}