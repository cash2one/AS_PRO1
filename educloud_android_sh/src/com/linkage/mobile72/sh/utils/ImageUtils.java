package com.linkage.mobile72.sh.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.linkage.mobile72.sh.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageUtils {
	static ImageLoader sImageLoader = ImageLoader.getInstance();

//	public static final DisplayImageOptions DISYPLAY_OPTION_AVATAR_TEACHER = new DisplayImageOptions.Builder()
//			.showStubImage(R.drawable.avatar_teacher)
//			.showImageForEmptyUri(R.drawable.avatar_teacher)
//			.showImageOnFail(R.drawable.avatar_teacher).cacheInMemory()
//			.bitmapConfig(Bitmap.Config.RGB_565)
//			.displayer(new RoundedBitmapDisplayer(10)).build();

	public static final DisplayImageOptions DISYPLAY_OPTION_AVATAR_PARENT = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_user)
			.showImageForEmptyUri(R.drawable.default_user)
			.showImageOnFail(R.drawable.default_user).cacheInMemory()
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new RoundedBitmapDisplayer(10)).build();

	public static final DisplayImageOptions DISYPLAY_OPTION_AVATAR = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_user)
			.showImageForEmptyUri(R.drawable.default_user)
			.showImageOnFail(R.drawable.default_user).cacheInMemory()
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	public static final DisplayImageOptions DISYPLAY_OPTION_WEB_IMAGE = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.app_stub_image)
			.showImageForEmptyUri(R.drawable.app_stub_image)
			.showImageOnFail(R.drawable.app_stub_image).cacheInMemory()
			.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();

	public static final DisplayImageOptions DISYPLAY_OPTION_WEB_IMAGE1 = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.res_info_table_left)
			.showImageForEmptyUri(R.drawable.res_info_table_left)
			.showImageOnFail(R.drawable.res_info_table_left).cacheInMemory()
			.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();

	public static final DisplayImageOptions DISYPLAY_OPTION_CACHEINMEM = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.default_user)
			.showImageForEmptyUri(R.drawable.default_user)
			.showImageOnFail(R.drawable.default_user).cacheInMemory()
			.bitmapConfig(Bitmap.Config.RGB_565).build();
//	public static final DisplayImageOptions DISYPLAY_OPTION_HOMEWORK = new DisplayImageOptions.Builder()
//			.showStubImage(R.drawable.campus_homework)
//			.showImageForEmptyUri(R.drawable.campus_homework)
//			.showImageOnFail(R.drawable.campus_homework).cacheInMemory()
//			.bitmapConfig(Bitmap.Config.RGB_565).build();

	public static void displayAvatar(String url, ImageView imageView) {
		sImageLoader.displayImage(url, imageView, DISYPLAY_OPTION_AVATAR);
	}

	public static void displayAvatar(long id, ImageView imageView, int type) {
		displayAvatar("", imageView, type);
	}

	public static void displayAvatar(String url, ImageView imageView, int type) {
		// if(type == Consts.UserType.TEACHER)
		// {
		// // sImageLoader.displayImage(url, imageView,
		// DISYPLAY_OPTION_AVATAR_TEACHER);
		// sImageLoader.displayImage("", imageView,
		// DISYPLAY_OPTION_AVATAR_TEACHER);
		// }
		// else {
		sImageLoader
				.displayImage(url, imageView, DISYPLAY_OPTION_AVATAR_PARENT);
		// }
	}

	public static void displayAvatarImage() {
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				/**
				 * if 是否有头像文件 否{ if(url是否正确) 是{下载头像文件，显示本地头像文件 } } 是{
				 * if(根据文件创建时间判断是否要更新头像) 否{直接显示本地头像} 是{ 下载头像文件，显示本地头像}
				 * 
				 * }
				 */

			}
		});
	}

//	public static void disHomeWorkImage(ImageView imageView) {
//		sImageLoader.displayImage("", imageView, DISYPLAY_OPTION_HOMEWORK);
//	}

	public static void displayWebImage(String url, ImageView imageView) {
		sImageLoader.displayImage(url, imageView, DISYPLAY_OPTION_WEB_IMAGE);
	}

	public static void displayWebImage1(String url, ImageView imageView) {
		sImageLoader.displayImage(url, imageView, DISYPLAY_OPTION_WEB_IMAGE1);
	}

	public static void displayCacheInMemImage(String url, ImageView imageView) {
		sImageLoader.displayImage(url, imageView, DISYPLAY_OPTION_CACHEINMEM);
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getImagePathFromProvider(Context context, Uri uri) {
//		String[] projection = new String[] { MediaStore.Images.Media.DATA };
//		Cursor cursor = context.getContentResolver().query(uri, projection,
//				null, null, null);
////	int rowNums = cursor.getCount();
//		if (rowNums == 0) {
//			return null;
//		}
//		cursor.moveToFirst();
//		String filePath = cursor.getString(0);
//		cursor.close();
		String filePath = null;
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
		    String wholeID = DocumentsContract.getDocumentId(uri);
		    String id = wholeID.split(":")[1];
		    String[] column = { MediaStore.Images.Media.DATA };
		    String sel = MediaStore.Images.Media._ID + "=?";
		    Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
		            sel, new String[] { id }, null);
		    int columnIndex = cursor.getColumnIndex(column[0]);
		    if (cursor.moveToFirst()) {
		        filePath = cursor.getString(columnIndex);
		    }
		    cursor.close();
		}else{
		    String[] projection = { MediaStore.Images.Media.DATA };
		    Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
		    if (null == cursor) {
				return null;
			}
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    filePath = cursor.getString(column_index);
		    cursor.close();
		}
		return filePath;
	}

	public static Bitmap scaleImage(String imagePath, int requestWidth,
			int requestHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);

		options.inSampleSize = calculateInSampleSize(options, requestWidth,
				requestHeight);

		options.inJustDecodeBounds = false;

		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

		String orientation = getExifOrientation(imagePath, "0");

		Matrix matrix = new Matrix();
		matrix.postRotate(Float.valueOf(orientation));

		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, false);

		return newBitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqW, int reqH) {
		final int h = options.outHeight;
		final int w = options.outWidth;
		int inSampleSize = 1;

		if (h > reqH || w > reqW) {
			final int heightRatio = Math.round((float) h / (float) reqH);
			final int widthRatio = Math.round((float) w / (float) reqW);

			inSampleSize = Math.min(heightRatio, widthRatio);
		}

		return inSampleSize;
	}

	public static String getExifOrientation(String path, String orientation) {
		// get image EXIF orientation if Android 2.0 or higher, using reflection
		// http://developer.android.com/resources/articles/backward-compatibility.html
		Method exif_getAttribute;
		Constructor<ExifInterface> exif_construct;
		String exifOrientation = "";

		int sdk_int = 0;
		try {
			sdk_int = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (Exception e1) {
			sdk_int = 3; // assume they are on cupcake
		}
		if (sdk_int >= 5) {
			try {
				exif_construct = android.media.ExifInterface.class
						.getConstructor(new Class[] { String.class });
				Object exif = exif_construct.newInstance(path);
				exif_getAttribute = android.media.ExifInterface.class
						.getMethod("getAttribute", new Class[] { String.class });
				try {
					exifOrientation = (String) exif_getAttribute.invoke(exif,
							android.media.ExifInterface.TAG_ORIENTATION);
					if (exifOrientation != null) {
						if (exifOrientation.equals("1")) {
							orientation = "0";
						} else if (exifOrientation.equals("3")) {
							orientation = "180";
						} else if (exifOrientation.equals("6")) {
							orientation = "90";
						} else if (exifOrientation.equals("8")) {
							orientation = "270";
						}
					} else {
						orientation = "0";
					}
				} catch (InvocationTargetException ite) {
					/* unpack original exception when possible */
					orientation = "0";
				} catch (IllegalAccessException ie) {
					System.err.println("unexpected " + ie);
					orientation = "0";
				}
				/* success, this is a newer device */
			} catch (NoSuchMethodException nsme) {
				orientation = "0";
			} catch (IllegalArgumentException e) {
				orientation = "0";
			} catch (InstantiationException e) {
				orientation = "0";
			} catch (IllegalAccessException e) {
				orientation = "0";
			} catch (InvocationTargetException e) {
				orientation = "0";
			}

		}
		return orientation;
	}

	public static byte[] processUploadImage(String filePath, int maxSize) {
		Bitmap bitmap = scaleImage(filePath, maxSize, maxSize);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, out);
		bitmap.recycle();
		return out.toByteArray();
	}

	public static String getWebImage(String imageurl) {
		String url = imageurl;

		int dotIndex = imageurl.lastIndexOf('.');
		String fileExtension = "";
		fileExtension = imageurl.substring(dotIndex + 1);

		if (fileExtension.equals("thumbnail")) {
			url = imageurl.substring(0, dotIndex + 1) + "web";
		}
		return url;
	}

	public static void savePictoFile(Uri url, String filePath)
			throws IOException {
		String originalfilepath = url.getPath();
		Bitmap photo = scaleImage(originalfilepath, 400, 400);
		if (photo != null) {
			File file = new File(filePath);
			FileOutputStream outstream = new FileOutputStream(file);
			if (photo.compress(Bitmap.CompressFormat.JPEG, 100, outstream)) {
				outstream.flush();
				outstream.close();
			}
		}
	}

	/**
	 * 写图片文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
	 * 
	 * @throws IOException
	 */
	public static void saveImage(Context context, String fileName, Bitmap bitmap)
			throws IOException {
		saveImage(context, fileName, bitmap, 100);
	}

	public static void saveImage(Context context, String fileName,
			Bitmap bitmap, int quality) throws IOException {
		if (bitmap == null)
			return;

		FileOutputStream fos = context.openFileOutput(fileName,
				Context.MODE_PRIVATE);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, quality, stream);
		byte[] bytes = stream.toByteArray();
		fos.write(bytes);
		fos.close();
	}

	/**
	 * 获取bitmap
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap getBitmap(Context context, String fileName) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			fis = context.openFileInput(fileName);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

//	public static void savePictoFile(Uri url, String filePath)
//			throws IOException {
//		String originalfilepath = url.getPath();
//		Bitmap photo = scaleImage(originalfilepath, 400, 400);
//		if (photo != null) {
//			File file = new File(filePath);
//			FileOutputStream outstream = new FileOutputStream(file);
//			if (photo.compress(Bitmap.CompressFormat.JPEG, 100, outstream)) {
//				outstream.flush();
//				outstream.close();
//			}
//		}
//	}

//	public static Bitmap scaleImage(String imagePath, int requestWidth,
//			int requestHeight) {
//		final BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(imagePath, options);
//
//		options.inSampleSize = calculateInSampleSize(options, requestWidth,
//				requestHeight);
//
//		options.inJustDecodeBounds = false;
//
//		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
//		Bitmap newBitmap = null;
//
//		if (bitmap != null) {
//			String orientation = getExifOrientation(imagePath, "0");
//
//			Matrix matrix = new Matrix();
//			matrix.postRotate(Float.valueOf(orientation));
//
//			newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
//					bitmap.getHeight(), matrix, false);
//		}
//		return newBitmap;
//
//	}
//
//	public static String getExifOrientation(String path, String orientation) {
//		Method exif_getAttribute;
//		Constructor<ExifInterface> exif_construct;
//		String exifOrientation = "";
//
//		int sdk_int = 0;
//		try {
//			sdk_int = Integer.valueOf(android.os.Build.VERSION.SDK);
//		} catch (Exception e1) {
//			sdk_int = 3; // assume they are on cupcake
//		}
//		if (sdk_int >= 5) {
//			try {
//				exif_construct = android.media.ExifInterface.class
//						.getConstructor(new Class[] { String.class });
//				Object exif = exif_construct.newInstance(path);
//				exif_getAttribute = android.media.ExifInterface.class
//						.getMethod("getAttribute", new Class[] { String.class });
//				try {
//					exifOrientation = (String) exif_getAttribute.invoke(exif,
//							android.media.ExifInterface.TAG_ORIENTATION);
//					if (exifOrientation != null) {
//						if (exifOrientation.equals("1")) {
//							orientation = "0";
//						} else if (exifOrientation.equals("3")) {
//							orientation = "180";
//						} else if (exifOrientation.equals("6")) {
//							orientation = "90";
//						} else if (exifOrientation.equals("8")) {
//							orientation = "270";
//						}
//					} else {
//						orientation = "0";
//					}
//				} catch (InvocationTargetException ite) {
//					/* unpack original exception when possible */
//					orientation = "0";
//				} catch (IllegalAccessException ie) {
//					System.err.println("unexpected " + ie);
//					orientation = "0";
//				}
//				/* success, this is a newer device */
//			} catch (NoSuchMethodException nsme) {
//				orientation = "0";
//			} catch (IllegalArgumentException e) {
//				orientation = "0";
//			} catch (InstantiationException e) {
//				orientation = "0";
//			} catch (IllegalAccessException e) {
//				orientation = "0";
//			} catch (InvocationTargetException e) {
//				orientation = "0";
//			}
//
//		}
//		return orientation;
//	}
//
//	// 计算图片的缩放值
//	public static int calculateInSampleSize(BitmapFactory.Options options,
//			int reqWidth, int reqHeight) {
//		final int height = options.outHeight;
//		final int width = options.outWidth;
//		int inSampleSize = 1;
//
//		if (height > reqHeight || width > reqWidth) {
//			final int heightRatio = Math.round((float) height
//					/ (float) reqHeight);
//			final int widthRatio = Math.round((float) width / (float) reqWidth);
//			inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
//			inSampleSize = inSampleSize < 1 ? 1 : inSampleSize;
//		}
//		return inSampleSize;
//	}

}
