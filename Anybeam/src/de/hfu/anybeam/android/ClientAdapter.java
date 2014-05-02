package de.hfu.anybeam.android;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.hfu.anybeam.networkCore.Client;

public class ClientAdapter extends ArrayAdapter<Client> {

	public ClientAdapter(Context context, ArrayList<Client> clients) {
		super(context, R.layout.client_item, clients);
	}
	
	@Override
	public int getCount() {
		int count = super.getCount();
		return count == 0 ? 1 : count;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.client_item, parent, false);
		}
		
		//Checks if there are no devices
		if (super.getCount() == 0) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.client_not_found, parent, false);
			
			// Lookup view for data population
			TextView tv = (TextView) convertView
					.findViewById(R.id.tvDeviceNotFound);

			// Populate the data into the template view using the data object
			tv.setText(R.string.noclients);
			
			// Return the completed view to render on screen
			return convertView;
		}

		// Get the data item for this position
		Client c = getItem(position);

		// Lookup view for data population
		ImageView ivDeviceImage = (ImageView) convertView
				.findViewById(R.id.ivDeviceType);
		ImageView ivWarningImage = (ImageView) convertView
				.findViewById(R.id.ivWarningIcon);
		TextView tvDeviceName = (TextView) convertView
				.findViewById(R.id.tvDeviceName);

		// Populate the data into the template view using the data object
		ivDeviceImage.setImageResource(DeviceIconUtils.getIconForDeviceType(c.getDeviceType()));
		ivWarningImage.setVisibility(c.isEncryptionKeyCompatible() ? View.GONE : View.VISIBLE);
		tvDeviceName.setText(c.getName());

		// Return the completed view to render on screen
		return convertView;
	}
}
