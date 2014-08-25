package com.gztracks.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gztrackz.R;
import com.gztracks.login.Activity_Login;

public class Activity_Register extends Activity {

	private EditText emailTxt, firstNameTxt, lastNameTxt, passwordTxt,
			confirmPasswordTxt;
	private Context context;
	private String email, firstName, lastName, password, confirmPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		init();

	}

	public void buttonClicked(View viewClicked) {

		registerButtonClicked();
	}

	private void init() {

		emailTxt = (EditText) findViewById(R.id.et_registerEmail);
		passwordTxt = (EditText) findViewById(R.id.et_registerPassword);
		firstNameTxt = (EditText) findViewById(R.id.et_firstname);
		lastNameTxt = (EditText) findViewById(R.id.et_lastname);
		confirmPasswordTxt = (EditText) findViewById(R.id.et_confirmPassword);
		context = this;
	}

	private void registerButtonClicked() {

		email = emailTxt.getText().toString();
		firstName = firstNameTxt.getText().toString();
		lastName = lastNameTxt.getText().toString();
		password = passwordTxt.getText().toString();
		confirmPassword = confirmPasswordTxt.getText().toString();

		if (email.length() > 0 && firstName.length() > 0
				&& lastName.length() > 0 && password.length() > 0
				&& confirmPassword.length() > 0) {

			if (password.equals(confirmPassword)) {

				new Task_Register(context, email, firstName, lastName, password)
						.execute();

			} else {

				Toast.makeText(context, "Passwords don't match!",
						Toast.LENGTH_LONG).show();

			}
		} else {
			Toast.makeText(context, "Please fill all fields!",
					Toast.LENGTH_LONG).show();
		}
	}

	// called on post execute of task_register.
	public void performRegisterBasedOnResult(String resultFlag) {

		Toast.makeText(context, resultFlag, Toast.LENGTH_LONG).show();

		if (resultFlag.equalsIgnoreCase("registration successful")) {
			finish();
		}

	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		Intent registerIntent = new Intent(context, Activity_Login.class);
		registerIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		
		Bundle bndlanimation = 
				ActivityOptionsCompat.makeCustomAnimation(context, R.anim.slide_in_from_left, R.anim.slide_out_to_left).toBundle();
		
		ActivityCompat.startActivity(Activity_Register.this, registerIntent, bndlanimation);
	}
	
}
