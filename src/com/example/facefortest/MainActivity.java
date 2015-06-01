package com.example.facefortest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mirko.android.datetimepicker.date.DatePickerDialog;
import mirko.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity implements
		SwipeRefreshLayout.OnRefreshListener {
	private List<String> list = new ArrayList<String>();
	private List<String> list_id = new ArrayList<String>();
	private int total = 0;
	private ImageAdapter imageAdapter;
	private GridView mGridView;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private final Calendar mCalendar = Calendar.getInstance();
	private boolean mlimit = false;
	private Date date;
	private boolean mcollect = false;

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

				imageBrower(position, list, list_id.get(position));
			}
		});
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Person person = new Person();
				person.setObjectId(list_id.get(position));
				person.delete(MainActivity.this, new DeleteListener() {

					@Override
					public void onSuccess() {
						list.remove(position);
						list_id.remove(position);
						total--;
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
					mcollect=false;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.main, menu);
		return true;// 返回false就不会显示菜单
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_settings:
			datePickerDialog.show(getFragmentManager(), "pick");
			break;
		case R.id.bitchs:
			Intent intent =new Intent(MainActivity.this,BitchsActivity.class);
			startActivity(intent);
			
			break;
		case R.id.collect:
			mcollect = true;
			mlimit = false;
			total = 0;
			list.clear();
			new GetDataTask().execute(total);
			break;

		default:
			break;
		}

		return true;
	}

	private void imageBrower(int position, List<String> list, String id) {
		Intent intent = new Intent(MainActivity.this, ImagePagerActivity.class);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_Ids, id);
		intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,
				(ArrayList<String>) list);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
	}
	
	

	private class GetDataTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			BmobQuery<Person> bmobQuery = new BmobQuery<Person>();
			if (mlimit) {
				BmobQuery<Person> query1 = new BmobQuery<Person>();
				query1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(
						date));
				BmobQuery<Person> query2 = new BmobQuery<Person>();
				query2.addWhereLessThan("createdAt",
						new BmobDate(Utils.addDate(date)));

				List<BmobQuery<Person>> query3 = new ArrayList<BmobQuery<Person>>();
				query3.add(query1);
				query3.add(query2);
				bmobQuery.and(query3);
			}
			if (mcollect) {
				bmobQuery.addWhereEqualTo("like", true);
			}
			bmobQuery.setSkip(total);
			bmobQuery.order("-createdAt");
			bmobQuery.findObjects(MainActivity.this,
					new FindListener<Person>() {

						@Override
						public void onSuccess(List<Person> persons) {
							if (persons==null||persons.size()==0) {
								toast("已经到头了");
								mSwipeRefreshLayout.setRefreshing(false);
								return;
							}
							total += persons.size();
							for (int i = 0; i < persons.size(); i++) {
								if (persons.get(i).getFile() != null) {
									list.add(0, persons.get(i).getFile()
											.getFileUrl(MainActivity.this));
									list_id.add(0, persons.get(i).getObjectId());
									// Log.i("MainActivity", persons.get(i)
									// .getFile().getFileUrl());
								}
							}
							imageAdapter = new ImageAdapter(MainActivity.this,
									list);
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
		Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onRefresh() {
		new GetDataTask().execute();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}


}
