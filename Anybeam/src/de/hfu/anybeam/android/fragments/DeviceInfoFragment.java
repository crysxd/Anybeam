package de.hfu.anybeam.android.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import de.hfu.anybeam.android.R;
import de.hfu.anybeam.android.SendActivity;
import de.hfu.anybeam.android.utils.DeviceIconUtils;
import de.hfu.anybeam.networkCore.Client;

/**
 * Fragment to display a additional client info in the {@link SendActivity}
 * @author preussjan
 * @since 1.0
 * @version 1.0
 */
public class DeviceInfoFragment extends DialogFragment{
	private TextView tvAddress;
	private TextView tvId;
	private TextView tvGroup;
	private TextView tvOs;

    public DeviceInfoFragment() {
        // Empty constructor required for DialogFragment
    }

    /**
     * Load the information into the layout.
     * @param client the {@link Client} to display
     * @return the fragment to display
     */
    public static DeviceInfoFragment newInstance(Client client) {
    	DeviceInfoFragment frag = new DeviceInfoFragment();
        Bundle args = new Bundle();
        args.putString("title", client.getName());
        args.putString("address", client.getAddress(client.getBestProvider()).toString());
        args.putString("group", client.getBestProvider().getName());
        args.putString("os", client.getOsName());
        args.putInt("deviceType", DeviceIconUtils.getIconForDeviceType(client.getDeviceType()));
        
        String id = client.getId();
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
        tvOs.setText(getArguments().getString("os", "Unknown"));

        return view;
    }
}
