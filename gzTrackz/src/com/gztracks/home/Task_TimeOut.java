package com.gztracks.home;

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
import android.util.Log;

public class Task_TimeOut extends AsyncTask<String, Void, Void> {
	String email, password;
	Fragment_Home fragHome;
	Context context;
	ProgressDialog progressD;
	String date, time;

	public Task_TimeOut(Fragment_Home fragHome, String email) {

		this.fragHome = fragHome;
		this.context = this.fragHome.getActivity();
		this.email = email;

	}

	@Override
	protected void onPreExecute() {
		progressD = new ProgressDialog(context);

		progressD.setMessage("TimeOut in progress...");

		progressD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressD.show();
		progressD.setCanceledOnTouchOutside(false);

	}

	@Override
	protected Void doInBackground(String... params) {

		try {

			String urlTopTracks = "http://gz123.site90.net/timeout/?email="
					+ email;

			HttpClient client = new DefaultHttpClient();
			ResponseHandler<String> handler = new BasicResponseHandler();

			HttpPost request = new HttpPost(urlTopTracks);

			String httpResponseTopTracks = client.execute(request, handler);

			StringTokenizer token = new StringTokenizer(httpResponseTopTracks,
					"<");
			String retrieveResult = token.nextToken();

			JSONObject result = new JSONObject(retrieveResult);

			Log.d("Time out", "Response: " + retrieveResult);

			date = result.getString("date");
			time = result.getString("time");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (progressD.isShowing()) {
			progressD.dismiss();
		}
	}
}
