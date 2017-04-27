package cz.ghrabuvka.robotak.aktivity;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cz.ghrabuvka.robotak.R;
import cz.ghrabuvka.robotak.bluetooth.SpravceBluetoothu;
import cz.ghrabuvka.robotak.udalosti.ZarizeniNalezeno;
import cz.ghrabuvka.robotak.udalosti.ZarizeniPripojeno;
import cz.ghrabuvka.robotak.ui.SeznamZarizeni;
import cz.ghrabuvka.robotak.ui.TovarnaDialogu;

public class VyberZarizeni extends ActionBarActivity
{
	private SeznamZarizeni seznamZarizeniAdapter;
	private ListView seznamZarizeniZobrazeni;
	
	private BroadcastReceiver stavBluetoothu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_vyber_zarizeni);
		
		NastavSeznamZarizeni();
		NastavPrijimacZmenyStavu();
		
		AktualizujStavBluetoothu();
		
		ZacniVyhledavatZarizeni(null);
	}	
	@Override
	protected void onDestroy()
	{
		if (this.stavBluetoothu != null)
		{
			this.unregisterReceiver(this.stavBluetoothu);
			this.stavBluetoothu = null;
		}
		
		SpravceBluetoothu.UvolniProstredky(this);
		
		super.onDestroy();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
        getMenuInflater().inflate(R.menu.vyber_zarizeni, menu);
        
        return true;
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem polozkaMenu)
	{        	
    	if (polozkaMenu.getItemId() == R.id.menu_bluetooth_zapnout)
    	{
    		if (!SpravceBluetoothu.JeZapnutBluetooth())
    		{
    			SpravceBluetoothu.ZapniBluetooth(this);
    		}
    	}
           
    	return super.onOptionsItemSelected(polozkaMenu);      
    }
	
	private void NastavSeznamZarizeni()
	{
		this.seznamZarizeniAdapter = new SeznamZarizeni(this, R.id.zarizeni, new ArrayList<BluetoothDevice>());
		
		this.seznamZarizeniZobrazeni = (ListView) findViewById(R.id.seznam_zarizeni);		
		this.seznamZarizeniZobrazeni.setAdapter(this.seznamZarizeniAdapter);
		
		this.seznamZarizeniZobrazeni.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View prvek, int pozice, long id)
			{
				PokusSePripojitKZarizeni((BluetoothDevice) parent.getItemAtPosition(pozice));
			}
		});
		
		this.seznamZarizeniAdapter.notifyDataSetChanged();
	}
	private void NastavPrijimacZmenyStavu()
	{
		stavBluetoothu = new BroadcastReceiver()
		{
    	    public void onReceive(Context kontext, Intent zamer)
    	    {
    	        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(zamer.getAction()))
    	        {
    	        	AktualizujStavBluetoothu();
    	        }
    	    }
    	};
    	
    	this.registerReceiver(stavBluetoothu, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
	}
	
	private void AktualizujStavBluetoothu()
	{
		TextView zobrazovacStavu = (TextView) findViewById(R.id.stav_bluetoothu);
		
		zobrazovacStavu.setText(getString(R.string.vyhledani_stav_bluetoothu) + ": ");
    	
        if (SpravceBluetoothu.JeZapnutBluetooth())
        {
        	zobrazovacStavu.append(getString(R.string.vyhledani_stav_bluetoothu_zapnut));
        }
        else 
        {
        	zobrazovacStavu.append(getString(R.string.vyhledani_stav_bluetoothu_vypnut));
        }
	}
	
	public void ZacniVyhledavatZarizeni(View tlacitko)
	{
		if (!SpravceBluetoothu.JeZapnutBluetooth())
		{
			TovarnaDialogu.ZapnutiBluetoothu(this).show();
		}
		else if (!SpravceBluetoothu.ProbihaVyhledavani())
		{
			ZarizeniNalezeno zobrazovacZarizeni = new ZarizeniNalezeno()
			{		

				@Override
				public void ZpracujNoveZarizeni(BluetoothDevice zarizeni)
				{
					seznamZarizeniAdapter.PridejZarizeni(zarizeni);
				}
			};
			
			SpravceBluetoothu.ZacniVyhledavatZarizeni(this, zobrazovacZarizeni);
		}
	}
	private void PokusSePripojitKZarizeni(final BluetoothDevice zarizeni)
	{
		final ProgressDialog nacitani = TovarnaDialogu.Nacitani(this, getString(R.string.dialog_nacitani_bluetooth_pripojeni), false);
		
		nacitani.show();
		
		final Context kontext = this;
		
		ZarizeniPripojeno pripojeni = new ZarizeniPripojeno()
		{
			@Override
			public void PripojeniKZarizeniUspesne()
			{
				nacitani.cancel();
				TovarnaDialogu.Upozorneni(kontext, R.string.pripojeni_zarizeni_uspech, true).show();
				
				kontext.startActivity(new Intent(kontext, OvladaniZarizeni.class));
			}

			@Override
			public void PripojeniKZarizeniNeuspesne()
			{
				nacitani.cancel();
				TovarnaDialogu.Upozorneni(kontext, R.string.pripojeni_zarizeni_chyba, true).show();
				
			}
		};
		
		SpravceBluetoothu.PripojSeKZarizeni(zarizeni, pripojeni);		
	}
}
