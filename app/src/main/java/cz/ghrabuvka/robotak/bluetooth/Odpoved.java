package cz.ghrabuvka.robotak.bluetooth;

import android.util.Log;
import cz.ghrabuvka.robotak.nastaveni.Nastaveni;

public class Odpoved
{
	private String kategorie = "";
	private String hodnota = "";
	
	public Odpoved(String zprava)
	{
		String[] polozkyZpravy = zprava.split(String.valueOf(Nastaveni.ZPRAVA_ROZDELOVAC_ZPRAVY));
		
		if (polozkyZpravy.length > 0)
		{
			this.kategorie = polozkyZpravy[0];
		}
		
		if (polozkyZpravy.length > 1)
		{	
			for (int i = 1; i < polozkyZpravy.length; i++)
			{
				this.hodnota += polozkyZpravy[i];
			}
			
			this.hodnota = this.hodnota.replace(String.valueOf(Nastaveni.ZPRAVA_PRIJEM_TERMINACNI_ZNAK), "");
		}
		
		Log.i("odpoved", this.kategorie + " " + this.hodnota + " (" + zprava + ")");
	}
	
	public String VratKategorii()
	{
		return this.kategorie;
	}
	
	public String VratHodnotu()
	{
		return this.hodnota;
	}
}
