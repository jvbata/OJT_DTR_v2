package com.gztracks.home;

import java.util.StringTokenizer;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

//fetch date and time
public class Task_CheckTimeAndStatus extends AsyncTask<String, Void, Void> {
	
	String email;
	Fragment_Home fragHome;
	ProgressDialog progressD;

	public Task_CheckTimeAndStatus(Fragment_Home fragHome, String email) {
	
		this.fragHome = fragHome;
		this.email = email;
	}

	@Override
	protected Void doInBackground(String... params) {
		
		try {
			
			String url = "http://gz123.site90.net/loginstatus/?email="
					+ email;

			HttpClient client = new DefaultHttpClient();
			ResponseHandler<String> handler = new BasicResponseHandler();

			HttpPost request = new HttpPost(url);

			String responseString = client.execute(request, handler);

			StringTokenizer token = new StringTokenizer(responseString,
					"<");
			
			String result = token.nextToken();

			JSONObject resultJson = new JSONObject(result);
			
			String status = resultJson.getString("active");
			
			fragHome.updateButton(status);
			
			String date = resultJson.getString("date");
			String time = resultJson.getString("time");
			
			fragHome.startTheClock(date, time);
			
			Log.d("RESULT", result);
		

		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;

	}
}