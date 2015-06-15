package com.example.facefortest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;



public class LoginActivity extends Activity {

	EditText et_username, et_password;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		et_password=(EditText)findViewById(R.id.login_password_edit);
		Button button=(Button)findViewById(R.id.login_btnlogin);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (et_password.getText().toString().equals("123")) {
					((FaceApplication)getApplication()).setAdmin(false);
					startActivity(new Intent(LoginActivity.this,MainActivity.class).putExtra("admin", false));
				}else if (et_password.getText().toString().equals("147")) {
					startActivity(new Intent(LoginActivity.this,MainActivity.class).putExtra("admin", true));
					((FaceApplication)getApplication()).setAdmin(true);
				} 
			}
		});
	}

	
	
}
