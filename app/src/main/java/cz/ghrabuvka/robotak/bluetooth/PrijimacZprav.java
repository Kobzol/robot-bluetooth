package cz.ghrabuvka.robotak.bluetooth;

import java.io.IOException;
import java.io.InputStream;

import android.os.AsyncTask;
import android.util.Log;
import cz.ghrabuvka.robotak.nastaveni.Nastaveni;

public class PrijimacZprav extends AsyncTask<Komunikator, String, Void>
{
	private Komunikator komunikator;
	
	@Override
	protected void onProgressUpdate(String... params)
	{
		Odpoved odpoved = new Odpoved(params[0]);
		komunikator.PrijmiOdpoved(odpoved);
		komunikator.ZpravaPrijata();
	}
	
	@Override
	protected Void doInBackground(Komunikator... params)
	{
		this.komunikator = params[0];
		
		try
		{
			InputStream proudDat = komunikator.VratSpojeni().getInputStream();
			
			String zprava = "";
			int znak = 0;
			
			while (komunikator.JePripojeneZarizeni() && !this.isCancelled())
			{
				znak = proudDat.read();
				
				if (znak == Nastaveni.ZPRAVA_PRIJEM_TERMINACNI_ZNAK)
				{
					if (komunikator.LzePrijmoutZpravu())
					{
						publishProgress(zprava);
					}
					
					zprava = "";
				}
				else zprava += (char) znak;
			}
		}
		catch (IOException e)
		{
			Log.i("comm", "error receive - " + e.getLocalizedMessage());
		}	
		
		return null;
	}
}