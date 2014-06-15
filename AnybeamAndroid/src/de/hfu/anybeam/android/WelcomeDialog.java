package de.hfu.anybeam.android;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class WelcomeDialog extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.dialog_welcome_text)
		.setTitle(R.string.dialog_welcome_title)
		.setPositiveButton(R.string.dialog_welcome_done, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		
		// Create the AlertDialog object and return it
		return builder.create();
	}
}