package com.linkage.mobile72.sh.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.utils.multipic.FileTraversal;
import com.linkage.mobile72.sh.utils.multipic.ImgCallBack;
import com.linkage.lib.util.LogUtils;

public class Utils {

	private Context context;

	public Utils(Context context) {
		this.context = context;
	}

	public static SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
	
	public static SimpleDateFormat FORMAT_MM_DD_HH_mm_ss_SSS = new SimpleDateFormat(
			"MM-dd hh:mm:ss秒SSS", Locale.CHINA);

	/**
	 * @param defString 2015-06-15 16:22:08
	 * @return Date
	 */
	public static Date getDateFromDefString(String defString)
	{
	    Date date = null;
       
        try
        {
            date = DEFAULT_FORMAT.parse(defString);
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
	}
	/**
	 * 获取屏幕高度
	 */
	public static int getWindowHeight(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	/**
	 * 获取屏幕宽度
	 */
	public static int getWindowWidth(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	public static int dp2px(Context context, float dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static boolean checkApkExist(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName))
			return false;
		try {
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			if (info == null) {
				return false;
			} else {
				return true;
			}
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	public static void runApp(Context context, String packageName) {
		PackageInfo pi;
		try {
			pi = context.getPackageManager().getPackageInfo(packageName, 0);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			// resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);
			PackageManager pManager = context.getPackageManager();
			List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				packageName = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// intent.addCategory(Intent.CATEGORY_LAUNCHER);

				ComponentName cn = new ComponentName(packageName, className);

				intent.setComponent(cn);
				context.startActivity(intent);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void runAppByParam(Context context, String packageName, String launchUrl, Map<String, Object> params) {
		LogUtils.e("packageName:" + packageName);
		ComponentName componetName = new ComponentName(  
                //这个是另外一个应用程序的包名  
				packageName,  
                //这个参数是要启动的Activity  
				launchUrl);  
        try {
            Intent intent = new Intent();
            intent.setComponent(componetName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(params != null && params.size() > 0) {
            	Set<Map.Entry<String, Object>> set = params.entrySet();
                for (Iterator<Map.Entry<String, Object>> it = set.iterator(); it.hasNext();) {
                    Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
                    intent.putExtra(entry.getKey(), entry.getValue().toString());
                }
            }
            context.startActivity(intent);  
        } catch (Exception e) {  
        	UIUtilities.showToast(context, "应用未安装或路径不存在");
        }  
	}
	
	public static void runAppByParam(Context context, String packageName, Map<String, Object> params) {
		PackageInfo pi;
		try {
			pi = context.getPackageManager().getPackageInfo(packageName, 0);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);
			PackageManager pManager = context.getPackageManager();
			List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				packageName = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;

				Intent intent = new Intent(Intent.ACTION_MAIN);
				// intent.addCategory(Intent.CATEGORY_LAUNCHER);

				ComponentName cn = new ComponentName(packageName, className);

				intent.setComponent(cn);
				if(params != null && params.size() > 0) {
	            	Set<Map.Entry<String, Object>> set = params.entrySet();
	                for (Iterator<Map.Entry<String, Object>> it = set.iterator(); it.hasNext();) {
	                    Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
	                    intent.putExtra(entry.getKey(), entry.getValue().toString());
	                }
	            }
				context.startActivity(intent);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}   
	}

	public static PackageInfo getPackageInfoByName(Context context, String packageName) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		return packageInfo;
	}

	/**
	 */
	public static String getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return context.getString(R.string.app_name);
		}
	}

	public static String getVersionCode(Context context) {// 获取版本号(内部识别号)
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return String.valueOf(pi.versionCode);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getAppName(Context context, String packageName) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(packageName, 0);
			String name = info.applicationInfo.loadLabel(context.getPackageManager()).toString();
			return name;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static Drawable getAppDrawable(Context context, String packageName) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(packageName, 0);
			Drawable icon = info.applicationInfo.loadIcon(context.getPackageManager());
			return icon;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String format(long milliseconds) {
		Date d = new Date(milliseconds);
		return FORMAT_MM_DD_HH_mm_ss_SSS.format(d);
	}

	public static String defaultFormat(Date date) {
		if(date != null) {
			return DEFAULT_FORMAT.format(date);
		}else {
			return DEFAULT_FORMAT.format(new Date());
		}
	}
	
	public static String removeSpecilChar(String str) {
		String result = "";
		if (null != str) {
			Pattern pat = Pattern.compile("\\s*|\n|\r|\t");
			Matcher mat = pat.matcher(str);
			result = mat.replaceAll("");
		}
		return result;
	}

	public ArrayList<String> listAlldir() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		Uri uri = intent.getData();
		ArrayList<String> list = new ArrayList<String>();
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);// managedQuery(uri,
																						// proj,
																						// null,
																						// null,
																						// null);
		while (cursor.moveToNext()) {
			String path = cursor.getString(0);
			list.add(new File(path).getAbsolutePath());
		}
		return list;
	}

	public List<FileTraversal> LocalImgFileList() {
		List<FileTraversal> data = new ArrayList<FileTraversal>();
		String filename = "";
		List<String> allimglist = listAlldir();
		List<String> retulist = new ArrayList<String>();
		if (allimglist != null) {
			Set set = new TreeSet();
			String[] str;
			for (int i = 0; i < allimglist.size(); i++) {
				retulist.add(getfileinfo(allimglist.get(i)));
			}
			for (int i = 0; i < retulist.size(); i++) {
				set.add(retulist.get(i));
			}
			str = (String[]) set.toArray(new String[0]);
			for (int i = 0; i < str.length; i++) {
				filename = str[i];
				FileTraversal ftl = new FileTraversal();
				ftl.filename = filename;
				data.add(ftl);
			}

			for (int i = 0; i < data.size(); i++) {
				for (int j = 0; j < allimglist.size(); j++) {
					if (data.get(i).filename.equals(getfileinfo(allimglist.get(j)))) {
						data.get(i).filecontent.add(allimglist.get(j));
					}
				}
			}
		}
		return data;
	}

	public Bitmap getPathBitmap(Uri imageFilePath, int dw, int dh) throws FileNotFoundException {

		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;

		Bitmap pic = BitmapFactory.decodeStream(
				context.getContentResolver().openInputStream(imageFilePath), null, op);

		int wRatio = (int) Math.ceil(op.outWidth / (float) dw);
		int hRatio = (int) Math.ceil(op.outHeight / (float) dh);

		if (wRatio > 1 && hRatio > 1) {
			if (wRatio > hRatio) {
				op.inSampleSize = wRatio;
			} else {
				op.inSampleSize = hRatio;
			}
		}
		op.inJustDecodeBounds = false;
		pic = BitmapFactory.decodeStream(context.getContentResolver()
				.openInputStream(imageFilePath), null, op);

		return pic;
	}

	public String getfileinfo(String data) {
		String filename[] = data.split("/");
		if (filename != null) {
			return filename[filename.length - 2];
		}
		return null;
	}

	public void imgExcute(ImageView imageView, ImgCallBack icb, String... params) {
		LoadBitAsynk loadBitAsynk = new LoadBitAsynk(imageView, icb);
		loadBitAsynk.execute(params);
	}

	public class LoadBitAsynk extends AsyncTask<String, Integer, Bitmap> {

		ImageView imageView;
		ImgCallBack icb;

		LoadBitAsynk(ImageView imageView, ImgCallBack icb) {
			this.imageView = imageView;
			this.icb = icb;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = null;
			try {
				if (params != null) {
					for (int i = 0; i < params.length; i++) {
						bitmap = getPathBitmap(Uri.fromFile(new File(params[i])), 200, 200);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null) {
				// imageView.setImageBitmap(result);
				icb.resultImgCall(imageView, result);
			}
		}

	}

	/**
	 * 动态设置ListView的高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null)
			return;

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
}
