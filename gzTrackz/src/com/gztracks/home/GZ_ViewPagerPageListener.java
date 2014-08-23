package com.gztracks.home;

import android.app.ActionBar;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class GZ_ViewPagerPageListener implements OnPageChangeListener {
	
	ActionBar actionBar;
	
	public GZ_ViewPagerPageListener(ActionBar actionBar) {
		
		this.actionBar = actionBar;
	}

	@Override
	public void onPageSelected(int position) {
		// on changing the page
		// make respected tab selected
		actionBar.setSelectedNavigationItem(position);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

}
