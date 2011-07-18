package com.howfun.android.ryfblog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

public class Utils {

	public static final String RYF_BLOG = "RYFBlog";

	public static final int TIMEOUT = 5 * 1000;

	public static final String BLOG_URL = "http://www.ruanyifeng.com/blog/atom.xml";

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

}
