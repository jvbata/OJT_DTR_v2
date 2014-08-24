package com.gztracks.gztracks;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.gztrackz.R;
import com.gztracks.home.GZ_ViewPagerPageListener;
import com.gztracks.tabsadapter.TabsPagerAdapter;

public class TabsManager extends FragmentActivity implements
		ActionBar.TabListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "History", "Home", "StandUps" };
	private String PREFERENCE_NAME = "com.example.gztrackz",
			FNAME = "com.example.gztrackz.firstname",
			LNAME = "com.example.gztrackz.lastname",
			EMAIL = "com.example.gztrackz.email";
	private String email;

	private Context context;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager);

		init();

	}

	private void init() {

		prefs = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		context = this;
		viewPager.setAdapter(mAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setTitle(prefs.getString(FNAME, null) + " "
				+ prefs.getString(LNAME, null));
		email = getIntent().getStringExtra("email");

		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		actionBar.setSelectedNavigationItem(0);

		viewPager.setOnPageChangeListener(new GZ_ViewPagerPageListener(
				actionBar));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.time_manager, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_logout:
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						SharedPreferences.Editor editor = prefs.edit();
						setResult(RESULT_OK);
						editor.putString(LNAME, null);
						editor.putString(FNAME, null);
						editor.putString(EMAIL, null);
						editor.commit();
						finish();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						break;
					}
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage("Are you sure?")
					.setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener)
					.setCancelable(false).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}
