package com.example.facefortest;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

public class MainActivity extends Activity {
	private List<String> list=new ArrayList<String>();
	private int total=0;
	ImageAdapter imageAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Bmob.initialize(this, "6bb1226b16bb29f5b8e3b71621af32fc");
		
		new GetDataTask().execute(total);

	}
	private class GetDataTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			BmobQuery<Person> bmobQuery=new BmobQuery<Person>();
			bmobQuery.setSkip(total);
			bmobQuery.order("-createdAt");
			bmobQuery.findObjects(MainActivity.this, new FindListener<Person>() {
				
				@Override
				public void onSuccess(List<Person> persons) {
					// TODO 自动生成的方法存根
					total+=persons.size();
					for (int i = 0; i < persons.size(); i++) {
						if (persons.get(i).getFile()!=null) {
							list.add(persons.get(i).getFile().getFileUrl());
						}
					}
					imageAdapter=new ImageAdapter(MainActivity.this, list);
				}
				
				@Override
				public void onError(int arg0, String arg1) {
					// TODO 自动生成的方法存根
					toast(arg1);
				}
			});
			return null;
		}
	}
	
	 public void toast(String string){
		 Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
	 }

}
