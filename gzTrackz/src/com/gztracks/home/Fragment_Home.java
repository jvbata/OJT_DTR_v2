package com.gztracks.home;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gztrackz.R;
import com.gztracks.gztracks.TabsManager;

public class Fragment_Home extends Fragment {

	private String PREFERENCE_NAME = "com.example.gztrackz",
			FNAME = "com.example.gztrackz.firstname",
			EMAIL = "com.example.gztrackz.email";

	TextView tv_time, tv_date, tv_name;
	TabsManager tabManager;
	Thread timerThread;

	ImageView btn_timein_timeout;

	String email;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		tabManager = (TabsManager) activity;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View fragmentHome = inflater.inflate(R.layout.fragment_home, container,
				false);

		init(fragmentHome);

		new Task_CheckTimeAndStatus(this, email).execute();

		return fragmentHome;
	}

	private void init(View fragmentHome) {

		SharedPreferences prefs = getActivity().getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);

		email = prefs.getString(EMAIL, null);

		tv_time = (TextView) fragmentHome
				.findViewById(R.id.tv_fragment_home_time);
		tv_date = (TextView) fragmentHome
				.findViewById(R.id.tv_fragment_home_date);
		tv_name = (TextView) fragmentHome
				.findViewById(R.id.tv_fragment_home_username);
		
		btn_timein_timeout = (ImageView) fragmentHome
				.findViewById(R.id.iv_fragment_home_timeintimeout);
		
		btn_timein_timeout.setBackgroundResource(R.drawable.inactivetimein);
		btn_timein_timeout.setTag(R.drawable.activetimeout);

		btn_timein_timeout
				.setOnClickListener(new ButtonTimeInTimeOutListener());

		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
				"Walkway_Bold.ttf");
		Typeface tf2 = Typeface.createFromAsset(getActivity().getAssets(),
				"CODE Bold.otf");
		Typeface tf3 = Typeface.createFromAsset(getActivity().getAssets(),
				"Nexa Light.otf");

		tv_time.setTypeface(tf);
		tv_date.setTypeface(tf2);
		tv_name.setTypeface(tf3);

		tv_name.setText("Hello, " + prefs.getString(FNAME, null) + "!");
	}

	// update button if user close app and has not timeout.
	public void updateButton(final String status) {

		tabManager.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				boolean hasNotTimeOut = status.equals("true");
				

				if (hasNotTimeOut) {

					btn_timein_timeout
							.setBackgroundResource(R.drawable.activetimeout);
					
					
					btn_timein_timeout.setTag(R.drawable.activetimein);
				}

			}
		});

	}

	private class ButtonTimeInTimeOutListener implements View.OnClickListener {

		@Override
		public void onClick(View clickedView) {

			int tag = (Integer) clickedView.getTag();
			
			if (tag == R.drawable.activetimein) {

				new Task_TimeOut(Fragment_Home.this, email).execute();

				btn_timein_timeout
						.setBackgroundResource(R.drawable.inactivetimein);
				
				btn_timein_timeout.setTag(R.drawable.activetimeout);

			} else {
				
				new Task_TimeIn(Fragment_Home.this, email, 121.774017,
						12.879721).execute();

				// progress bar then time out
				btn_timein_timeout
						.setBackgroundResource(R.drawable.inactivetimeout);
				
				btn_timein_timeout.setTag(R.drawable.activetimein);

			}

		}
	}

	// start the timer
	public void startTheClock(String resultDate, String resultTime) {

		try {

			// parse the resultDate and ResultTime to a date object
			Date fetchedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
					Locale.ENGLISH).parse(resultDate + "T" + resultTime);

			Calendar currentDateAndTimeCal = new GregorianCalendar();
			currentDateAndTimeCal.setTime(fetchedDate);

			timerThread = new TimerThread(currentDateAndTimeCal);
			timerThread.start();

		} catch (ParseException e1) {
			e1.printStackTrace();
		}

	}

	// thread for updating the timer
	private class TimerThread extends Thread implements Runnable {

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",
				Locale.US);
		Calendar calendar;

		public TimerThread(Calendar calendar) {
			this.calendar = calendar;
		}

		@Override
		public void run() {

			while (!Thread.currentThread().isInterrupted()) {

				try {

					updateClock(calendar, dateFormat);
					Thread.sleep(1000);

					calendar.set(Calendar.SECOND,
							calendar.get(Calendar.SECOND) + 1);

				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
				}
			}

		}
	}

	private void updateClock(final Calendar calendar,
			final SimpleDateFormat dateFormatter) {

		tabManager.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				dateFormatter.applyPattern("E, MM-dd-yyyy");
				tv_time.setText(dateFormatter.format(calendar.getTime()));
				dateFormatter.applyPattern("hh:mm:ss a");
				tv_date.setText(dateFormatter.format(calendar.getTime()));

			}
		});
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// stop the timer
		timerThread.interrupt();
	}

}
