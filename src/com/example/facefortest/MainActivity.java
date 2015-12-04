package com.example.facefortest;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.facefortest.bitch.BitchPagerActivity;
import com.example.facefortest.bitch.BitchsActivity;
import com.example.facefortest.stars.StarsActivity;
import com.face.test.bean.Person2;
import com.weedong.corps.framwork.view.pulltorefresh.PullToRefreshBase;
import com.weedong.corps.framwork.view.pulltorefresh.PullToRefreshBase.Mode;
import com.weedong.corps.framwork.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.weedong.corps.framwork.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.weedong.corps.framwork.view.pulltorefresh.PullToRefreshGridView;

import mirko.android.datetimepicker.date.DatePickerDialog;
import mirko.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
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
import android.widget.ScrollView;
import android.widget.Toast;

public class MainActivity extends Activity implements
		SwipeRefreshLayout.OnRefreshListener {

	private List<Person2> persons = new ArrayList<Person2>();
	private int total = 0;
	private ImageAdapter imageAdapter;
	private PullToRefreshGridView mGridView;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//	private SwipeRefreshLayout mSwipeRefreshLayout;
	private final Calendar mCalendar = Calendar.getInstance();
	private boolean mlimit = false;
	private Date date;
	private boolean mcollect = false;
	private boolean admin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_pull_to_refresh_activity);
		

//		admin = getIntent().getExtras().getBoolean("admin", false);
		admin=((FaceApplication)getApplication()).getAdmin();
		mGridView = (PullToRefreshGridView) findViewById(R.id.gv_content);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				imageBrower(position, persons);
			}
		});
		mGridView.setMode(Mode.BOTH);
		mGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<GridView> refreshView) {
				// TODO Auto-generated method stub
				mGridView.onRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<GridView> refreshView) {
				// TODO Auto-generated method stub
				
				new GetDataTask().execute(total);
			}});
		mGridView.getRefreshableView().setNumColumns(3);
		mGridView.getRefreshableView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if (admin) {
					final Person2 person = new Person2();
					BmobFile file = new BmobFile();
					file.setUrl(persons.get(position).getFile().getUrl());
					file.delete(MainActivity.this, new DeleteListener() {
						
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							person.setObjectId(persons.get(position).getObjectId());
							person.delete(MainActivity.this,
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
							
							if (arg0==153) {
								person.setObjectId(persons.get(position).getObjectId());
								person.delete(MainActivity.this,
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
							}else {
								toast("删除文件失败：" +arg0+ arg1);
							}
						}
					});
					
					
					
				}else {
					toast("你没有操作权限");
				}
				

				return true;
			}
		});
//		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
//		mSwipeRefreshLayout.setOnRefreshListener(this);
		new GetDataTask().execute(total);

	}

	final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
			new OnDateSetListener() {

				public void onDateSet(DatePickerDialog datePickerDialog,
						int year, int month, int day) {
					total = 0;
					mlimit = true;
					mcollect = false;
					persons.clear();
					// list.clear();
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
			Intent intent = new Intent(MainActivity.this, BitchsActivity.class);
			startActivity(intent);

			break;
		case R.id.collect:
			mcollect = true;
			mlimit = false;
			total = 0;
			persons.clear();
			new GetDataTask().execute(total);
			break;
		case R.id.star:
			Intent intent2 = new Intent(MainActivity.this, StarsActivity.class);
			startActivity(intent2);

			break;
		case R.id.person2:
			break;

		default:
			break;
		}

		return true;
	}

	private void imageBrower(int position, List<Person2> list) {
		Intent intent = new Intent(MainActivity.this, BitchPagerActivity.class);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,
				(Serializable) persons);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
	}

	private class GetDataTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			BmobQuery<Person2> bmobQuery = new BmobQuery<Person2>();
			bmobQuery.order("-createdAt");
			if (mlimit) {
				BmobQuery<Person2> query1 = new BmobQuery<Person2>();
				query1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(
						date));
				BmobQuery<Person2> query2 = new BmobQuery<Person2>();
				query2.addWhereLessThan("createdAt",
						new BmobDate(Utils.addDate(date)));

				List<BmobQuery<Person2>> query3 = new ArrayList<BmobQuery<Person2>>();
				query3.add(query1);
				query3.add(query2);
				bmobQuery.and(query3);
				bmobQuery.order("-updatedAt");
			}
			
			if (mcollect) {
				bmobQuery.addWhereEqualTo("like", true);
				bmobQuery.order("-updatedAt");
			}
			bmobQuery.setSkip(total);
			bmobQuery.findObjects(MainActivity.this,
					new FindListener<Person2>() {

						@Override
						public void onSuccess(List<Person2> persons) {
							if (persons == null || persons.size() == 0) {
								toast("已经到头了");
//								mSwipeRefreshLayout.setRefreshing(false);
								mGridView.onRefreshComplete();
								return;
							}

							MainActivity.this.persons.addAll(persons);

							total += persons.size();
							imageAdapter = new ImageAdapter(MainActivity.this,
									MainActivity.this.persons);
							toast(persons.get(persons.size() - 1)
									.getCreatedAt());
							mGridView.getRefreshableView().setAdapter(imageAdapter);
							mGridView.onRefreshComplete();
							mGridView.getRefreshableView().setSelection(total- persons.size()-1);
//							mSwipeRefreshLayout.setRefreshing(false);
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
		Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRefresh() {
		new GetDataTask().execute();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		return false;
	}

}
