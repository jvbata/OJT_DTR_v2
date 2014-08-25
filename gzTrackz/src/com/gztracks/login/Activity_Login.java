package com.gztracks.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gztrackz.R;
import com.gztracks.gztracks.TabsManager;
import com.gztracks.register.Activity_Register;
import com.gztracks.utilities.FontAndToastUtils;
import com.gztracks.utilities.SharedPrefUtil;

public class Activity_Login extends Activity {

	private TextView tv_register;

	private EditText et_email, et_password;
	private FontAndToastUtils utils;

	private Context context;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// initialize variables
		init();
		
		checkLoginSession();
	}

	private void init() {

		context = this;
		utils = new FontAndToastUtils(Activity_Login.this);
		tv_register = (TextView) findViewById(R.id.tv_registerBtn);
		et_email = (EditText) findViewById(R.id.et_usernameEmail);
		et_password = (EditText) findViewById(R.id.et_password);

		tv_register.setTypeface(utils.getTtfFont("Walkway_SemiBold.ttf"));
	}
	
	private void checkLoginSession() {
		
		prefs = this.getSharedPreferences(SharedPrefUtil.PREFERENCE_NAME,
				Context.MODE_PRIVATE);

		String firstName = prefs.getString(SharedPrefUtil.KEY_FNAME, null);
		
		//if not null no not to login
		if (firstName != null) {
			Intent i = new Intent(this, TabsManager.class);
			i.putExtra("email", prefs.getString(SharedPrefUtil.KEY_EMAIL, null));
			startActivityForResult(i, 1);
		}
		
	}

	public void buttonClicked(View viewClicked) {

		switch (viewClicked.getId()) {

		case R.id.btn_login:
			userClickedLoginBtn();
			break;

		case R.id.tv_registerBtn:
			userClickedRegisterBtn();
			break;

		}
	}

	private void userClickedLoginBtn() {

		String emailInput = et_email.getText().toString();
		String passInput = et_password.getText().toString();

		if (emailInput.length() > 0 && passInput.length() > 0) {

			if (isConnectingToInternet()) {

				new Task_Login(context, emailInput, passInput, prefs).execute();
			} else {

				utils.promptUser("Please make sure internet connection exists!");
			}

		} else {

			utils.promptUser("Please fill all fields!");
		}
	}

	private void userClickedRegisterBtn() {

		Intent registerIntent = new Intent(context, Activity_Register.class)
				.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

		Bundle bndlanimation = ActivityOptionsCompat.makeCustomAnimation(
				context, R.anim.slide_out_to_right, R.anim.slide_in_from_right)
				.toBundle();

		ActivityCompat.startActivity(Activity_Login.this, registerIntent,
				bndlanimation);

	}

	// called on post execute of task_login
	public void performLoginBasedOnResult(Boolean login) {

		// login successful
		if (login) {
			
			Intent i = new Intent(context, TabsManager.class);
			i.putExtra("email", prefs.getString(SharedPrefUtil.KEY_EMAIL, null));

			startActivityForResult(i, 1);

		} else
			Toast.makeText(context, "Invalid login credentials!",
					Toast.LENGTH_LONG).show();

	}

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED)
			finish();
		else {
			et_email.setText(null);
			et_password.setText(null);
			et_email.requestFocus();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		ActivityCompat.finishAffinity(this);
	}

	/*
	 * private boolean isNetworkAvailable() { ConnectivityManager
	 * connectivityManager = (ConnectivityManager)
	 * getSystemService(Context.CONNECTIVITY_SERVICE); NetworkInfo
	 * activeNetworkInfo = connectivityManager .getActiveNetworkInfo(); return
	 * activeNetworkInfo != null && activeNetworkInfo.isConnected(); }
	 */
}
