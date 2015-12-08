package com.linkage.mobile72.sh.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;

import com.linkage.lib.util.LogUtils;

public class BitmapUtils {
	
//	public static final long MAX_CODE_LENGTH = 2 * 1024 * 1024;
//	public static final long MAX_COMPRESS_LENGTH = 1024 * 1024;
	public static final long MAX_CODE_LENGTH = 200 * 1024;
	public static final long MAX_COMPRESS_LENGTH = 200 * 1024;

	public static final int BITMAP_SIZE_HIGHT = 480;
	public static final int BITMAP_SIZE_MID = 320;
	public static final int BITMAP_SIZE_LOW = 240;
	public static final int BITMAP_SIZE_AVATAR = 120;

	public static final int BITMAP_QUALITY = 60;

	public static void compress(File file, int size, int quality)
			throws IOException {
		// 压缩大小

		FileInputStream stream = new FileInputStream(file);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		// 获取这个图片的宽和高
		Bitmap bitmap = BitmapFactory.decodeStream(stream, null, opts);// 此时返回bm为空
		stream.close();
		opts.inJustDecodeBounds = false;
		// 计算缩放比
		/*
		 * int be = (int) (opts.outHeight / (float) size); if (be <= 0) be = 1;
		 * opts.inSampleSize = be;
		 */
		opts.inSampleSize = calculateInSampleSize(opts, 400, 600);
		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
		stream = new FileInputStream(file);
		bitmap = BitmapFactory.decodeStream(stream, null, opts);
		stream.close();
		// 删除文件
		file.delete();
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream outstream = new FileOutputStream(file);
		if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outstream)) {
			outstream.flush();
			outstream.close();
		}
	}

	/**
	 * Android图片缩放
	 * 
	 * @param drawable
	 * @param w
	 * @param h
	 * @return
	 */
	public static Drawable zoomDrawable(Drawable drawable) {

		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();

		Bitmap oldbmp = drawableToBitmap(drawable); // drawable转换成bitmap
		Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
		if (width > 480) {
			float scaleWidth = ((float) 480 / width); // 计算缩放比例
			// float scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidth, scaleWidth); // 设置缩放比例
		}
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true); // 建立新的bitmap，其内容是对原bitmap的缩放后的图

		return new BitmapDrawable(newbmp); // 把bitmap转换成drawable并返回

	}

	public static Bitmap drawableToBitmap(Drawable drawable) {// drawable

		int width = drawable.getIntrinsicWidth(); // 取drawable的长宽
		int height = drawable.getIntrinsicHeight();

		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565; // 取drawable的颜色格式
		Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应bitmap
		Canvas canvas = new Canvas(bitmap); // 建立对应bitmap的画布
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas); // 把drawable内容画到画布中

		return bitmap;

	}

	// 计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			inSampleSize = inSampleSize < 1 ? 1 : inSampleSize;
		}
		return inSampleSize;
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处：
	 * 1.使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
	 * 2.缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth > beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		
		Bitmap newBitmap = null;
		
		if (bitmap != null) {
			String orientation = getExifOrientation(imagePath, "0");

			Matrix matrix = new Matrix();
			matrix.postRotate(Float.valueOf(orientation));

			newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, false);
		}
		
		return newBitmap;
		
//		if (newBitmap != null) {
//			 // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
//			newBitmap = ThumbnailUtils.extractThumbnail(newBitmap, width, height,
//						ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//			return newBitmap;
//		} else {
//			return null;
//		}
		
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
		Bitmap newBitmap = null;
		
		if (bitmap != null) {
			String orientation = getExifOrientation(imagePath, "0");

			Matrix matrix = new Matrix();
			matrix.postRotate(Float.valueOf(orientation));

			newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, false);
		}
		return newBitmap;
		
	}

	public static String getExifOrientation(String path, String orientation) {
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
	
	/**
	 * @param tempDirPath 项目内 临时图片文件处理目录
	 * @param nativePath 待处理(压缩、扶正)的本地图片文件路径
	 * @return 党原文件不存在 返回null 当处理失败 均返回原图片文件路径 当处理成功 返回处理后新图片路径
	 */
	public static String handleLocalBitmapFile(String nativePath, String tempDirPath)
    {
    	File nativeFile = new File(nativePath);
    	long nativeSize = nativeFile.length();
    	if (!nativeFile.exists() || nativeSize == 0) 
    	{
    		LogUtils.e("图片文件不存在");
			return null;
		}
    	String orientation = getExifOrientation(nativePath, "0");
    	if ("0".equals(orientation)) 
    	{
			if (nativeSize < MAX_CODE_LENGTH) {
				return nativePath;
			}
			else {
				Bitmap resultBitmap = condensationBitmapFile(nativeFile, nativeSize, MAX_CODE_LENGTH);
				if (resultBitmap == null) {
					return nativePath;
				}
				else 
				{
					String aimPath = createFileNameByTime(tempDirPath);
					if (writeImageFile(aimPath, resultBitmap)) 
					{
						return aimPath;
					} else 
					{
						return nativePath;
					}
				}
			}
		} 
    	else 
		{
    		Bitmap standedBitmap = standBitmap(orientation, condensationBitmapFile(nativeFile, nativeSize, MAX_COMPRESS_LENGTH));
    		if (standedBitmap == null) {
				return nativePath;
			}
			else 
			{
				String standedPath = createFileNameByTime(tempDirPath);
				if (writeImageFile(standedPath, standedBitmap)) 
				{
					return standedPath;
				} else 
				{
					return nativePath;
				}
			}
		}
        
    }
	
	private static Bitmap condensationBitmapFile(File nativeFile, long nativeSize, long aimSize)
	{
		FileInputStream fileinputstream = null;
        Bitmap bitmap = null;
        // 计算缩放比 
        int be = 1;
        while (nativeSize > aimSize) 
        {
        	nativeSize /= 4;
			be *= 2;
		}
		try
        {
            fileinputstream = new FileInputStream(nativeFile);
            FileDescriptor filedescriptor = fileinputstream.getFD();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            LogUtils.e("压缩比例为"+be);
            options.inSampleSize = be;
            // 重新读入图片，注意这次要把options.inJustDecodeBounds设为false哦
            bitmap = BitmapFactory.decodeFileDescriptor(filedescriptor, null, options);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
        	try 
        	{
				fileinputstream.close();
			} 
        	catch (IOException e) 
        	{
				e.printStackTrace();
			}
        }
		return bitmap;
	}
	
	private static Bitmap standBitmap(String orientation, Bitmap bitmap)
	{
		if (bitmap == null) {
			return null;
		}
		Matrix matrix = new Matrix();
		matrix.postRotate(Float.valueOf(orientation));
		Bitmap resultBitmap = null;
		try
        {
			resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, false);
        }
		catch (OutOfMemoryError ex)
        {
        	ex.printStackTrace();
        	return bitmap;
            // 如何出现了内存不足异常，最好return 原始的bitmap对象。.
        }
		if (resultBitmap != null) {
			return resultBitmap;
		} else {
			return bitmap;
		}
	}
	
	public static boolean writeImageFile(String aimPath, Bitmap bitmap)
    {
        if (bitmap == null)
        {
            return false;
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try
        {
            fos = new FileOutputStream(aimPath);
            bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            try
            {
            	if (bos != null)
                {
                    bos.close();
                }
                if (fos != null)
                {
                    fos.close();
                }
            }
            catch (IOException e)
            {
            	e.printStackTrace();
            }
        }
    }
	
	private static String createFileNameByTime(String tempDirPath) {
		String filaName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Calendar.getInstance().getTime())+UUID.randomUUID() + ".jpg";
		File aimFile = new File(tempDirPath, filaName);
		return aimFile.getAbsolutePath();
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
	
}
