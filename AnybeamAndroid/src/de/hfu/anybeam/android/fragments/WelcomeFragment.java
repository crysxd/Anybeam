package de.hfu.anybeam.android.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import de.hfu.anybeam.android.R;

public final class WelcomeFragment extends Fragment {
	private int mView = 0;

	public static WelcomeFragment newInstance(int view) {
		WelcomeFragment fragment = new WelcomeFragment();
		fragment.mView = view;		
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		View view = inflater.inflate(mView,  null, false);
		
		if (mView == R.layout.fragment_welcome4) {
			EditText etPassword = (EditText) view.findViewById(R.id.etPassword);
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			etPassword.setText(prefs.getString("group_password", ""));
		}
		
		return view;
	}


	


}
