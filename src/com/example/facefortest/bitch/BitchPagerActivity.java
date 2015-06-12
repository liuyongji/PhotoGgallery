package com.example.facefortest.bitch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.example.facefortest.BitmapUtil;
import com.example.facefortest.R;
import com.face.test.bean.FaceInfos;
import com.face.test.bean.Stars;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.google.gson.Gson;
import com.loveplusplus.demo.image.HackyViewPager;
import com.loveplusplus.demo.image.ImageDetailFragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BitchPagerActivity extends FragmentActivity {
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";

	private HttpRequests request = null;// 在线api
	private HackyViewPager mPager;
	private int pagerPosition;
	private TextView indicator;
	private List<Bitchs> persons;
	private Handler handler;
	private Bitmap mBitmap;
	private BmobFile bmobFile;
	private FaceInfos faceInfos;
	private EditText namEditText;
	private ProgressDialog progressDialog;

	// private String ids;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager);
		request = new HttpRequests("99a9423512d4f19c17bd8d6b526e554c",
				"z8stpP3-HMdYhg6kAK73A2nBFwZg4Thl");

		pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		persons = (List<Bitchs>) getIntent().getSerializableExtra(
				EXTRA_IMAGE_URLS);
		namEditText = (EditText) findViewById(R.id.ed_name);
		mPager = (HackyViewPager) findViewById(R.id.pager);
		ImagePagerAdapter mAdapter = new ImagePagerAdapter(
				getSupportFragmentManager(), persons);
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

		handler = new Handler() {
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					File file = BitmapUtil.saveBitmap(mBitmap);
					bmobFile = new BmobFile(file);
					bmobFile.upload(BitchPagerActivity.this,
							new UploadFileListener() {

								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									Stars stars = new Stars();
									stars.setFaceId(faceInfos.getFace().get(0)
											.getFace_id());
									stars.setName(namEditText.getText()
											.toString());
									stars.setBmobFile(bmobFile);
									stars.save(BitchPagerActivity.this,
											new SaveListener() {

												@Override
												public void onSuccess() {
													if (progressDialog.isShowing()) {
														progressDialog.dismiss();
													}
													 Toast.makeText(BitchPagerActivity.this,
															 namEditText.getText()+"添加成功", 1).show();
//													namEditText
//															.setText(namEditText
//																	.getText()
//																	+ "添加成功");
													Bitchs bitchs = new Bitchs();
													bitchs.setObjectId(persons
															.get(pagerPosition)
															.getObjectId());
													bitchs.delete(BitchPagerActivity.this);
												}

												@Override
												public void onFailure(int arg0,
														String arg1) {
													// TODO Auto-generated
													// method stub
													if (progressDialog.isShowing()) {
														progressDialog.dismiss();
													}
												}
											});

								}

								@Override
								public void onFailure(int arg0, String arg1) {
									if (progressDialog.isShowing()) {
										progressDialog.dismiss();
									}
								}
							});

					break;
				case 2:
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
//					namEditText.setText(namEditText.getText() + "error");
					 Toast.makeText(BitchPagerActivity.this,
					 namEditText.getText()+"error", 1).show();
					break;
				default:
					break;
				}
			};
		};
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		// public List<String> fileList;

		public List<Bitchs> fileList;

		public ImagePagerAdapter(FragmentManager fm, List<Bitchs> fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.size();
		}

		@Override
		public Fragment getItem(int position) {
			String url = fileList.get(position).getFile()
					.getFileUrl(BitchPagerActivity.this);
			return ImageDetailFragment.newInstance(url, fileList.get(position)
					.getCreatedAt());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.main2, menu);
		menu.add(0, 1, 0, "star");
		menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;// 返回false就不会显示菜单
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.bitchs_date:
			Bitchs person = new Bitchs();
			person.setObjectId(persons.get(pagerPosition).getObjectId());
			person.setLike(true);
			person.update(BitchPagerActivity.this, new UpdateListener() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					Toast.makeText(BitchPagerActivity.this, "success",
							Toast.LENGTH_LONG).show();
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(BitchPagerActivity.this, arg0 + "  " + arg1,
							Toast.LENGTH_LONG).show();
				}
			});

			break;
		case 1:
			

			progressDialog = new ProgressDialog(BitchPagerActivity.this);
			progressDialog.show();

			new Thread(new Myrun(persons.get(pagerPosition))).start();

		default:
			break;
		}

		return true;
	}

	private class Myrun implements Runnable {
		private Bitchs bitchs;

		public Myrun(Bitchs bitchs) {
			this.bitchs = bitchs;
		}

		@Override
		public void run() {
			try {

				// 以下是取得图片的两种方法
				// ////////////// 方法1：取得的是byte数组, 从byte数组生成bitmap
				byte[] data = getImage(bitchs.getFile().getFileUrl(
						BitchPagerActivity.this));
				if (data != null) {
					mBitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length);// bitmap
					JSONObject jsonObject = null;
					try {
						jsonObject = request
								.detectionDetect(new PostParameters()
										.setImg(data));
						Log.i("lyj", jsonObject.toString());
						Gson gson = new Gson();
						faceInfos = gson.fromJson(jsonObject.toString(),
								FaceInfos.class);
						if (faceInfos.getFace().size() <= 0) {
							Message message = new Message();
							message.what = 2;
							handler.sendMessage(message);

						} else {
							String face1 = faceInfos.getFace().get(0)
									.getFace_id();

							jsonObject = request
									.facesetAddFace(new PostParameters()
											.setFaceId(face1).setFacesetName(
													"Stars1"));

							Message message = new Message();
							message.obj = jsonObject.toString();
							message.what = 0;
							handler.sendMessage(message);

						}

					} catch (FaceppParseException e) {
						e.printStackTrace();
						Message message = new Message();
						message.what = 2;
						handler.sendMessage(message);
					}

				} else {
					Message message = new Message();
					message.what = 2;
					handler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Message message = new Message();
				message.what = 2;
				handler.sendMessage(message);
			}

		}

	};

	public byte[] getImage(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		InputStream inStream = conn.getInputStream();
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return readStream(inStream);
		}
		return null;
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

}