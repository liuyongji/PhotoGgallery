package com.example.facefortest.stars;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;

import com.example.facefortest.AddStarsActivity;
import com.example.facefortest.FaceApplication;
import com.example.facefortest.R;
import com.face.test.bean.RemoveInfo;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.google.gson.Gson;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class StarsActivity extends Activity implements
		SwipeRefreshLayout.OnRefreshListener {
	private int total = 0;
	private GridView mGridView;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private StarsAdapter imageAdapter;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//	private Date date;
	private HttpRequests request = null;// 在线api
	private Handler handler;
	private int position;

	private List<Stars> persons = new ArrayList<Stars>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		request = new HttpRequests("99a9423512d4f19c17bd8d6b526e554c",
				"z8stpP3-HMdYhg6kAK73A2nBFwZg4Thl");
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
				StarsActivity.this.position=position;
				new Thread(new MYRUN(position)).start();

				

				return true;
			}
		});
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					final Stars person = new Stars();
					BmobFile file = new BmobFile();
					file.setUrl(persons.get(position).getBmobFile().getUrl());
					file.delete(StarsActivity.this, new DeleteListener() {

						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							person.setObjectId(persons.get(position).getObjectId());
							person.delete(StarsActivity.this, new DeleteListener() {

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
							person.setObjectId(persons.get(position).getObjectId());
							person.delete(StarsActivity.this, new DeleteListener() {

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
					});
					break;
				case 1:
					Toast.makeText(StarsActivity.this, (String) msg.obj,
							Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
				}
			};
		};
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		new GetDataTask().execute(total);
	}

	public class MYRUN implements Runnable {
		private int s;

		public MYRUN(int s) {
			this.s = s;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			JSONObject jsonObject = null;
			try {
				jsonObject = request.facesetRemoveFace(new PostParameters()
						.setFacesetName("Stars1").setFaceId(
								persons.get(s).getFaceId()));

				Log.i("lyj", jsonObject.toString());
				Gson gson = new Gson();
				RemoveInfo removeInfo = gson.fromJson(jsonObject.toString(),
						RemoveInfo.class);
				Log.i("lyj", removeInfo.getSuccess());
				Message message = new Message();
				if (removeInfo.getSuccess().equals("true")) {
					message.what = 0;
					message.obj = persons.get(s).getObjectId();
				} else {
					message.what = 1;
					message.obj = removeInfo.getRemoved();
				}
				handler.sendMessage(message);

			} catch (Exception e) {
				Message message = new Message();
				message.what = 1;
				message.obj = e.getMessage();
				handler.sendMessage(message);
			}

		}
	}

	private class GetDataTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			BmobQuery<Stars> bmobQuery = new BmobQuery<Stars>();

			bmobQuery.setSkip(total);
			bmobQuery.setLimit(200);
			bmobQuery.order("-name");
			bmobQuery.findObjects(StarsActivity.this,
					new FindListener<Stars>() {

						@Override
						public void onSuccess(List<Stars> persons) {
							if (persons == null || persons.size() == 0) {
								toast("已经到头了");
								mSwipeRefreshLayout.setRefreshing(false);
								return;
							}

							StarsActivity.this.persons.addAll(0, persons);
							total += persons.size();
							imageAdapter = new StarsAdapter(StarsActivity.this,
									StarsActivity.this.persons);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 0, "新增");
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 1:
			Intent intent=new Intent(StarsActivity.this,AddStarsActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void toast(String string) {
		Toast.makeText(StarsActivity.this, string, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onRefresh() {
		new GetDataTask().execute();
	}

	private void imageBrower(int position, List<Stars> persons) {
		Intent intent = new Intent(StarsActivity.this, StarsPagerActivity.class);
		intent.putExtra(StarsPagerActivity.EXTRA_IMAGE_URLS,
				(Serializable) persons);
		intent.putExtra(StarsPagerActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
	}


}
