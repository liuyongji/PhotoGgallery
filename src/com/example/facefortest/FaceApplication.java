package com.example.facefortest;

import java.io.File;

import cn.bmob.v3.Bmob;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import a.This;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

public class FaceApplication extends Application {
	private ImageLoaderConfiguration configuration;
	private static ImageLoader imageLoader = ImageLoader.getInstance();
	private static DisplayImageOptions options;
	
	private  boolean admin=false;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Bmob.initialize(this, "6bb1226b16bb29f5b8e3b71621af32fc");
		File cacheDir =StorageUtils.getOwnCacheDirectory(this, "facetest");  
		configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.diskCache(new UnlimitedDiscCache(cacheDir))
				.threadPoolSize(3).discCacheFileCount(100).build();
		imageLoader.init(configuration);
		
	}
	public static void displayImage(String url,ImageView imageView){
//		Drawable drawable =BitmapUtil.getImageThumbnail(url, 150, 150);
		options = new DisplayImageOptions.Builder()	
		.showImageOnLoading(R.drawable.loading)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.cacheOnDisc(true).build();
		imageLoader.displayImage(url, imageView, options);
	}
	
	public  boolean getAdmin(){
		return admin;
	}
	public  void setAdmin(boolean admin){
		this.admin=admin;
	}
	
	
	

}
