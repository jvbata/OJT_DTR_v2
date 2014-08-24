package com.gztracks.record_standups;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class Task_SaveStandups extends AsyncTask<Void, Void, Void> {

	private final static String SAVE_STANDUPS_URL = "http://gz123.site90.net/standups/?email=";

	private Fragment_RecordStandups fragRecStandups;
	private ProgressDialog progressD;
	private String email;
	private String previousTask, todoTask, taskProblem;

	public Task_SaveStandups(Fragment_RecordStandups fragRecStandups, String email,
			String previousTask, String todoTask, String taskProblem) {

		this.fragRecStandups = fragRecStandups;
		this.email = email;
		this.previousTask = previousTask;
		this.todoTask = todoTask;
		this.taskProblem = taskProblem;
	}

	@Override
	protected void onPreExecute() {

		progressD = new ProgressDialog(fragRecStandups.getActivity());
		progressD.setMessage("Saving standup...");
		progressD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressD.setCanceledOnTouchOutside(false);
		progressD.show();
	}

	@Override
	protected Void doInBackground(Void... params) {

		try {

			previousTask = previousTask.replace(" ", "%20");
			todoTask = todoTask.replace(" ", "%20");
			taskProblem = taskProblem.replace(" ", "%20");

			String saveStandupsUrl = SAVE_STANDUPS_URL + email + "&standup_y="
					+ previousTask + "&standup_todo=" + todoTask + "&problem="
					+ taskProblem;

			HttpClient client = new DefaultHttpClient();
			ResponseHandler<String> handler = new BasicResponseHandler();

			HttpPost request = new HttpPost(saveStandupsUrl);

			client.execute(request, handler);

		} catch (Exception e) {
			
			fragRecStandups.savingStandUpsFailed();
			
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
