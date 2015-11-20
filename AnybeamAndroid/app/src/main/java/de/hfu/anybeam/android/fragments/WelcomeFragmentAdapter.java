package de.hfu.anybeam.android.fragments;

import org.goodev.helpviewpager.HelpFragmentPagerAdapter;

import de.hfu.anybeam.android.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class WelcomeFragmentAdapter extends HelpFragmentPagerAdapter {
	protected static final int[] CONTENT = new int[] {
		R.layout.fragment_welcome1, 
		R.layout.fragment_welcome2, 
		R.layout.fragment_welcome3, 
		R.layout.fragment_welcome4};

	private int mCount = CONTENT.length;

	public WelcomeFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getHelpItem(int position) {
		
		return WelcomeFragment.newInstance(CONTENT[position % CONTENT.length]);
	}

	@Override
	public int getHelpCount() {
		return mCount;
	}
}