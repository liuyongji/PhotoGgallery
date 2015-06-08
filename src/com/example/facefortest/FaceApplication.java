package com.example.facefortest;

import java.io.File;

import cn.bmob.v3.Bmob;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Application;
import android.widget.ImageView;

public class FaceApplication extends Application {
	private ImageLoaderConfiguration configuration;
	private static ImageLoader imageLoader = ImageLoader.getInstance();
	private static DisplayImageOptions options;

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
		options = new DisplayImageOptions.Builder()
		
//		.discCache(new UnlimitedDiscCache(StorageUtils.getCacheDirectory(getApplicationContext())))
				.showImageOnLoading(R.drawable.loading)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.cacheOnDisc(true).build();
	}
	public static void displayImage(String url,ImageView imageView){
		imageLoader.displayImage(url, imageView, options);
	}

}
