package com.howfun.android.ryfblog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
	public static final String TAG = "DBAdapter";

	private static final String DATABASE_NAME = "ryf_blog";
	private static final int DATABASE_VERSION = 1;

	private static final String TABLE_BLOGS = "blogs";

	private static final String KEY_ID = "_id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_SUMMARY = "summary";
	private static final String KEY_PUBLISHED = "published";
	private static final String KEY_CONTENT = "content";

	private static final String CREATE_TABLE_BLOGS = "CREATE TABLE "
			+ TABLE_BLOGS + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE
			+ " TEXT NOT NULL," + KEY_SUMMARY + " TEXT NOT NULL,"
			+ KEY_PUBLISHED + " TEXT NOT NULL," + KEY_CONTENT
			+ " TEXT NOT NULL" + ");";
	private static final String DROP_TABLE_BLOGS = "DROP TABLE IF EXISTS "
			+ TABLE_BLOGS;

	private final Context mCtx;

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	public DBAdapter(Context context) {
		mCtx = context;
	}

	public DBAdapter open() {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public void addBlog(Blog blog) {
		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, blog.getTitle());
		values.put(KEY_SUMMARY, blog.getSummary());
		values.put(KEY_PUBLISHED, blog.getPublished());
		values.put(KEY_CONTENT, blog.getContent());
		mDb.insert(TABLE_BLOGS, null, values);
	}

	public void addBlogs(List<Blog> blogs) {
		if (blogs != null) {
			Iterator<Blog> it = blogs.iterator();
			while (it.hasNext()) {
				addBlog(it.next());
			}
		}
	}

	public List<Blog> getBlogs(int num) {
		List<Blog> blogs = new ArrayList<Blog>();
		if (num < 0) {
			return blogs;
		}
		Cursor cur = null;

		if (num == 0) { // query all
			cur = mDb.query(TABLE_BLOGS, null, null, null, null, null, KEY_PUBLISHED + " DESC");
		} else {
			String sql = "select * from " + TABLE_BLOGS + " order by "+KEY_PUBLISHED+" DESC limit "
					+ String.valueOf(num);
			cur = mDb.rawQuery(sql, null);
		}

		if (cur != null) {
			cur.moveToFirst();
		} else {
			return blogs;
		}
		if (cur.getCount() == 0) { // blog list is empty
			cur.close();
			return blogs;
		}

		do {
			String title = cur.getString(cur.getColumnIndex(KEY_TITLE));
			String summary = cur.getString(cur.getColumnIndex(KEY_SUMMARY));
			String published = cur.getString(cur.getColumnIndex(KEY_PUBLISHED));
			String content = cur.getString(cur.getColumnIndex(KEY_CONTENT));
			Blog blog = new Blog(title, summary, published, content);
			blogs.add(blog);

		} while (cur.moveToNext());
		cur.close();

		return blogs;
	}

	public List<Blog> getAllBlogs() {
		return getBlogs(0);
	}

	public boolean blogExists(String title, String published) {
		boolean flag = false;
		List<Blog> blogs = getAllBlogs();
		Iterator<Blog> it = blogs.iterator();
		while (it.hasNext()) {
			Blog blog = it.next();
			String blogTitle = blog.getTitle();
			String blogPostDate = blog.getPublished();
			if (title.equals(blogTitle) && (blogPostDate.equals(published))) {
				flag = true;
				return flag;
			}
		}
		return flag;
	}

	public boolean delAllBlogs() {
		boolean flag = false;
		flag = mDb.delete(TABLE_BLOGS, null, null) > 0;

		return flag;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Utils.log(TAG, "create table " + TABLE_BLOGS);
			db.execSQL(CREATE_TABLE_BLOGS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(DROP_TABLE_BLOGS);
			onCreate(db);
		}
	}

}
