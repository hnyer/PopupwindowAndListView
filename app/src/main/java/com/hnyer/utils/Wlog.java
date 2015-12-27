package com.hnyer.utils;

import android.util.Log;

public class Wlog {
	private static boolean IS_DEBUG = true;

	public static final void openLog(boolean enable) {
		IS_DEBUG = enable;
	}

	public static void v(String tag, String msg) {
		if(IS_DEBUG){
			Log.v(tag, msg);
		}
	}
	public static void d(String tag, String msg) {
		if(IS_DEBUG){
			Log.d(tag, msg);
		}
	}
	public static void i(String tag, String msg) {
		if(IS_DEBUG){
			Log.i(tag, msg);
		}
	}
	public static void w(String tag, String msg) {
		if(IS_DEBUG){
			Log.w(tag, msg);
		}
	}
	public static void e(String tag, String msg) {
		if(IS_DEBUG){
			Log.e(tag, msg);
		}
	}
}

