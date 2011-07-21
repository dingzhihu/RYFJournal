package com.howfun.android.ryfblog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

public class BlogActivity extends Activity {
   private TextView mTitle;
   private TextView mPublished;
   private WebView mContent;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      super.onCreate(savedInstanceState);
      setContentView(R.layout.blog);
      mTitle = (TextView) findViewById(R.id.blog_title);
      mPublished = (TextView) findViewById(R.id.blog_published);
      mContent = (WebView) findViewById(R.id.blog_content);
      Intent intent = getIntent();
      mTitle.setText(intent.getStringExtra(Utils.BLOG_TITLE_REF));
      mPublished.setText(intent.getStringExtra(Utils.BLOG_PUBLISHED_REF));

      final String mimeType = "text/html";
      final String encoding = "utf-8";
      String content = intent.getStringExtra(Utils.BLOG_CONTENT_REF);
      mContent.loadDataWithBaseURL(null, getFormattedContent(content),
            mimeType, encoding, null);
      SharedPreferences pref = getSharedPreferences(Utils.PREF, 0);
      pref.edit().putBoolean(Utils.PREF_READ_BLOG, true).commit();

   }

   private String getFormattedContent(String str) {

      String prefix = "<html><head><style type=\"text/css\">"
            + "body {background-color: #f5f5d5}</style></head><body>";
      String suffix = "</body></html>";
      return prefix + str + suffix;
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
         Intent intent = new Intent(BlogActivity.this, PrefActivity.class);
         startActivity(intent);
         break;
      case R.id.about:
         Utils.about(BlogActivity.this);
         break;
      case R.id.exit:
         setResult(Utils.RESULT_EXIT);
         finish();
         break;
      }
      return true;
   }
}
