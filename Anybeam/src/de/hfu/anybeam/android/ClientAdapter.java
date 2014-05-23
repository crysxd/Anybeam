package de.hfu.anybeam.android;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.hfu.anybeam.android.utils.DeviceIconUtils;
import de.hfu.anybeam.networkCore.Client;

/**
 * Adapter to fill the client listview with entrys
 * @author preussjan
 * @since 1.0
 * @version 1.0
 */
public class ClientAdapter extends ArrayAdapter<Client> {

	/**
	 * Constructor
	 * @param context the application {@link Context}
	 * @param clients list of current found clients
	 */
	public ClientAdapter(Context context, ArrayList<Client> clients) {
		super(context, R.layout.client_item, clients);
	}
	
	//Modifyed getCount() to call getView() even if there are no clients 
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
		
		//Checks if there are really no devices
		if (super.getCount() == 0) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.client_not_found, parent, false);
			
			// Lookup view for data population
			TextView tv = (TextView) convertView
					.findViewById(R.id.tvDeviceNotFound);

			// Populate the data into the template view using the data object
			tv.setText(R.string.send_clients_not_found);
			
			// Return the completed view to render on screen
			return convertView;
		}

		// Get the data item for this position
		Client c = getItem(position);

		// Lookup view for data population
		ImageView ivDeviceImage = (ImageView) convertView
				.findViewById(R.id.ivDeviceType);
		TextView tvDeviceName = (TextView) convertView
				.findViewById(R.id.tvDeviceName);
		TextView tvDeviceInfo = (TextView) convertView
				.findViewById(R.id.tvDeviceInfo);

		// Populate the data into the template view using the data object
		ivDeviceImage.setImageResource(DeviceIconUtils.getIconForDeviceType(c.getDeviceType()));
		tvDeviceName.setText(c.getName());
		
		if(c.getBestProvider() != null)
			tvDeviceInfo.setText(c.getBestProvider().getName());
		else
			tvDeviceInfo.setText("--");

		// Return the completed view to render on screen
		return convertView;
	}
}
