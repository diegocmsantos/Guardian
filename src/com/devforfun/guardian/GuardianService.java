package com.devforfun.guardian;

import java.util.Date;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;

public class GuardianService extends Service {
	
	private float startVoz;
	private float endVoz;
	private float totalVoz;

	@Override
	public void onStart(Intent intent, int startId) {
		float voz = intent.getFloatExtra("voz", 0);
		float dados = intent.getFloatExtra("dados", 0);
		long data = intent.getLongExtra("data", 0);
		String action = intent.getStringExtra("action");
		
		if ("OFFHOOK".equals(action)) {
			startVoz = new Date().getTime() / 60000;
			System.out.println("START VOZ OFFHOOK: " + startVoz);
		} else if ("IDLE".equals(action)) {
			endVoz = new Date().getTime() / 60000;
			System.out.println("START VOZ: " + startVoz);
			System.out.println("END VOZ: " + endVoz);
			totalVoz = endVoz - startVoz;
			System.out.println("TOTAL VOZ: " + totalVoz);
			System.out.println(totalVoz);
		}
		
//		System.out.println("CHEGOU " + voz + " TEMPO DE VOZ");
//		System.out.println("CHEGOU " + dados + " DADOS");
//		System.out.println("CHEGOU " + data + " DATA");
		
//		Indice indice = new Indice(voz, 0, null);
//		addData(indice);
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void addData(Indice _indice) {
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		
		float voz = 0;
		float dados = 0;
		long data = 0;
		
		Cursor allRows = cr.query(GuardianProvider.CONTENT_URI, null, null, null, null);
		if (allRows.moveToFirst()) {
			do {
				// Extract the quake details.
				voz = allRows.getFloat(GuardianProvider.VOZ_COLUMN);
				dados = allRows.getFloat(GuardianProvider.DADOS_COLUMN);
				data = allRows.getLong(GuardianProvider.DATA_COLUMN);
				
			} while (allRows.moveToNext());
		}
		allRows.close();
		
		voz =+ _indice.getVoz();
		dados =+ _indice.getDados();

		values.put(GuardianProvider.KEY_VOZ, voz);
		values.put(GuardianProvider.KEY_DADOS, dados);
		values.put(GuardianProvider.KEY_DATA, data);

		if (voz != 0 || dados != 0) {
			if (data != 0) {
				cr.insert(GuardianProvider.CONTENT_URI, values);
			}
		}
	}

}
