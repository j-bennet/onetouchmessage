package com.chaotix.onetouchmessage;

import com.chaotix.onetouchmessage.interfaces.PhoneSelector;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends FragmentActivity implements PhoneSelector {
	
	private static String tag = "MainActivity";
	private static final int RESULT_CONTACT_PICKER = 1001;
	private String[] allPhones = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        View btCreate = findViewById(R.id.btCreate);
        btCreate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				openContacts(v);
			}});
    }
    
    public void openContacts(View sender)
    {
        Log.d(tag, "Opening contacts");
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, RESULT_CONTACT_PICKER);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case RESULT_CONTACT_PICKER:
                    addMessageFor(data);
                    break;
            }
        }
        else
        {
            Log.d(tag, String.format("Contact picker result code: %s", resultCode));
        }
    }
    
    private void addMessageFor(Intent contactData) {
        Uri contactUri = contactData.getData();
        
        if (contactUri != null)
        {
            Log.d(tag, String.format("Contact URI: %s", contactUri));
            retrievePhoneNumbers(contactUri);
            showSelectPhoneDialog();
        }
    }
    
	private void retrievePhoneNumbers(Uri contactUri) {
		String contactId = contactUri.getLastPathSegment();

		ContentResolver resolver = getContentResolver();

		Cursor phones = resolver.query(Phone.CONTENT_URI, null,
				Phone.CONTACT_ID + " = ?", new String[] { contactId }, null);
		
		allPhones = new String[phones.getCount()];
		int i = 0;

		while (phones.moveToNext()) {
			allPhones[i] = phones.getString(phones
					.getColumnIndex(Phone.NUMBER));
			int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
			switch (type) {
				default:
					Log.d(tag, "Phone #: " + allPhones[i]);
					break;
			}
			i++;
		}
		phones.close();
	}
	
	private void showSelectPhoneDialog() {
        FragmentManager fm = getSupportFragmentManager();
        PhoneDialog dialog = new PhoneDialog();
        dialog.show(fm, "fmSelectPhone");
    }
    
    private void sendMessageTo(String phoneNumber) {
    	// TODO
        String messageText = "Hey from One Touch Message";
     
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, messageText, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public String[] getAllPhones() {
		return allPhones;
	}

	@Override
	public void setSelectedPhone(String phoneNumber) {
		sendMessageTo(phoneNumber);
	}
}
