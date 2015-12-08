package com.linkage.mobile72.sh.utils;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.linkage.lib.util.LogUtils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 本地图片缓存加载
 */
public class NativeImageLoader implements ImageCache {
	private LruCache<String, Bitmap> mLruCache;
	private static NativeImageLoader mInstance;

	private NativeImageLoader() {
		// 获取应用程序的最大内存
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		// 用最大内存的1/8来缓存图片
		final int cacheSize = maxMemory / 8;
		mLruCache = new LruCache<String, Bitmap>(cacheSize) {
			// 获取每张图片的大小
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			}
		};
	}

	/**
	 * 获取NativeImageLoader的实例
	 */
	public static NativeImageLoader getInstance() {
		if (mInstance == null) {
			LogUtils.i("实例化NativeImageLoader");
			mInstance = new NativeImageLoader();
		}
		return mInstance;
	}

	/**
	 * 往内存缓存中添加Bitmap
	 */
	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null && bitmap != null) {
			mLruCache.put(key, bitmap);
		}
	}

	/**
	 * 根据key来获取内存中的图片
	 */
	private Bitmap getBitmapFromMemCache(String key) {
		return mLruCache.get(key);
	}

	@Override
	public Bitmap getBitmap(String url) {
		return getBitmapFromMemCache(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		addBitmapToMemoryCache(url, bitmap);
	}
}