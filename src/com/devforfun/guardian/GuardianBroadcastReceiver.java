package com.devforfun.guardian;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GuardianBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "GuardianBroadcastReceiver";

	private Context globalContext;

	private float startVoz;
	private float endVoz;
	private float totalVoz;

	private float dados;

	@Override
	public void onReceive(Context context, Intent intent) {

		globalContext = context;
		
		String action = intent.getAction();
		if (action.equalsIgnoreCase("android.intent.action.PHONE_STATE")) {
			String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			String extra = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			System.out.println(extra);
			if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                    TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//				phoneWasAnswered(number);
				System.out.println("EXTRA_STATE_OFFHOOK");
				sendIntent(0, 0, extra);
			} else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                    TelephonyManager.EXTRA_STATE_IDLE)) {
//				phoneIsIdle(number);
				System.out.println("EXTRA_STATE_OFFHOOK");
				sendIntent(0, 0, extra);
			}
			
		}
		

//		TelephonyManager teleMgr = (TelephonyManager) context
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		teleMgr.listen(new MyPhoneStateListener(),
//				PhoneStateListener.LISTEN_CALL_STATE);

	}

	private void phoneIsIdle(String incomingNumber) {
		Log.d(TAG, "call state idle...incoming number is["
				+ incomingNumber + "]");
		
		Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        endVoz = calendar.getTimeInMillis();
        
        totalVoz = endVoz - startVoz;
        totalVoz = (totalVoz / 60000);
        
        System.out.println("START VOZ: " + startVoz);
        System.out.println("END VOZ: " + endVoz);
        System.out.println("TOTAL VOZ: " + totalVoz);
        
//        sendIntent(totalVoz, 0);
	}

	private void phoneWasAnswered(String incomingNumber) {
		Log.d(TAG, "call state off-hook...incoming number is["
				+ incomingNumber + "]");
		Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        startVoz = calendar.getTimeInMillis();
        System.out.println("START VOZ: " + startVoz);
	}

	private void sendIntent(float voz, float dados, String extra) {
		Intent startIntent = new Intent(globalContext, GuardianService.class);
		startIntent.putExtra("voz", voz);
		// startIntent.putExtra("dados", dados);
		startIntent.putExtra("action", extra);

		globalContext.startService(startIntent);
	}

}
