package de.hfu.anybeam;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		Client c = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.client_item, parent, false);
		}
		
		
		// Lookup view for data population
		ImageView ivDeviceImage = (ImageView) convertView.findViewById(R.id.ivDeviceType);
		TextView tvDeviceName = (TextView) convertView.findViewById(R.id.tvDeviceName);
		TextView tvDeviceIP = (TextView) convertView.findViewById(R.id.tvDeviceIP);
		// Populate the data into the template view using the data object
				
		switch (c.getDeviceType()) {
		case TYPE_SMARPHONE:
			//Selects Phone Icon
			ivDeviceImage.setImageResource(R.drawable.ic_device_phone);
			break;
		case TYPE_TABLET:
			//Selects Tablet Icon
			ivDeviceImage.setImageResource(R.drawable.ic_device_tablet);
			break;
		case TYPE_LAPTOP:
			//Selects Laptop Icon
			ivDeviceImage.setImageResource(R.drawable.ic_device_laptop);
			break;
		case TYPE_DESKTOP:
			//Selects Computer Icon
			ivDeviceImage.setImageResource(R.drawable.ic_device_computer);			
			break;

		default:
			//Selects Device Unknown Icon
			ivDeviceImage.setImageResource(R.drawable.ic_device_unknown);
			break;
		}
		
		tvDeviceName.setText(c.getName());
		tvDeviceName.setTextColor(Color.BLACK);
		tvDeviceIP.setText(c.getAddress().toString());
		tvDeviceIP.setTextColor(Color.BLACK);
		// Return the completed view to render on screen
		return convertView;
	}
	
}
