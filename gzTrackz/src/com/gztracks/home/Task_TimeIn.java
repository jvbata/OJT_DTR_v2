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

public class Task_TimeIn extends AsyncTask<String, Void, Boolean> {
	String email, password;
	
	Fragment_Home fragHome;
	
	Context context;
	
	ProgressDialog progressD;
	String date, time;
	double longitude = 0, latitude = 0;

	public Task_TimeIn(Fragment_Home fragHome, String email, double longitude,
			double latitude) {

		this.fragHome = fragHome;
		this.context = this.fragHome.getActivity();
		this.email = email;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	@Override
	protected void onPreExecute() {

		progressD = new ProgressDialog(context);

		progressD.setMessage("TimeIn in progress...");

		progressD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressD.show();

		progressD.setCanceledOnTouchOutside(false);

	}

	@Override
	protected Boolean doInBackground(String... params) {
		boolean flag = true;

		try {

			String urlTopTracks = "http://gz123.site90.net/timein/?email="
					+ email + "&longitude=" + Double.toString(longitude)
					+ "&latitude=" + Double.toString(latitude);

			HttpClient client = new DefaultHttpClient();
			ResponseHandler<String> handler = new BasicResponseHandler();

			HttpPost httpPost = new HttpPost(urlTopTracks);

			String httpResponseTopTracks = client.execute(httpPost, handler);

			StringTokenizer token = new StringTokenizer(httpResponseTopTracks,
					"<");
			String retrieveResult = token.nextToken();
			
			Log.d("Time in", "Response: " + retrieveResult);

			JSONObject result = new JSONObject(retrieveResult);

			date = result.getString("date");
			time = result.getString("time");
			flag = true;
			
			urlTopTracks = "http://gz123.site90.net/standups_status/?email="
					+ email;

			httpPost = new HttpPost(urlTopTracks);

			httpResponseTopTracks = client.execute(httpPost, handler);

			token = new StringTokenizer(httpResponseTopTracks,
					"<");
			retrieveResult = token.nextToken();
			
			Log.d("check standups", "Response: " + retrieveResult);

		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}

		return flag;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (progressD.isShowing()) {
			progressD.dismiss();
		}
	}
}
