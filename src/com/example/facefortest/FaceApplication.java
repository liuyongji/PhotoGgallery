package com.example.facefortest;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
		configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.threadPoolSize(3).discCacheFileCount(100).build();
		imageLoader.init(configuration);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading).cacheInMemory(true)
				.cacheOnDisc(true).build();
	}
	public static void displayImage(String url,ImageView imageView){
		imageLoader.displayImage(url, imageView, options);
	} 

}
