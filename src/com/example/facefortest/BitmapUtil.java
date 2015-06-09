package com.example.facefortest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.face.test.bean.ClientError;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.os.Environment;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.View;

public class BitmapUtil {
	private static File root;
	private static File tmpfile;

	public static Bitmap getScaledBitmap(String fileName, int dstWidth) {
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(fileName, localOptions);
		int originWidth = localOptions.outWidth;
		int originHeight = localOptions.outHeight;

		localOptions.inSampleSize = originWidth > originHeight ? originWidth
				/ dstWidth : originHeight / dstWidth;
		localOptions.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(fileName, localOptions);
	}

	public static byte[] getBitmapByte(Context context, Bitmap bitmap) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return out.toByteArray();
	}

	public static String bitmapChangeString(Bitmap bitmap) {
		if (bitmap != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			String str = new String(Base64.encodeToString(baos.toByteArray(),
					Base64.DEFAULT));
			return str;
		}
		return null;
	}

	public static File saveBitmap(Bitmap bitmap) {
		// f.createTempFile("prefix", "suffix");
		try {
			tmpfile = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/test.png");
			FileOutputStream out = new FileOutputStream(tmpfile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
			Log.i("facetest", "create success");
		} catch (FileNotFoundException e) {
			Log.i("facetest", "FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("facetest", "create fail");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tmpfile;
	}

	public static boolean saveBitmap(Bitmap bitmap, String name) {
		// f.createTempFile("prefix", "suffix");
		File file = null;
		try {
			root = new File(Environment.getExternalStorageDirectory().getPath()
					+ "/facetest/");
			if (!root.exists()) {
				root.mkdir();
			}
			file = new File(Environment.getExternalStorageDirectory().getPath()
					+ "/facetest/" + name + ".png");
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
			Log.i("facetest", "create success");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 加水印 也可以加文字
	 * 
	 * @param src
	 * @param watermark
	 * @param title
	 * @return
	 */
	public static Bitmap watermarkBitmap(Bitmap src, List<String> title) {
		if (src == null) {
			return null;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		// 需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// Paint paint = new Paint();
		// 加入文字
		if (title != null) {
			String familyName = "宋体";
			Typeface font = Typeface.create(familyName, Typeface.NORMAL);
			TextPaint textPaint = new TextPaint();
			textPaint.setColor(Color.RED);
			textPaint.setTypeface(font);
			textPaint.setTextSize(22);
			// 这里是自动换行的
			// StaticLayout layout = new
			// StaticLayout(title,textPaint,w,Alignment.ALIGN_OPPOSITE,1.0F,0.0F,true);
			// layout.draw(cv);
			// 文字就加左上角算了
			for (int i = 0; i < title.size(); i++) {
				cv.drawText(title.get(i), 0, h - 27 * (title.size() - i),
						textPaint);
			}
		}
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储
		return newb;
	}

	public static Bitmap watermarkBitmap(Bitmap src, String title) {
		if (src == null) {
			return null;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		// 需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// Paint paint = new Paint();
		// 加入文字
		if (title != null) {
			String familyName = "宋体";
			Typeface font = Typeface.create(familyName, Typeface.NORMAL);
			TextPaint textPaint = new TextPaint();
			textPaint.setColor(Color.RED);
			textPaint.setTypeface(font);
			textPaint.setTextSize(22);
			// 这里是自动换行的
			// StaticLayout layout = new
			// StaticLayout(title,textPaint,w,Alignment.ALIGN_OPPOSITE,1.0F,0.0F,true);
			// layout.draw(cv);
			// 文字就加左上角算了

			cv.drawText(title, 0, h - 27, textPaint);

		}
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储
		return newb;
	}

	public static void deletefile() {
		tmpfile.delete();
		Log.i("facetest", "delete success");
	}

	public static Bitmap convertViewToBitmap(View view) {
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}
	public static Bitmap getImageFromAssetsFile(Context context, String fileName) {   
	    Bitmap image = null;   
	    AssetManager am = context.getResources().getAssets();   
	    try {   
	        InputStream is = am.open(fileName);   
	        image = BitmapFactory.decodeStream(is);   
	        is.close();   
	    } catch (IOException e) {   
	        e.printStackTrace();   
	    }   
	    return image;   
	}
	
	
	public static Bitmap oldRemeber(Bitmap bmp)  
    {  
        // 速度测试  
        long start = System.currentTimeMillis();  
        int width = bmp.getWidth();  
        int height = bmp.getHeight();  
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);  
        int pixColor = 0;  
        int pixR = 0;  
        int pixG = 0;  
        int pixB = 0;  
        int newR = 0;  
        int newG = 0;  
        int newB = 0;  
        int[] pixels = new int[width * height];  
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);  
        for (int i = 0; i < height; i++)  
        {  
            for (int k = 0; k < width; k++)  
            {  
                pixColor = pixels[width * i + k];  
                pixR = Color.red(pixColor);  
                pixG = Color.green(pixColor);  
                pixB = Color.blue(pixColor);  
                newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);  
                newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);  
                newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);  
                int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);  
                pixels[width * i + k] = newColor;  
            }  
        }  
          
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
        long end = System.currentTimeMillis();  
        Log.d("may", "used time="+(end - start));  
        return bitmap;  
    } 
	
	
	/** 
     * 模糊效果 
     * @param bmp 
     * @return 
     */  
    public static  Bitmap blurImage(Bitmap bmp)  
    {  
        int width = bmp.getWidth();  
        int height = bmp.getHeight();  
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);  
          
        int pixColor = 0;  
          
        int newR = 0;  
        int newG = 0;  
        int newB = 0;  
          
        int newColor = 0;  
          
        int[][] colors = new int[9][3];  
        for (int i = 1, length = width - 1; i < length; i++)  
        {  
            for (int k = 1, len = height - 1; k < len; k++)  
            {  
                for (int m = 0; m < 9; m++)  
                {  
                    int s = 0;  
                    int p = 0;  
                    switch(m)  
                    {  
                    case 0:  
                        s = i - 1;  
                        p = k - 1;  
                        break;  
                    case 1:  
                        s = i;  
                        p = k - 1;  
                        break;  
                    case 2:  
                        s = i + 1;  
                        p = k - 1;  
                        break;  
                    case 3:  
                        s = i + 1;  
                        p = k;  
                        break;  
                    case 4:  
                        s = i + 1;  
                        p = k + 1;  
                        break;  
                    case 5:  
                        s = i;  
                        p = k + 1;  
                        break;  
                    case 6:  
                        s = i - 1;  
                        p = k + 1;  
                        break;  
                    case 7:  
                        s = i - 1;  
                        p = k;  
                        break;  
                    case 8:  
                        s = i;  
                        p = k;  
                    }  
                    pixColor = bmp.getPixel(s, p);  
                    colors[m][0] = Color.red(pixColor);  
                    colors[m][1] = Color.green(pixColor);  
                    colors[m][2] = Color.blue(pixColor);  
                }  
                  
                for (int m = 0; m < 9; m++)  
                {  
                    newR += colors[m][0];  
                    newG += colors[m][1];  
                    newB += colors[m][2];  
                }  
                  
                newR = (int) (newR / 9F);  
                newG = (int) (newG / 9F);  
                newB = (int) (newB / 9F);  
                  
                newR = Math.min(255, Math.max(0, newR));  
                newG = Math.min(255, Math.max(0, newG));  
                newB = Math.min(255, Math.max(0, newB));  
                  
                newColor = Color.argb(255, newR, newG, newB);  
                bitmap.setPixel(i, k, newColor);  
                  
                newR = 0;  
                newG = 0;  
                newB = 0;  
            }  
        }  
          
        return bitmap;  
    } 

}
