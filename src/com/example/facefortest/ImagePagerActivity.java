package com.example.facefortest;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

import com.example.facefortest.R;
import com.loveplusplus.demo.image.HackyViewPager;
import com.loveplusplus.demo.image.ImageDetailFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class ImagePagerActivity extends FragmentActivity {
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	public static final String EXTRA_IMAGE_Ids = "image_ids";

	private HackyViewPager mPager;
	private int pagerPosition;
	private TextView indicator;
	private String ids;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager);

		pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		ArrayList<String> urls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
		ids = getIntent().getStringExtra(EXTRA_IMAGE_Ids);

		mPager = (HackyViewPager) findViewById(R.id.pager);
		ImagePagerAdapter mAdapter = new ImagePagerAdapter(
				getSupportFragmentManager(), urls);
		mPager.setAdapter(mAdapter);
		indicator = (TextView) findViewById(R.id.indicator);

		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
				.getAdapter().getCount());
		indicator.setText(text);
		// 更新下标
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				CharSequence text = getString(R.string.viewpager_indicator,
						arg0 + 1, mPager.getAdapter().getCount());
				indicator.setText(text);
			}

		});
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		mPager.setCurrentItem(pagerPosition);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public List<String> fileList;

		public ImagePagerAdapter(FragmentManager fm, List<String> fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.size();
		}

		@Override
		public Fragment getItem(int position) {
			String url = fileList.get(position);
			return ImageDetailFragment.newInstance(url);
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.main2, menu);
		return true;// 返回false就不会显示菜单
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Person person = new Person();
		person.setObjectId(ids);
		person.setLike(true);
		person.update(ImagePagerActivity.this, new UpdateListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(ImagePagerActivity.this, "success", Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(ImagePagerActivity.this, arg0+"  "+arg1, Toast.LENGTH_LONG).show();
			}
		});
		
		return true;
	}

}