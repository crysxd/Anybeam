package de.hfu.anybeam.android.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import de.hfu.anybeam.android.DeviceIconUtils;
import de.hfu.anybeam.android.R;
import de.hfu.anybeam.networkCore.Client;

public class DeviceInfoFragment extends DialogFragment{
	private TextView tvAddress;
	private TextView tvId;
	private TextView tvGroup;
	private TextView tvOs;

    public DeviceInfoFragment() {
        // Empty constructor required for DialogFragment
    }

    public static DeviceInfoFragment newInstance(Client c) {
    	DeviceInfoFragment frag = new DeviceInfoFragment();
        Bundle args = new Bundle();
        args.putString("title", c.getName());
        args.putString("address", c.getAddress(c.getBestProvider()).toString());
        args.putString("group", c.getBestProvider().getName());
        args.putString("os", c.getOsName());
        args.putInt("deviceType", DeviceIconUtils.getIconForDeviceType(c.getDeviceType()));
        
        String id = c.getId();
        //Shorten Id
        if (id.length() > 20) {
			args.putString("id", id.substring(0,20) + "...");						
		} else {
			args.putString("id", id);			
		}
        
        
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_info, container);
        
        tvAddress = (TextView) view.findViewById(R.id.tvDeviceInfoAddress);
        tvId = (TextView) view.findViewById(R.id.tvDeviceInfoId);
        tvGroup = (TextView) view.findViewById(R.id.tvDeviceInfoGroup);
        tvOs = (TextView) view.findViewById(R.id.tvDeviceInfoOS);

        getDialog().setTitle(getArguments().getString("title", "Unknown"));
        getDialog().requestWindowFeature(Window.FEATURE_LEFT_ICON);
        getDialog().setContentView(R.layout.fragment_device_info);
        getDialog().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, getArguments().getInt("deviceType"));

        tvAddress.setText(getArguments().getString("address", "Unknown"));
        tvId.setText(getArguments().getString("id", "Unknown"));
//        tvGroup.setText(getArguments().getString("group", "Unknown"));
        tvOs.setText(getArguments().getString("os", "Unknown"));

        return view;
    }
}
