package com.example.facefortest;

import java.io.File;
import java.util.List;

import org.json.JSONObject;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;

import com.face.test.bean.FaceInfos;
import com.face.test.bean.Stars;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddStarsActivity extends Activity implements OnClickListener {
	private TextView tv_result, tv_url;
	private EditText ed_url, ed_name;
	private Button btn_single, btn_double, btn_img;
	private HttpRequests request = null;// 在线api
	private Handler handler;
	private FaceInfos faceInfos;
	private Bitmap mBitmap;
	private BmobFile bmobFile;
	private List<Stars> starssss;
	private int i = 0;
	private ProgressDialog progressDialog;

	// private byte[] data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addstar);
		request = new HttpRequests("99a9423512d4f19c17bd8d6b526e554c",
				"z8stpP3-HMdYhg6kAK73A2nBFwZg4Thl");
		initView();
		progressDialog=new ProgressDialog(this);
		handler = new Handler() {
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					File file = BitmapUtil.saveBitmap(mBitmap);
					bmobFile = new BmobFile(file);
					bmobFile.upload(AddStarsActivity.this,
							new UploadFileListener() {

								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									progressDialog.dismiss();
									Stars stars = new Stars();
									stars.setFaceId(faceInfos.getFace().get(0)
											.getFace_id());
									stars.setName(ed_name.getText().toString());
									stars.setBmobFile(bmobFile);
									stars.save(AddStarsActivity.this);
									tv_result.setText(ed_name.getText()
											+ faceInfos.getFace().get(0)
													.getFace_id() + "success");

								}

								@Override
								public void onFailure(int arg0, String arg1) {
									// TODO Auto-generated method stub
									progressDialog.dismiss();
									tv_result.setText(ed_name.getText()
											+ "fail");
								}
							});

					break;
				case 2:
					tv_result.setText(tv_result.getText()+starssss.get(i).getName()+ "fail\n");
					i++;
					if (i >= starssss.size()) {
						tv_result.setText(tv_result.getText()
								+ "end");
						return;
					}
					new AddThread(starssss.get(i).getUrl(), 1).start();
					break;
				case 3:
					File file2 = BitmapUtil.saveBitmap(mBitmap);
					bmobFile = new BmobFile(file2);
					bmobFile.upload(AddStarsActivity.this,
							new UploadFileListener() {

								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									Stars stars = new Stars();
									stars.setFaceId(faceInfos.getFace().get(0)
											.getFace_id());
									stars.setName(starssss.get(i).getName());
									stars.setBmobFile(bmobFile);
									stars.save(AddStarsActivity.this);
									tv_result.setText(tv_result.getText()
											+ starssss.get(i).getName()
											+ starssss.get(i).getUrl()
											+ faceInfos.getFace().get(0)
													.getFace_id() + "\n");
									i++;
									if (i >= starssss.size()) {
										tv_result.setText(tv_result.getText()
												+ "end");
										return;
									}

									new AddThread(starssss.get(i).getUrl(), 1)
											.start();
								}

								@Override
								public void onFailure(int arg0, String arg1) {
									// TODO Auto-generated method stub
									tv_result.setText(tv_result.getText()
											+ starssss.get(i).getName() + arg1
											+ "\n");
									i++;
									if (i >= starssss.size()) {
										tv_result.setText(tv_result.getText()
												+ "end");
										return;
									}
									new AddThread(starssss.get(i).getUrl(), 1)
											.start();
								}
							});

					break;
				default:
					break;
				}
			};
		};
	}

	private void initView() {
		tv_url = (TextView) findViewById(R.id.tv_url);
		tv_result = (TextView) findViewById(R.id.tv_result);
		ed_url = (EditText) findViewById(R.id.et_single_url);
		ed_name = (EditText) findViewById(R.id.et_single_name);
		btn_single = (Button) findViewById(R.id.btn_single_add);
		btn_double = (Button) findViewById(R.id.btn_double_add);
		btn_img = (Button) findViewById(R.id.btn_singleimg);
		btn_img.setOnClickListener(this);
		btn_single.setOnClickListener(this);
		btn_double.setOnClickListener(this);
		tv_url.setOnClickListener(this);
	}

	private class AddThread extends Thread {
		private String url;
		private int type;

		public AddThread(String url, int type) {
			this.url = url;
			this.type = type;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				byte[] data;
				if (type == 2) {
					mBitmap = BitmapUtil.getScaledBitmap(url, 700);

					data = BitmapUtil.getBitmapByte(AddStarsActivity.this,
							mBitmap);

				} else {
					data = Utils.getImage(url);
					mBitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length);// bitmap
				}

				// if (data != null) {

				JSONObject jsonObject = null;
				try {
					if (type == 2) {

						jsonObject = request
								.detectionDetect(new PostParameters()
										.setImg(data));
					} else {

						jsonObject = request
								.detectionDetect(new PostParameters()
										.setUrl(url));
					}
					Log.i("lyj", jsonObject.toString());
					Gson gson = new Gson();
					faceInfos = gson.fromJson(jsonObject.toString(),
							FaceInfos.class);
					if (faceInfos.getFace().size() <= 0) {
						Message message = new Message();
						message.what = 2;
						handler.sendMessage(message);

					} else {
						String face1 = faceInfos.getFace().get(0).getFace_id();

						jsonObject = request
								.facesetAddFace(new PostParameters().setFaceId(
										face1).setFacesetName("Stars1"));

						Message message = new Message();
						message.obj = jsonObject.toString();
						switch (type) {
						case 0:
							message.what = 0;
							break;
						case 1:
							message.what = 3;

							break;
						case 2:
							message.what = 0;
							break;

						default:
							break;
						}

						handler.sendMessage(message);

					}

				} catch (FaceppParseException e) {
					e.printStackTrace();
					Message message = new Message();
					message.what = 2;
					handler.sendMessage(message);
				}

				// } else {
				// Message message = new Message();
				// message.what = 2;
				// handler.sendMessage(message);
				// }
			} catch (Exception e) {
				e.printStackTrace();
				Message message = new Message();
				message.what = 2;
				handler.sendMessage(message);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_single_add:
			if (ed_url.getText().equals("") || ed_url.getText() == null) {
				tv_result.setText("url不能为空");
				return;
			}
			progressDialog.show();
			new AddThread(ed_url.getText().toString(), 0).start();

			break;
		case R.id.btn_double_add:
			initdata();
			break;
		case R.id.btn_singleimg:
			progressDialog.show();
			new AddThread(tv_url.getText().toString(), 2).start();
			break;
		case R.id.tv_url:
			startActivityForResult(new Intent("android.intent.action.PICK",
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1001);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1001:
			if (data != null) {
				Uri localUri = data.getData();
				String[] arrayOfString = new String[1];
				arrayOfString[0] = "_data";
				Cursor localCursor = this.getContentResolver().query(localUri,
						arrayOfString, null, null, null);
				if (localCursor == null)
					return;
				localCursor.moveToFirst();
				String str = localCursor.getString(localCursor
						.getColumnIndex(arrayOfString[0]));
				localCursor.close();
				tv_url.setText(str);

			}
			break;
		default:
			break;
		}
	}

	private void initdata() {
		String result = Util.getFile(this, "stars.txt");
		Gson gson = new Gson();
		starssss = gson.fromJson(result, new TypeToken<List<Stars>>() {
		}.getType());

		new AddThread(starssss.get(i).getUrl(), 1).start();
	}

}
