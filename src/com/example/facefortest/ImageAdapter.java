package com.example.facefortest;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageAdapter extends BaseAdapter {
	private Activity activity;
	private static LayoutInflater inflater;
//	private List<String> list;
	private int width;
	
	private List<Person> list;
	

	public ImageAdapter(Activity a, final List<Person> urls) {
		activity = a;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		list = urls;
		WindowManager wm = (WindowManager) activity
				.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
	}
	

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.lv_item, null);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.imageView1);
		
		imageView.setLayoutParams(new LinearLayout.LayoutParams(width / 3,
				width / 3));
		if (list.get(position).getFile()!=null) {
//			list.get(position).getFile().loadImageThumbnail(activity, imageView, 300, 300);
			FaceApplication.displayImage(list.get(position).getFile().getFileUrl(activity), imageView);
		}else {
			imageView.setImageResource(R.drawable.error);
		}
		
		
		return convertView;
	}

}
