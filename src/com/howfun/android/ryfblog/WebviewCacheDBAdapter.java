package com.howfun.android.ryfblog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WebviewCacheDBAdapter {

   private static final String TAG = "WebviewCacheDBAdapter";

   private static final String DATABASE_NAME = "webviewCache.db";
   private static final int DATABASE_VERSION = 1;

   private static final String TABLE_CACHE = "cache";

   private final Context mCtx;

   private DatabaseHelper mDbHelper;
   private SQLiteDatabase mDb;

   public WebviewCacheDBAdapter(Context context) {
      mCtx = context;
   }

   public WebviewCacheDBAdapter open() {
      mDbHelper = new DatabaseHelper(mCtx);
      mDb = mDbHelper.getWritableDatabase();
      return this;
   }

   public void close() {
      mDbHelper.close();
   }

   public void cleanCache() {
      Utils.log(TAG, "clean table " + TABLE_CACHE);
      String sql = "DELETE FROM " + TABLE_CACHE +"";
      mDb.execSQL(sql);

   }

   private static class DatabaseHelper extends SQLiteOpenHelper {
      public DatabaseHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);

      }

      @Override
      public void onCreate(SQLiteDatabase db) {
         
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      }
   }

}
