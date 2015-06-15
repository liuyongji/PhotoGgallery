package com.example.facefortest.bitch;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.facefortest.FaceApplication;
import com.example.facefortest.R;
import com.example.facefortest.Utils;

import mirko.android.datetimepicker.date.DatePickerDialog;
import mirko.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class BitchsActivity extends Activity implements
		SwipeRefreshLayout.OnRefreshListener {
	private int total = 0;
	private GridView mGridView;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private BitchsAdapter imageAdapter;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private boolean mlimit = false;
	private Date date;
	private final Calendar mCalendar = Calendar.getInstance();

	private List<Bitchs> persons = new ArrayList<Bitchs>();

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
				imageBrower(position, persons);
			}
		});
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if (!((FaceApplication) getApplication()).getAdmin()) {
					toast("you are not admin");
					return false;
				}
				final Bitchs person = new Bitchs();
				BmobFile file = new BmobFile();
				file.setUrl(persons.get(position).getFile().getUrl());
				file.delete(BitchsActivity.this, new DeleteListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						person.setObjectId(persons.get(position).getObjectId());
						person.delete(BitchsActivity.this,
								new DeleteListener() {

									@Override
									public void onSuccess() {
										persons.remove(position);
										total--;
										imageAdapter.notifyDataSetChanged();

									}

									@Override
									public void onFailure(int arg0, String arg1) {
										// TODO Auto-generated method stub
										toast("删除字段失败：" + arg1);
									}
								});
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						toast("删除文件失败：" + arg0 + arg1);
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
					persons.clear();
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
			bmobQuery.setLimit(200);
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

							BitchsActivity.this.persons.addAll(0, persons);
							total += persons.size();
							imageAdapter = new BitchsAdapter(
									BitchsActivity.this,
									BitchsActivity.this.persons);
							toast(persons.get(persons.size() - 1)
									.getCreatedAt());
							mGridView.setAdapter(imageAdapter);
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

	private void imageBrower(int position, List<Bitchs> persons) {
		Intent intent = new Intent(BitchsActivity.this,
				BitchPagerActivity.class);
		intent.putExtra(BitchPagerActivity.EXTRA_IMAGE_URLS,
				(Serializable) persons);
		intent.putExtra(BitchPagerActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
		// Toast.makeText(BitchsActivity.this,
		// persons.get(position).getCreatedAt(), Toast.LENGTH_SHORT)
		// .show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 0, "日期");
		menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;// 返回false就不会显示菜单
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			datePickerDialog.show(getFragmentManager(), "pick");
			break;

		default:
			break;
		}
		
		return true;
	}

}
