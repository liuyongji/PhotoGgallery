package com.example.facefortest;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {
	private List<String> list = new ArrayList<String>();
	private List<String> list_id=new ArrayList<String>();
	private int total = 0;
	private ImageAdapter imageAdapter;
	private GridView mGridView;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Bmob.initialize(this, "6bb1226b16bb29f5b8e3b71621af32fc");
		mGridView = (GridView) findViewById(R.id.gv_content);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				imageBrower(position, list);
			}
		});
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Person person =new Person();
				person.setObjectId(list_id.get(position));
				person.delete(MainActivity.this,new DeleteListener() {
					
					@Override
					public void onSuccess() {
						list.remove(position);
						list_id.remove(position);
						imageAdapter.notifyDataSetChanged();
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}
				});
				
				return true;
			}
		});
		mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.id_swipe_ly);
		mSwipeRefreshLayout.setOnRefreshListener(this);  
		new GetDataTask().execute(total);

	}
	private void imageBrower(int position, List<String> list) {
		Intent intent = new Intent(MainActivity.this, ImagePagerActivity.class);
		intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList<String>) list);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
	}

	private class GetDataTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			BmobQuery<Person> bmobQuery = new BmobQuery<Person>();
			bmobQuery.setSkip(total);
			bmobQuery.order("-createdAt");
			bmobQuery.findObjects(MainActivity.this,
					new FindListener<Person>() {

						@Override
						public void onSuccess(List<Person> persons) {
							total += persons.size();
							for (int i = 0; i < persons.size(); i++) {
								if (persons.get(i).getFile() != null) {
									list.add(0,persons.get(i).getFile()
											.getFileUrl());
									list_id.add(0,persons.get(i).getObjectId());
//									Log.i("MainActivity", persons.get(i)
//											.getFile().getFileUrl());
								}
							}
							imageAdapter = new ImageAdapter(MainActivity.this,
									list);
							if (imageAdapter != null) {
								mGridView.setAdapter(imageAdapter);
							}
							mSwipeRefreshLayout.setRefreshing(false);
							imageAdapter.notifyDataSetChanged();
						}

						@Override
						public void onError(int arg0, String arg1) {
							toast(arg1);
						}
					});
			return null;
		}
	}

	public void toast(String string) {
		Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onRefresh() {
		new GetDataTask().execute(total);
	}

}
