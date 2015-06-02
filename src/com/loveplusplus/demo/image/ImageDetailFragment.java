package com.loveplusplus.demo.image;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import a.This;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.example.facefortest.FaceApplication;
import com.example.facefortest.R;

public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private PhotoView mImageView;
	private PhotoViewAttacher mAttacher;
	private String mcreate;

	public static ImageDetailFragment newInstance(String imageUrl,String createtime) {
		final ImageDetailFragment f = new ImageDetailFragment();
		final Bundle args = new Bundle();
		args.putString("time", createtime);
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
		mcreate=getArguments() != null ? getArguments().getString("time") : "nooo";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
		mImageView = (PhotoView) v.findViewById(R.id.image);
		mAttacher = new PhotoViewAttacher(mImageView);
		mAttacher.setZoomable(true);
		mAttacher.setScaleType(ScaleType.CENTER_INSIDE);
//		mAttacher.update();
		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
			
			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
//				Toast.makeText(getActivity(), mcreate, Toast.LENGTH_SHORT).show();
				getActivity().finish();
			}
		});
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		FaceApplication.displayImage(mImageUrl, mImageView);
		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		 mAttacher.cleanup();
	}

}
