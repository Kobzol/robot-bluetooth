package cz.ghrabuvka.robotak.aktivity;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cz.ghrabuvka.robotak.R;
import cz.ghrabuvka.robotak.bluetooth.Komunikator;
import cz.ghrabuvka.robotak.bluetooth.Odpoved;
import cz.ghrabuvka.robotak.bluetooth.Signal;
import cz.ghrabuvka.robotak.bluetooth.SpravceBluetoothu;
import cz.ghrabuvka.robotak.nastaveni.Nastaveni;
import cz.ghrabuvka.robotak.udalosti.ZpravaPrijata;
import cz.ghrabuvka.robotak.ui.Ruzice;
import cz.ghrabuvka.robotak.ui.TovarnaDialogu;

public class DetailKompasu extends ActionBarActivity
{
	private Ruzice ruzice;
	private float uhel;
	
	private Komunikator komunikator;
	
	private ArrayList<Signal> seznamSignalu = new ArrayList<Signal>()
	{
		private static final long serialVersionUID = 9083167573043093003L;

		{
			add(new Signal(Nastaveni.PRIKAZ_KOMPAS_ZMER, "", 2000));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_detail_kompasu);
		
		NastavRuzici();
		AktualizujRuzici();
		
		NastavPrijemDat();
		NastavSignaly();
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		if (this.komunikator != null)
		{
			this.komunikator.ZrusSignaly();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.detail_kompasu, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem polozkaMenu)
	{
		return super.onOptionsItemSelected(polozkaMenu);
	}
	
	private void NastavRuzici()
	{
		Drawable ruziceObrazek = getResources().getDrawable(R.drawable.ruzice);
		 
		this.ruzice = new Ruzice(this, ruziceObrazek);
		 
		((RelativeLayout) this.findViewById(R.id.ruzice_kontajner)).addView(this.ruzice, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	private void AktualizujRuzici()
	{
		ruzice.NastavUhel(uhel);
		ruzice.invalidate();
		
		((TextView) this.findViewById(R.id.uhel_hodnota)).setText(String.valueOf((int) uhel % 360) + " °");
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
					uhel = Float.parseFloat(odpoved.VratHodnotu());
					AktualizujRuzici();	
				}
				catch (Exception e)
				{
					TovarnaDialogu.Upozorneni(kontext, R.string.zprava_odpoved_chyba, false).show();
				}
			}
		};
		
		this.komunikator = SpravceBluetoothu.VratKomunikator();
		
		this.komunikator.ZrusUdalosti();
		this.komunikator.ZaregistrujUdalost(Nastaveni.ODPOVED_KOMPAS_UHEL, uhelKompasuPrijat);
		
		this.komunikator.ZacniPrijimat();
	}
	private void NastavSignaly()
	{
		this.komunikator.ZrusSignaly();
		this.komunikator.ZaregistrujSignaly(seznamSignalu);
	}
}
