package com.example.facefortest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mirko.android.datetimepicker.date.DatePickerDialog;
import mirko.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;


import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BitchsActivity extends Activity implements
		SwipeRefreshLayout.OnRefreshListener {
	private int total = 0;
	private List<String> list = new ArrayList<String>();
	private List<String> list_id = new ArrayList<String>();
	private GridView mGridView;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private BitchsAdapter imageAdapter;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private boolean mlimit = false;
	private Date date;
	private final Calendar mCalendar = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGridView = (GridView) findViewById(R.id.gv_content);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				imageBrower(position, list, list_id.get(position));
			}
		});
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		new GetDataTask().execute(total);
	}
	final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
			new OnDateSetListener() {

				public void onDateSet(DatePickerDialog datePickerDialog,
						int year, int month, int day) {
					total = 0;
					mlimit = true;
					list.clear();
					String datesString = Utils.pad(year) + "-"
							+ Utils.pad(month + 1) + "-" + Utils.pad(day);
					date = null;
					try {
						date = sdf.parse(datesString);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					new GetDataTask().execute(total);
				}

			}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
			mCalendar.get(Calendar.DAY_OF_MONTH));

	private class GetDataTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			BmobQuery<Bitchs> bmobQuery = new BmobQuery<Bitchs>();
			if (mlimit) {
				BmobQuery<Bitchs> query1 = new BmobQuery<Bitchs>();
				query1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(
						date));
				BmobQuery<Bitchs> query2 = new BmobQuery<Bitchs>();
				query2.addWhereLessThan("createdAt",
						new BmobDate(Utils.addDate(date)));

				List<BmobQuery<Bitchs>> query3 = new ArrayList<BmobQuery<Bitchs>>();
				query3.add(query1);
				query3.add(query2);
				bmobQuery.and(query3);
			}

			bmobQuery.setSkip(total);
			bmobQuery.setLimit(100);
			bmobQuery.order("-createdAt");
			bmobQuery.findObjects(BitchsActivity.this,
					new FindListener<Bitchs>() {

						@Override
						public void onSuccess(List<Bitchs> persons) {
							if (persons == null || persons.size() == 0) {
								toast("已经到头了");
								mSwipeRefreshLayout.setRefreshing(false);
								return;
							}
							total += persons.size();
							for (int i = 0; i < persons.size(); i++) {
								if (persons.get(i).getFile() != null) {
									list.add(0, persons.get(i).getFile()
											.getFileUrl(BitchsActivity.this));
									list_id.add(0, persons.get(i).getObjectId());
								}
							}
							imageAdapter = new BitchsAdapter(
									BitchsActivity.this, list);
							toast(persons.get(persons.size() - 1)
									.getCreatedAt());
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
		Toast.makeText(BitchsActivity.this, string, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onRefresh() {
		new GetDataTask().execute();
	}

	private void imageBrower(int position, List<String> list, String id) {
		Intent intent = new Intent(BitchsActivity.this,
				ImagePagerActivity.class);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_Ids, id);
		intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,
				(ArrayList<String>) list);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 0, "日期");
		return true;// 返回false就不会显示菜单
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		datePickerDialog.show(getFragmentManager(), "pick");
		return true;
	}

}
