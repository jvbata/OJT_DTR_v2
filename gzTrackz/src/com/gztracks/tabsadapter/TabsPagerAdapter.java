package com.gztracks.tabsadapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gztracks.home.Fragment_Home;
import com.gztracks.home.HomeFragment;
import com.gztracks.tabs.StandupsFragment;
import com.gztracks.tabs.TimestampsFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new TimestampsFragment();
		case 1:
			// Games fragment activity			
			return new Fragment_Home();
		case 2:
			// Movies fragment activity
			return new StandupsFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
