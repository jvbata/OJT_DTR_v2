package com.example.login;

import java.util.StringTokenizer;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class Task_Register extends AsyncTask<Void, Void, String> {

	private static final String REGISTER_URL = "http://gz123.site90.net/register/?email=";
	private String email, password, firstName, lastName;
	private Context context;
	private ProgressDialog progressD;

	public Task_Register(Context context, String email, String firstName,
			String lastName, String password) {

		this.context = context;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Override
	protected void onPreExecute() {

		progressD = new ProgressDialog(context);
		progressD.setMessage("Registering...");
		progressD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressD.setCanceledOnTouchOutside(false);
		progressD.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		String resultFlag = null;

		try {

			firstName = firstName.replace(" ", "%20");
			lastName = lastName.replace(" ", "%20");

			String accountDataToRegister = REGISTER_URL + email + "&password="
					+ password + "&first_name=" + firstName + "&last_name="
					+ lastName;

			HttpClient client = new DefaultHttpClient();
			ResponseHandler<String> handler = new BasicResponseHandler();

			HttpPost request = new HttpPost(accountDataToRegister);

			String httpResponse = client.execute(request, handler);

			StringTokenizer token = new StringTokenizer(httpResponse, "<");
			String retrieveResult = token.nextToken();

			JSONObject result = new JSONObject(retrieveResult);

			resultFlag = result.getString("result");

		} catch (Exception e) {

			((Activity_Register) context)
					.performRegisterBasedOnResult("Unexpected error occured.");

			e.printStackTrace();
		}

		return resultFlag;
	}

	@Override
	protected void onPostExecute(String resultFlag) {

		if (progressD.isShowing()) {
			progressD.dismiss();
		}

		((Activity_Register) context).performRegisterBasedOnResult(resultFlag);
	}

}
