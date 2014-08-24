package com.gztracks.record_standups;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gztrackz.R;
import com.gztracks.gztracks.TabsManager;

public class Fragment_RecordStandups extends Fragment {

	EditText et_previousTask, et_todoTask, et_taskProblem;
	Button btn_recordStandup;
	TabsManager tabManager;

	// need to receive an email pass from activity.

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		tabManager = (TabsManager) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view_recordStandups = inflater.inflate(
				R.layout.fragment_record_standups, container, false);

		init(view_recordStandups);

		return view_recordStandups;

	}

	private void init(View view_recordStandups) {

		et_previousTask = (EditText) view_recordStandups
				.findViewById(R.id.et_standupPrevious);
		et_todoTask = (EditText) view_recordStandups
				.findViewById(R.id.et_standupTodo);
		et_taskProblem = (EditText) view_recordStandups
				.findViewById(R.id.et_standupProblem);

		btn_recordStandup = (Button) view_recordStandups
				.findViewById(R.id.btn_record_standup);

		btn_recordStandup.setOnClickListener(new ButtonRecordStandupListener());

	}

	private class ButtonRecordStandupListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			saveStandups();
		}

	}

	private void saveStandups() {

		String email = "warren@gz.com";

		String previousTask = et_previousTask.getText().toString();
		String todoTask = et_todoTask.getText().toString();
		String taskProblem = et_taskProblem.getText().toString();

		new Task_SaveStandups(this, email, previousTask, todoTask, taskProblem)
				.execute();

	}

	public void savingStandUpsFailed() {

		tabManager.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				Toast.makeText(tabManager,
						"Failed to save standups. try again.",
						Toast.LENGTH_LONG).show();

			}
		});

	}

}
