package com.howfun.android.ryfblog;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

public class PrefActivity extends Activity {

   private static final int COUNT_ALL = 0;
   private static final int COUNT_5 = 5;
   private static final int COUNT_10 = 10;
   private static final int COUNT_15 = 15;

   SharedPreferences mSettings = null;
   private int mBlogCount;
   private boolean mCleanBuffer;

   private RadioButton mBtnAll, mBtn5, mBtn10, mBtn15, mBtnCleanYes,
         mBtnCleanNo;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setTitle(R.string.pref);
      setContentView(R.layout.pref);

      mBtnAll = (RadioButton) findViewById(R.id.radio_count_all);
      mBtn5 = (RadioButton) findViewById(R.id.radio_count_5);
      mBtn10 = (RadioButton) findViewById(R.id.radio_count_10);
      mBtn15 = (RadioButton) findViewById(R.id.radio_count_15);
      mBtnCleanYes = (RadioButton) findViewById(R.id.radio_clean_yes);
      mBtnCleanNo = (RadioButton) findViewById(R.id.radio_clean_no);

      mSettings = getSharedPreferences(Utils.PREF, 0);

      checkPref();
      Button ok = (Button) findViewById(R.id.pref_button_ok);
      ok.setOnClickListener(new OnClickListener() {
         public void onClick(View view) {
            savePreferences();
            PrefActivity.this.finish();
         }
      });
      Button cancel = (Button) findViewById(R.id.pref_button_cancel);
      cancel.setOnClickListener(new OnClickListener() {
         public void onClick(View view) {
            PrefActivity.this.finish();
         }
      });
   }

   private void checkPref() {
      int count = mSettings.getInt(Utils.PREF_BLOG_COUNT, 0);
      boolean clean = mSettings.getBoolean(Utils.PREF_CLEAN_BUFFER, false);

      mBlogCount = count;
      mCleanBuffer = clean;
      switch (count) {
      case COUNT_ALL:
         mBtnAll.setChecked(true);
         break;
      case COUNT_5:
         mBtn5.setChecked(true);
         break;
      case COUNT_10:
         mBtn10.setChecked(true);
         break;
      case COUNT_15:
         mBtn15.setChecked(true);
         break;

      default:
         break;
      }
      if (clean) {
         mBtnCleanYes.setChecked(true);
      } else {
         mBtnCleanNo.setChecked(true);
      }
   }

   private void savePreferences() {
      int count = 0;
      boolean clean = false;

      if (mBtnAll.isChecked()) {
         count = COUNT_ALL;
      } else if (mBtn5.isChecked()) {
         count = COUNT_5;
      } else if (mBtn10.isChecked()) {
         count = COUNT_10;
      } else if (mBtn15.isChecked()) {
         count = COUNT_15;
      }

      if (mBtnCleanYes.isChecked()) {
         clean = true;
      } else if (mBtnCleanNo.isChecked()) {
         clean = false;
      }
      if (mBlogCount != count) {
         mSettings.edit().putInt(Utils.PREF_BLOG_COUNT, count).commit();
      }
      if (mCleanBuffer != clean) {
         mSettings.edit().putBoolean(Utils.PREF_CLEAN_BUFFER, clean).commit();
      }
   }
}
