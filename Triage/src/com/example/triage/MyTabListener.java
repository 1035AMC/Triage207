package com.example.triage;

import android.app.*;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;

/**
 * This class is a Tablistener which shifts between fragments in a tabbed view.
 * @author Connor Yoshimoto
 * 
 */
public class MyTabListener implements TabListener {
	/**
	 * The fragment to be used.
	 */
	Fragment fragment;

	/**
	 * 
	 * @param fragment - 
	 */
	public MyTabListener(Fragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.fragment_container, fragment);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(fragment);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// nothing done here
	}
}
