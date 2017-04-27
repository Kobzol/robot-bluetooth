package cz.ghrabuvka.robotak.bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import cz.ghrabuvka.robotak.udalosti.ZarizeniPripojeno;

public class PripojovacKZarizeni extends AsyncTask<Object, Void, Boolean>
{
	ZarizeniPripojeno poPripojeni;
	
	@Override
	protected void onPostExecute(Boolean pripojeniUspesne)
	{
		if (pripojeniUspesne)
		{
			poPripojeni.PripojeniKZarizeniUspesne();
		}
		else poPripojeni.PripojeniKZarizeniNeuspesne();
	}
	
	@Override
	protected Boolean doInBackground(Object... params)
	{
		BluetoothSocket komunikator = (BluetoothSocket) params[0];
		poPripojeni = (ZarizeniPripojeno) params[1];		
	   
		try 
		{
			komunikator.connect();
			
			Log.i("comm", SpravceBluetoothu.JePripojenoZarizeni() + "");
			
			return true;
		}
		catch (IOException e)
		{
		   e.printStackTrace();
		   
		   return false;
		}
	}

}