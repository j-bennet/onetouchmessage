package com.chaotix.onetouchmessage;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
	
	private static String tag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void openContacts(View sender)
    {
        Log.d(tag, "Opening contacts");
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, MessageApp.RESULT_CONTACT_PICKER);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case MessageApp.RESULT_CONTACT_PICKER:
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
        }
    }
    
	private void retrievePhoneNumbers(Uri contactUri) {
		String contactId = contactUri.getLastPathSegment();

		ContentResolver resolver = getContentResolver();

		Cursor phones = resolver.query(Phone.CONTENT_URI, null,
				Phone.CONTACT_ID + " = ?", new String[] { contactId }, null);

		while (phones.moveToNext()) {
			String number = phones.getString(phones
					.getColumnIndex(Phone.NUMBER));
			int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
			switch (type) {
				default:
					Log.d(tag, "Phone #: " + number);
					break;
			}
		}
		phones.close();
	}
    
    private void sendMessageTo(String phoneNumber) {
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
}
