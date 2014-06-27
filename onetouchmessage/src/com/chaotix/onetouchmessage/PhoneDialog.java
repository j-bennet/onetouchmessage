package com.chaotix.onetouchmessage;

import com.chaotix.onetouchmessage.interfaces.PhoneSelector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class PhoneDialog extends DialogFragment {
	
	private Activity activity;
	private PhoneSelector parent;
	private static final String tag = "PhoneDialogFragment";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		activity = getActivity();
		parent = (PhoneSelector) activity;
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    
		builder.setTitle(R.string.select_phone).setItems(parent.getAllPhones(),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String phoneNumber = parent.getAllPhones()[which];
						Log.d(tag, String.format("Selected #: %s", phoneNumber));
						parent.setSelectedPhone(phoneNumber);
					}
				});
	    return builder.create();
	}
}
