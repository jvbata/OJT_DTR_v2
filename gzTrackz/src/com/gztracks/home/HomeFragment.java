package com.gztracks.home;

import gps_classes.GZ_Service_Locator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gztrackz.R;
import com.gztracks.gztracks.StandUpsDialog;

public class HomeFragment extends Fragment {

	private ImageView timeLogBTN;
	public MyInterface homeInterface;
	private View rootView;

	public interface MyInterface {
		public void buttonClicked(boolean timeIn);
	}

	private String PREFERENCE_NAME = "com.example.gztrackz",
			FNAME = "com.example.gztrackz.firstname",
			EMAIL = "com.example.gztrackz.email";

	private SharedPreferences prefs;

	String email, hourDisplay = "--", minutesDisplay = "--",
			dateDisplay = "--------, ------ --", amPmDisplay = "--";

	private boolean loggedIn, checked = false;

	private TextView nameTXT, timeTXT, dateTXT, amPmTXT;

	private int timeIMG;

	private GZ_Service_Locator gps;

	private Thread timeThread;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_home, container, false);

		init();

		timeThread = new Thread();

		if (!checked) {

			new AlreadyLogged(getActivity(), email).execute();
			gps = new GZ_Service_Locator(getActivity());
			checked = true;

		} else {

			timeLogBTN.setImageResource(timeIMG);

		}

		timeLogBTN.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isConnectingToInternet()) {
					new TimeUpdate(getActivity(), email).execute();
					if (timeIMG == R.drawable.inactivetimeout) {
						DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case DialogInterface.BUTTON_POSITIVE:
									new TimeLog(getActivity(), email, true)
											.execute();
									break;
								case DialogInterface.BUTTON_NEGATIVE:
									break;
								}
							}
						};
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						builder.setMessage("Are you sure?")
								.setPositiveButton("Yes", dialogClickListener)
								.setNegativeButton("No", dialogClickListener)
								.setCancelable(false).show();
					} else {
						if (gps.canGetLocation()) {
							double latitude = gps.getLatitude();
							double longitude = gps.getLongitude();
							if (latitude == 0 && longitude == 0) {
								gps.showSettingsAlert();
							} else {
								new TimeLog(getActivity(), email, false,
										longitude, latitude).execute();
								/*
								 * Toast.makeText( getActivity(),
								 * "Your Location is - \nLat: " + latitude +
								 * "\nLong: " + longitude,
								 * Toast.LENGTH_LONG).show();
								 */
							}

						} else {
							Toast.makeText(getActivity(), "Please enable GPS!",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast.makeText(
							getActivity(),
							"Unable to connect to the server!\nPlease make sure you are connected to the internet.",
							Toast.LENGTH_LONG).show();
				}

				Log.d("CHECK", Boolean.toString(loggedIn));
			}
		});

		return rootView;
	}

	private void init() {

		prefs = getActivity().getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		email = prefs.getString(EMAIL, null);

		TextView timeTxt = (TextView) rootView
				.findViewById(R.id.tv_fragment_home_time);
		TextView dateTxt = (TextView) rootView
				.findViewById(R.id.tv_fragment_home_date);
		TextView nameTxt = (TextView) rootView
				.findViewById(R.id.tv_fragment_home_username);

		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
				"Walkway_Bold.ttf");
		Typeface tf2 = Typeface.createFromAsset(getActivity().getAssets(),
				"CODE Bold.otf");
		Typeface tf3 = Typeface.createFromAsset(getActivity().getAssets(),
				"Nexa Light.otf");

		timeTxt.setTypeface(tf);
		dateTxt.setTypeface(tf2);
		nameTxt.setTypeface(tf3);

		timeLogBTN = (ImageView) rootView
				.findViewById(R.id.iv_fragment_home_timeintimeout);

		nameTXT.setText("Hello, " + prefs.getString(FNAME, null) + "!");

		getActivity().registerReceiver(timeReceiver,
				new IntentFilter("gztrackz.update.time"));

		timeTXT.setText(hourDisplay + ":" + minutesDisplay);
		amPmTXT.setText(amPmDisplay);
		dateTXT.setText(dateDisplay);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			homeInterface = (MyInterface) activity;

		} catch (Exception e) {
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("loggedIn", loggedIn);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(timeReceiver);
		timeThread = null;
	}

	// fetch date and time
	private class AlreadyLogged extends AsyncTask<String, Void, Boolean> {
		String email;
		Context context;
		ProgressDialog progressD;
		String date = null, time = null;
		boolean timeIn;

		public AlreadyLogged(Context context, String email) {
			this.context = context;
			this.email = email;
		}

		@Override
		protected void onPreExecute() {
			progressD = new ProgressDialog(context);
			progressD.setMessage("Checking User Timelog Status!");
			progressD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressD.setCanceledOnTouchOutside(false);
			progressD.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (progressD.isShowing()) {
				progressD.dismiss();
			}
			loggedIn = timeIn;
			if (loggedIn) {
				// timeLogBTN.setText("Log Out!");
				timeLogBTN.setImageResource(R.drawable.inactivetimeout);
				timeIMG = R.drawable.inactivetimeout;
			} else {
				// timeLogBTN.setText("Log In!");
				timeLogBTN.setImageResource(R.drawable.inactivetimein);
				timeIMG = R.drawable.inactivetimein;
			}

			if (date != null && time != null) {
				String dayOfTheWeek = null, stringMonth = null;
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date dateOutput = format.parse(date);
					dayOfTheWeek = (String) android.text.format.DateFormat
							.format("EEEE", dateOutput);
					stringMonth = (String) android.text.format.DateFormat
							.format("MMM", dateOutput);

				} catch (Exception e) {
					e.printStackTrace();
				}

				dateDisplay = dayOfTheWeek + ", " + stringMonth + " "
						+ date.substring(8, date.length());

				dateTXT.setText(dateDisplay);

				timeThread = new Thread() {
					@Override
					public void run() {
						try {
							int minutes = Integer
									.parseInt(time.substring(3, 5));
							int hours = Integer.parseInt(time.substring(0, 2));
							while (true) {
								Intent sendTimeBroadcast = new Intent();
								sendTimeBroadcast
										.setAction("gztrackz.update.time");
								sendTimeBroadcast.putExtra("minutes",
										Integer.toString(minutes));
								sendTimeBroadcast.putExtra("hours",
										Integer.toString(hours));
								getActivity().sendBroadcast(sendTimeBroadcast);
								sleep(60000);
								minutes++;
								if (minutes == 60) {
									hours++;
									minutes = 0;
								}
							}
						} catch (Exception e) {
							this.interrupt();
						}
					}
				};
				timeThread.start();
			} else {
				dateTXT.setText("--------, ----- -");

			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean flag = true;
			try {
				String urlTopTracks = "http://gz123.site90.net/loginstatus/?email="
						+ email;

				HttpClient client = new DefaultHttpClient();
				ResponseHandler<String> handler = new BasicResponseHandler();

				HttpPost request = new HttpPost(urlTopTracks);

				String httpResponseTopTracks = client.execute(request, handler);

				StringTokenizer token = new StringTokenizer(
						httpResponseTopTracks, "<");
				String retrieveResult = token.nextToken();

				JSONObject result = new JSONObject(retrieveResult);
				String emailResult = result.getString("active");
				if (emailResult.compareToIgnoreCase("true") == 0) {
					timeIn = true;
				} else {
					timeIn = false;
				}
				date = result.getString("date");
				time = result.getString("time");
				Log.d("RESULT", retrieveResult);

			} catch (Exception e) {
				flag = false;
				e.printStackTrace();
			}

			return flag;
		}
	}

	// update date and time views
	private class TimeUpdate extends AsyncTask<String, Void, Boolean> {
		String email, password;
		Context context;
		ProgressDialog progressD;
		String date = null, time = null;
		boolean timeIn;

		public TimeUpdate(Context context, String email) {
			this.context = context;
			this.email = email;
			this.password = password;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (date != null && time != null) {
				String dayOfTheWeek = null, stringMonth = null;
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date dateOutput = format.parse(date);
					dayOfTheWeek = (String) android.text.format.DateFormat
							.format("EEEE", dateOutput);
					stringMonth = (String) android.text.format.DateFormat
							.format("MMM", dateOutput);

				} catch (Exception e) {
					e.printStackTrace();
				}

				dateDisplay = dayOfTheWeek + ", " + stringMonth + " "
						+ date.substring(8, date.length());

				dateTXT.setText(dateDisplay);

				timeThread = new Thread() {
					@Override
					public void run() {
						try {
							int minutes = Integer
									.parseInt(time.substring(3, 5));
							int hours = Integer.parseInt(time.substring(0, 2));
							while (true) {
								Intent sendTimeBroadcast = new Intent();
								sendTimeBroadcast
										.setAction("gztrackz.update.time");
								sendTimeBroadcast.putExtra("minutes",
										Integer.toString(minutes));
								sendTimeBroadcast.putExtra("hours",
										Integer.toString(hours));
								getActivity().sendBroadcast(sendTimeBroadcast);
								sleep(60000);
								minutes++;
								if (minutes == 60) {
									hours++;
									minutes = 0;
								}
							}
						} catch (Exception e) {
							this.interrupt();
						}
					}
				};
				timeThread.start();
			} else {
				dateTXT.setText("--------, ----- -");
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean flag = true;
			try {
				String urlTopTracks = "http://gz123.site90.net/loginstatus/?email="
						+ email;
				HttpClient client = new DefaultHttpClient();
				ResponseHandler<String> handler = new BasicResponseHandler();

				HttpPost request = new HttpPost(urlTopTracks);

				String httpResponseTopTracks = client.execute(request, handler);

				StringTokenizer token = new StringTokenizer(
						httpResponseTopTracks, "<");

				String retrieveResult = token.nextToken();

				JSONObject result = new JSONObject(retrieveResult);
				String emailResult = result.getString("active");

				if (emailResult.compareToIgnoreCase("true") == 0) {
					timeIn = true;
				} else {
					timeIn = false;
				}

				date = result.getString("date");
				time = result.getString("time");
				Log.d("RESULT", retrieveResult);

			} catch (Exception e) {
				flag = false;
				e.printStackTrace();
			}
			return flag;
		}
	}

	// register time logs to webhost
	private class TimeLog extends AsyncTask<String, Void, Boolean> {
		String email, password;
		Context context;
		ProgressDialog progressD;
		String date, time;
		boolean timeIn;
		double longitude, latitude;

		public TimeLog(Context context, String email, boolean timeIn) {
			this.context = context;
			this.email = email;
			this.password = password;
			this.timeIn = timeIn;
			this.longitude = 0;
			this.latitude = 0;
		}

		public TimeLog(Context context, String email, boolean timeIn,
				double longitude, double latitude) {
			this.context = context;
			this.email = email;
			this.password = password;
			this.timeIn = timeIn;
			this.longitude = longitude;
			this.latitude = latitude;
		}

		@Override
		protected void onPreExecute() {
			progressD = new ProgressDialog(context);
			if (timeIn)
				progressD.setMessage("TimeOut in progress...");
			else
				progressD.setMessage("TimeIn in progress...");
			progressD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressD.show();
			progressD.setCanceledOnTouchOutside(false);
			Log.d("PreCheck", Boolean.toString(timeIn));

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (progressD.isShowing()) {
				progressD.dismiss();
			}
			if (result) {
				if (!timeIn) {
					Toast.makeText(context,
							"Successfully timed in at " + time + ".",
							Toast.LENGTH_LONG).show();
					timeIMG = R.drawable.inactivetimeout;
					timeLogBTN.setImageResource(R.drawable.inactivetimeout);
					new StandupCheck(context, email).execute();
				} else {
					Toast.makeText(context,
							"Successfully timed out at " + time + ".",
							Toast.LENGTH_LONG).show();
					timeIMG = R.drawable.inactivetimein;
					timeLogBTN.setImageResource(R.drawable.inactivetimein);
				}

			}

			else
				Toast.makeText(
						context,
						"Unable to execute time in. Please check internet connection!",
						Toast.LENGTH_LONG).show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean flag = true;

			try {
				String urlTopTracks;
				HttpClient client = new DefaultHttpClient();
				ResponseHandler<String> handler = new BasicResponseHandler();

				HttpPost request;

				String httpResponseTopTracks;

				StringTokenizer token;
				String retrieveResult;

				JSONObject result;
				String emailResult;
				Log.d("PreCheck", Boolean.toString(timeIn));
				
				if (timeIn) {
					
					urlTopTracks = "http://gz123.site90.net/timeout/?email="
							+ email;
					
					request = new HttpPost(urlTopTracks);
					httpResponseTopTracks = client.execute(request, handler);

					token = new StringTokenizer(httpResponseTopTracks, "<");
					retrieveResult = token.nextToken();

					result = new JSONObject(retrieveResult);
					emailResult = result.getString("email");
					if (emailResult.length() == 0) {
						flag = false;
					} else {
						date = result.getString("date");
						time = result.getString("time");
						flag = true;
					}
					Log.d("Time out", Boolean.toString(timeIn));
				} else {
					urlTopTracks = "http://gz123.site90.net/timein/?email="
							+ email + "&longitude="
							+ Double.toString(longitude) + "&latitude="
							+ Double.toString(latitude);
					request = new HttpPost(urlTopTracks);
					httpResponseTopTracks = client.execute(request, handler);

					token = new StringTokenizer(httpResponseTopTracks, "<");
					retrieveResult = token.nextToken();

					result = new JSONObject(retrieveResult);
					emailResult = result.getString("email");
					if (emailResult.length() == 0) {
						flag = false;
					} else {
						date = result.getString("date");
						time = result.getString("time");
						flag = true;
					}
					Log.d("Time In", Boolean.toString(timeIn));
				}
			} catch (Exception e) {
				flag = false;
				e.printStackTrace();
			}

			return flag;
		}
	}

	// check standups
	private class StandupCheck extends AsyncTask<String, Void, Boolean> {
		String email, password;
		Context context;
		ProgressDialog progressD;
		String date, time;
		boolean standupAvailable;

		public StandupCheck(Context context, String email) {
			this.context = context;
			this.email = email;
			this.password = password;
		}

		@Override
		protected void onPreExecute() {
			progressD = new ProgressDialog(context);
			progressD.setMessage("Checking standup status...");
			progressD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressD.setCanceledOnTouchOutside(false);
			progressD.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean flag = true;
			try {
				String urlTopTracks = "http://gz123.site90.net/standups_status/?email="
						+ email;
				HttpClient client = new DefaultHttpClient();
				ResponseHandler<String> handler = new BasicResponseHandler();

				HttpPost request = new HttpPost(urlTopTracks);

				String httpResponseTopTracks = client.execute(request, handler);

				StringTokenizer token = new StringTokenizer(
						httpResponseTopTracks, "<");
				String retrieveResult = token.nextToken();

				if (retrieveResult.contains("empty")) {
					standupAvailable = true;
				} else {
					standupAvailable = false;
				}
				Log.d("Standup Status", retrieveResult);

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

			if (standupAvailable) {
				Intent i = new Intent(context, StandUpsDialog.class);
				i.putExtra("email", email);
				startActivityForResult(i, 1);
			}
		}
	}

	// why broadcast reciever ~_~ why???
	BroadcastReceiver timeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			int hours = Integer.parseInt(arg1.getStringExtra("hours"));
			int minutes = Integer.parseInt(arg1.getStringExtra("minutes"));

			if (hours > 12) {
				hours -= 12;
				amPmDisplay = "PM";
			} else {
				amPmDisplay = "AM";
			}
			if (hours < 10) {
				hourDisplay = "0" + Integer.toString(hours);
			} else {
				hourDisplay = Integer.toString(hours);
			}
			if (minutes < 10) {
				minutesDisplay = "0" + Integer.toString(minutes);
			} else {
				minutesDisplay = Integer.toString(minutes);
			}
			timeTXT.setText(hourDisplay + ":" + minutesDisplay);
			amPmTXT.setText(amPmDisplay);
		}
	};

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) getActivity()
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
}
