package com.devforfun.guardian;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Guardian extends Activity {
    
	Float voz;
	Float dados;

	TextView labelVoz;
	TextView textVoz;

	TextView labelDados;
	TextView textDados;

	ArrayAdapter<Indice> aa;
	ArrayList<Indice> indices = new ArrayList<Indice>();

	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        loadIndicesFromProvider();
        
        textVoz = (TextView) this.findViewById(R.id.text_voz);
        if (voz != null)
        	textVoz.setText(voz.toString());
        
        textDados = (TextView) this.findViewById(R.id.text_dados);
        if (dados != null)
        	textDados.setText(dados.toString());
        
        Cursor c = null;
        try {
            c = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                do {
                	String type = c.getString(c.getColumnIndex(CallLog.Calls.TYPE));
                    int duration = c.getInt(c.getColumnIndex(CallLog.Calls.DURATION));
                    String data = c.getString(c.getColumnIndex(CallLog.Calls.DATE));
                    System.out.println("TIPO: " + type);
                    System.out.println("DURACAO: " + duration / 60 + " minutos e " + duration % 60 + " segundos.");
                    System.out.println("DATA: " + formatarTempo(data));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

	private String formatarTempo(String data) {
		long l = Long.parseLong(data);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(l);
		java.util.Date date = cal.getTime();
		return setFormat("dd/MM/yyyy", date).toString();
	}
	
	public String setFormat(String pattern, Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	private void loadIndicesFromProvider() {
		// Clear the existing indices array
		indices.clear();
		ContentResolver cr = getContentResolver();

		// Return all the saved indices
		Cursor c = cr.query(GuardianProvider.CONTENT_URI, null, null, null,
				null);

		if (c.moveToFirst()) {
			do {
				// Extract the quake details.
				voz = c.getFloat(GuardianProvider.VOZ_COLUMN);
				dados = c.getFloat(GuardianProvider.DADOS_COLUMN);
				System.out.println("VOZ: " + voz.toString());
				System.out.println("DADOS: " + dados.toString());
//				Indice q = new Indice(voz, dados);
//				addIndiceToArray(q);
			} while (c.moveToNext());
		}
		c.close();
	}

	private void addIndiceToArray(Indice _indice) {
		indices.add(_indice);

		// Notify the array adapter of a change.
		aa.notifyDataSetChanged();
	}
	
}