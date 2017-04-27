package cz.ghrabuvka.robotak.aktivity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import cz.ghrabuvka.robotak.R;
import cz.ghrabuvka.robotak.bluetooth.Komunikator;
import cz.ghrabuvka.robotak.bluetooth.Odpoved;
import cz.ghrabuvka.robotak.bluetooth.Prikaz;
import cz.ghrabuvka.robotak.bluetooth.Signal;
import cz.ghrabuvka.robotak.bluetooth.SpravceBluetoothu;
import cz.ghrabuvka.robotak.nastaveni.Nastaveni;
import cz.ghrabuvka.robotak.robot.Motor;
import cz.ghrabuvka.robotak.udalosti.ZpravaPrijata;
import cz.ghrabuvka.robotak.ui.TlacitkoPrikazu;
import cz.ghrabuvka.robotak.ui.TovarnaDialogu;

public class OvladaniZarizeni extends ActionBarActivity
{
	private BluetoothDevice pripojeneZarizeni;
	private Komunikator komunikator;
	
	private ArrayList<Prikaz> seznamPrikazu;
	private ArrayList<Signal> seznamSignalu = new ArrayList<Signal>()
	{
		private static final long serialVersionUID = -3056977654362155816L;
		
		{
			add(new Signal(Nastaveni.PRIKAZ_KOMPAS_ZMER, "", 8000));
			add(new Signal(Nastaveni.PRIKAZ_SONARY_ZMER, "", 5000));
			add(new Signal(Nastaveni.PRIKAZ_MOTORY_STAV, "", 4000));
		}
	};
	
	@Override
 	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ovladani_zarizeni);
		
		if (!SpravceBluetoothu.JePripojenoZarizeni())
		{
			TovarnaDialogu.Upozorneni(this, R.string.ovladani_zarizeni_neni_pripojeno, true).show();
			this.finish();
		}
		else
		{
			this.pripojeneZarizeni = SpravceBluetoothu.VratPripojeneZarizeni();
			this.setTitle(this.getTitle() + " (" + pripojeneZarizeni.getName() + ")");
		}
		
		NactiSeznamPrikazu();
		ZobrazSeznamPrikazu();
		
		NastavUdalostiTlacitek();
		
		NastavPrijemDat();
		NastavSignaly();
	}
	@Override
	protected void onDestroy()
	{
		if (this.komunikator != null)
		{
			this.komunikator.ZrusSignaly();
		}
		
		SpravceBluetoothu.OdpojSeOdZarizeni();
		
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.ovladani_zarizeni, menu);
		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem polozkaMenu)
	{
		if (polozkaMenu.getItemId() == R.id.menu_detail_sonaru)
		{
			startActivity(new Intent(this, DetailSonaru.class));
		}
		else if (polozkaMenu.getItemId() == R.id.menu_detail_kompasu)
		{
			startActivity(new Intent(this, DetailKompasu.class));
		}
		else if (polozkaMenu.getItemId() == R.id.menu_upravit_prikazy)
		{
			Dialog dialogPrikazu = TovarnaDialogu.SeznamPrikazu(this, seznamPrikazu);
			
			dialogPrikazu.setOnDismissListener(new OnDismissListener()
			{
				@Override
				public void onDismiss(DialogInterface dialog)
				{

					NactiSeznamPrikazu();
					ZobrazSeznamPrikazu();	
					NastavUdalostiTlacitek();
				}
			});
			
			dialogPrikazu.show();
		}	
		
		return super.onOptionsItemSelected(polozkaMenu);
	}
	
	@SuppressWarnings("unchecked")
	private void NactiSeznamPrikazu()
	{
		this.seznamPrikazu = (ArrayList<Prikaz>) Nastaveni.NactiData(this, Nastaveni.SOUBOR_PRIKAZY);
	}	
 	private void ZobrazSeznamPrikazu()
	{
		GridLayout kontajnerPrikazu = (GridLayout) this.findViewById(R.id.prikazy);
		kontajnerPrikazu.removeAllViews();
		
		for (Prikaz prikaz : seznamPrikazu)
		{
			TlacitkoPrikazu tlacitkoPrikazu = new TlacitkoPrikazu(this, prikaz);
			
			kontajnerPrikazu.addView(tlacitkoPrikazu);
		}
	}
	
	private void NastavPrijemDat()
	{
		final Context kontext = this;
		
		ZpravaPrijata uhelKompasuPrijat = new ZpravaPrijata()
		{			
			@Override
			public void ZpracujOdpoved(Odpoved odpoved)
			{
				try
				{
					((TextView) ((Activity) kontext).findViewById(R.id.kompas_mereni_hodnota)).setText(Float.parseFloat(odpoved.VratHodnotu()) % 360 + " °");
				}
				catch (Exception e)
				{
					TovarnaDialogu.Upozorneni(kontext, R.string.zprava_odpoved_chyba, false).show();
				}
			}
		};		
		ZpravaPrijata vzdalenostiSonaruPrijaty = new ZpravaPrijata()
		{			
			@Override
			public void ZpracujOdpoved(Odpoved odpoved)
			{
				try
				{
					String[] hodnotyKompasu = odpoved.VratHodnotu().split(String.valueOf(Nastaveni.ZPRAVA_ROZDELOVAC_HODNOT));
					String vzdalenosti = "[";
					
					for (int i = 0; i < hodnotyKompasu.length; i++)
					{
						vzdalenosti += hodnotyKompasu[i];
						
						if (i != hodnotyKompasu.length - 1)
						{
							vzdalenosti += ":";
						}
					}
					
					vzdalenosti += "]";
					
					((TextView) ((Activity) kontext).findViewById(R.id.sonary_mereni_hodnota)).setText(vzdalenosti);	
				}
				catch (Exception e)
				{
					TovarnaDialogu.Upozorneni(kontext, R.string.zprava_odpoved_chyba, false).show();
				}
			}
		};		
		ZpravaPrijata stavMotoruPrijat = new ZpravaPrijata()
		{	
			@Override
			public void ZpracujOdpoved(Odpoved odpoved)
			{
				String[] stavyMotoru = odpoved.VratHodnotu().split(String.valueOf(Nastaveni.ZPRAVA_ROZDELOVAC_HODNOT));
				
				String stavMotoru = "";
				
				if (stavyMotoru.length > 0)
				{
					Motor levyMotor = new Motor(stavyMotoru[0]);
					
					stavMotoru += "L: ";
					stavMotoru += levyMotor.VratStavMotoru().name();
				}
				
				if (stavyMotoru.length > 1)
				{
					Motor pravyMotor = new Motor(stavyMotoru[1]);
					
					stavMotoru += " P: ";
					stavMotoru += pravyMotor.VratStavMotoru().name();
				}		
				
				((TextView) ((Activity) kontext).findViewById(R.id.motory_mereni_hodnota)).setText(stavMotoru);	
			}
		};
		
		this.komunikator = SpravceBluetoothu.VratKomunikator();
		
		this.komunikator.ZrusUdalosti();
		/*this.komunikator.ZaregistrujUdalost(Nastaveni.ODPOVED_KOMPAS_UHEL, uhelKompasuPrijat);
		this.komunikator.ZaregistrujUdalost(Nastaveni.ODPOVED_SONARY_VZDALENOSTI, vzdalenostiSonaruPrijaty);
		this.komunikator.ZaregistrujUdalost(Nastaveni.ODPOVED_MOTORY_STAV, stavMotoruPrijat);*/
		
		this.komunikator.ZacniPrijimat();
	}
	private void NastavSignaly()
	{
		this.komunikator.ZrusSignaly();
		this.komunikator.ZaregistrujSignaly(seznamSignalu);
	}
	
	private void NastavUdalostiTlacitek()
	{
		GridLayout kontajnerPrikazu = (GridLayout) this.findViewById(R.id.prikazy);
		
		final EditText udajPrikazuPole = (EditText) this.findViewById(R.id.manualni_ovladani_prikaz_pole);
		final EditText hodnotaPrikazuPole = (EditText) this.findViewById(R.id.manualni_ovladani_hodnota_pole);
		
		final Context kontext = this; 
		
		for (int i = 0; i < kontajnerPrikazu.getChildCount(); i++)
		{
			TlacitkoPrikazu tlacitkoPrikazu = (TlacitkoPrikazu) kontajnerPrikazu.getChildAt(i);
			
			tlacitkoPrikazu.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View tlacitko)
				{
					Prikaz prikaz = ((TlacitkoPrikazu) tlacitko).VratPrikaz();
					String hodnotaPrikazu = hodnotaPrikazuPole.getText().toString();
					
					if (!komunikator.OdesliZpravu(prikaz.VratCelyPrikaz(hodnotaPrikazu)))
					{
						TovarnaDialogu.Upozorneni(kontext, R.string.odeslani_zpravy_chyba, false).show();
					}
				}
			});
		}
		
		Button odeslaniZpravy = (Button) this.findViewById(R.id.manualni_ovladani_odeslat);
		
		odeslaniZpravy.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View tlacitko)
			{
				String udajPrikazu = ((EditText) udajPrikazuPole).getText().toString();
				String hodnotaPrikazu = hodnotaPrikazuPole.getText().toString();
				
				Prikaz prikaz = new Prikaz("", udajPrikazu);
				
				if (!komunikator.OdesliZpravu(prikaz.VratCelyPrikaz(hodnotaPrikazu)))
				{
					TovarnaDialogu.Upozorneni(kontext, R.string.odeslani_zpravy_chyba, false).show();
				}
			}
		});
	}
}
 