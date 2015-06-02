package com.example.facefortest.bitch;

import java.util.List;

import com.example.facefortest.FaceApplication;
import com.example.facefortest.R;
import com.example.facefortest.R.drawable;
import com.example.facefortest.R.id;
import com.example.facefortest.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BitchsAdapter extends BaseAdapter{
	private Context context;
	private static LayoutInflater inflater;
	private List<Bitchs> list;
	private int width;
	
	public BitchsAdapter(Context context,List<Bitchs> list){
		this.context=context;
		this.list=list;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = inflater.inflate(R.layout.lv_item, null);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.imageView1);
		
		imageView.setLayoutParams(new LinearLayout.LayoutParams(width / 3,
				width / 3));
		if (list.get(position).getFile()!=null) {
			FaceApplication.displayImage(list.get(position).getFile().getFileUrl(context), imageView);
		}else {
			imageView.setImageResource(R.drawable.error);
		}
		return convertView;
	}

}
