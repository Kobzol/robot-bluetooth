package cz.ghrabuvka.robotak.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import cz.ghrabuvka.robotak.nastaveni.Nastaveni;
import cz.ghrabuvka.robotak.udalosti.ZpravaPrijata;

public class Komunikator
{
	private BluetoothSocket spojeni;	
	private char terminacniZnak;
	
	private HashMap<String, ZpravaPrijata> udalosti = new HashMap<String, ZpravaPrijata>();
	private PrijimacZprav prijimacZprav;
	
	private Handler periodickyOdesilac = new Handler();
	
	private long casPosledniZpravy = System.nanoTime();
	
	public Komunikator(BluetoothSocket spojeni, char terminacniZnak)
	{
		this.spojeni = spojeni;
		this.terminacniZnak = terminacniZnak;
	}
	
	public boolean JePripojeneZarizeni()
	{
		return spojeni != null && spojeni.isConnected();
	}
	public BluetoothSocket VratSpojeni()
	{
		return this.spojeni;
	}
	
	public boolean OdesliZpravu(Prikaz prikaz)
	{
		return this.OdesliZpravu(prikaz.VratCelyPrikaz());
	}
	public boolean OdesliZpravu(String zprava)
	{
		if (this.JePripojeneZarizeni())
    	{
			Log.i("odeslano", zprava);
			
			OutputStream os;
			try
			{
				os = spojeni.getOutputStream();
				
				zprava += terminacniZnak;
				
				byte[] buffer = zprava.getBytes("ASCII");
	    	
				os.write(buffer);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
			
			return true;
    	}
		else return false;
	}
	
	public void ZacniPrijimat()
	{
	 	if (this.prijimacZprav == null || this.prijimacZprav.isCancelled())
	 	{
	 		this.prijimacZprav = new PrijimacZprav();
	 		this.prijimacZprav.execute(this);
	 	}
	}
	public void PrestanPrijimat()
	{
		if (this.prijimacZprav != null)
		{
			this.prijimacZprav.cancel(false);
		}
	}
	
	public void PrijmiOdpoved(Odpoved odpoved)
	{
		String kategorie = odpoved.VratKategorii();
		
		if (udalosti.containsKey(kategorie))
		{
			udalosti.get(kategorie).ZpracujOdpoved(odpoved);
		}
	}
	
	public void ZpravaPrijata()
	{
		this.casPosledniZpravy = System.nanoTime();
	}
	public boolean LzePrijmoutZpravu()
	{
		return ((System.nanoTime() - this.casPosledniZpravy) / 1e3) > Nastaveni.POCET_MILISEKUND_MEZI_ZPRAVAMI;
	}
	
	public void ZaregistrujUdalost(String kategorie, ZpravaPrijata udalost)
	{
		this.udalosti.put(kategorie, udalost);
	}
	public void ZrusUdalosti()
	{
		this.udalosti.clear();
	}

	public void ZaregistrujSignal(final Signal signal)
	{
		final Komunikator komunikator = this;
		Runnable periodickySignal = new Runnable()
		{	
			@Override
			public void run()
			{
				komunikator.OdesliZpravu(signal);
				periodickyOdesilac.postDelayed(this, signal.VratPeriodu());
			}
		};
		
		periodickyOdesilac.postDelayed(periodickySignal, signal.VratPeriodu());
	}
	public void ZaregistrujSignaly(List<Signal> seznamSignalu)
	{
		/*for (Signal signal : seznamSignalu) TODO
		{
			this.ZaregistrujSignal(signal);
		}*/
	}
	public void ZrusSignaly()
	{
		this.periodickyOdesilac.removeCallbacksAndMessages(null);
	}
}
