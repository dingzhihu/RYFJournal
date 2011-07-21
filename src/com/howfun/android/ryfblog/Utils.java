package com.howfun.android.ryfblog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

public class Utils {

   public static final String RYF_BLOG = "RYFBlog";

   public static final int TIMEOUT = 5 * 1000;

   public static final String BLOG_URL = "http://www.ruanyifeng.com/blog/atom.xml";

   public static final String PREF = "pref";
   public static final String PREF_BLOG_COUNT = "blog_count";
   public static final String PREF_CLEAN_BUFFER = "clean_buffer";
   public static final String PREF_LAST_UPDATED = "last_updated";
   public static final String PREF_READ_BLOG = "read_blog";

   public static final int RESULT_EXIT = Activity.RESULT_FIRST_USER;
   public static final int REQUEST_BLOG_CONTENT = 1;

   protected static final String BLOG_TITLE_REF = "blog_title_ref";
   protected static final String BLOG_PUBLISHED_REF = "blog_published_ref";
   protected static final String BLOG_CONTENT_REF = "blog_content_ref";

   public static void log(String tag, String info) {
      Log.d(RYF_BLOG + "============>" + tag, "-------->" + info);
   }

   public static void showMessageDlg(Context context, String msg) {
      new AlertDialog.Builder(context).setMessage(msg).setPositiveButton(
            android.R.string.ok, null).show();
   }

   public static InputStream getXml(String path) throws Exception {

      URL url = new URL(path);

      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      conn.setRequestMethod("GET");

      conn.setConnectTimeout(TIMEOUT);

      InputStream inStream = conn.getInputStream();

      return inStream;

   }

   public static byte[] readFromInput(InputStream inStream) throws Exception {

      ByteArrayOutputStream outStream = new ByteArrayOutputStream();

      byte[] buffer = new byte[1024];

      int len = 0;

      while ((len = inStream.read(buffer)) != -1) {

         outStream.write(buffer, 0, len);

      }

      inStream.close();

      return outStream.toByteArray();

   }

   public static void about(Context ctx) {
      new AlertDialog.Builder(ctx).setIcon(R.drawable.icon).setTitle(
            R.string.app_name).setMessage(R.string.about).setPositiveButton(
            android.R.string.ok, null).show();
   }
   
   public static String getDate() {
      String date = "";
      Calendar calender = Calendar.getInstance();
      int year = calender.get(Calendar.YEAR);
      int month = calender.get(Calendar.MONTH) + 1;
      int day = calender.get(Calendar.DAY_OF_MONTH);
      date = year + "/" + month + "/" + day;
      return date;
   }

}
