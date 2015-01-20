package com.example.facefortest;

import java.util.List;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{
	private ImageLoaderConfiguration configuration;
	private Activity activity;
	private static LayoutInflater inflater;
	private ImageLoader imageLoader=ImageLoader.getInstance();
	private DisplayImageOptions options;
	private List<String> list;
	
	// 构造函数
	@SuppressWarnings({ "deprecation" })
	public ImageAdapter(Activity a,final List<String> urls){
		activity=a;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		list=urls;
		configuration=new ImageLoaderConfiguration.Builder(activity).
				threadPoolSize(3).
				discCacheFileCount(100).
				build();
		imageLoader.init(configuration);
		options=new DisplayImageOptions.Builder().
//				showImageOnLoading(R.drawable.loading).
//				showImageForEmptyUri(R.drawable.failed).
				cacheInMemory(true).
				cacheOnDisc(true).
				build();
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自动生成的方法存根
		ViewHolder holder = null;  

//		View vi=convertView;  
        if(convertView==null){ 
        	holder = new ViewHolder(); 
//        	convertView = inflater.inflate(R.layout.listview_items, null); 
//            holder.imageView=(ImageView)convertView.findViewById(R.id.imageitems);
            convertView.setTag(holder);
            
        }else {
        	holder = (ViewHolder) convertView.getTag();  
		}
        holder.imageView.setTag(list.get(position));
        imageLoader.displayImage(list.get(position), holder.imageView,options);
        return convertView;
	}
	public final class ViewHolder {
        public ImageView imageView;
    }
	public void add(String string){
		this.list.add(string);
	}

}
