package cz.ghrabuvka.robotak.aktivity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import cz.ghrabuvka.robotak.R;
import cz.ghrabuvka.robotak.bluetooth.Komunikator;
import cz.ghrabuvka.robotak.bluetooth.Odpoved;
import cz.ghrabuvka.robotak.bluetooth.Signal;
import cz.ghrabuvka.robotak.bluetooth.SpravceBluetoothu;
import cz.ghrabuvka.robotak.nastaveni.Nastaveni;
import cz.ghrabuvka.robotak.pomocne.Pomocnik;
import cz.ghrabuvka.robotak.udalosti.ZpravaPrijata;
import cz.ghrabuvka.robotak.ui.TovarnaDialogu;
import cz.ghrabuvka.robotak.ui.ZobrazeniSonaru;

public class DetailSonaru extends ActionBarActivity
{
	private ZobrazeniSonaru zobrazeniSonaru;
	private int[] vzdalenostiSonaru = new int[] {0, 0, 0};
	
	private Komunikator komunikator;
	private ArrayList<Signal> seznamSignalu = new ArrayList<Signal>()
	{
		private static final long serialVersionUID = -5894506857608270033L;
		
		{
			add(new Signal(Nastaveni.PRIKAZ_SONARY_ZMER, "", 2000));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_sonaru);
		
		NastavZobrazeniSonaru();
		AktualizujZobrazeniSonaru();
		
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
		
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.detail_sonaru, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem polozkaMenu)
	{
		return super.onOptionsItemSelected(polozkaMenu);
	}
	
	private void NastavZobrazeniSonaru()
	{
		this.zobrazeniSonaru = new ZobrazeniSonaru(this);
		
		((RelativeLayout) this.findViewById(R.id.zobrazeni_sonaru_kontajner)).addView(this.zobrazeniSonaru, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	private void AktualizujZobrazeniSonaru()
	{
		this.zobrazeniSonaru.NastavVzdalenostiSonaru(this.vzdalenostiSonaru);
		this.zobrazeniSonaru.invalidate();
		
		String hodnotySonaru = "[";
		
		for (int i = 0; i < this.vzdalenostiSonaru.length; i++)
		{
			hodnotySonaru += this.vzdalenostiSonaru[i];
			
			if (i != this.vzdalenostiSonaru.length - 1)
			{
				hodnotySonaru += ":";
			}
		}
		
		hodnotySonaru += "]";
		
		((TextView) this.findViewById(R.id.vzdalenosti_hodnoty)).setText(hodnotySonaru);
	}
	
	private void NastavPrijemDat()
	{
		final Context kontext = this;
		ZpravaPrijata vzdalenostiSonaruPrijaty = new ZpravaPrijata()
		{
			@Override
			public void ZpracujOdpoved(Odpoved odpoved)
			{
				try
				{
					vzdalenostiSonaru = Pomocnik.VratVzdalenostiSonaru(odpoved);
					AktualizujZobrazeniSonaru();
					
				}
				catch (Exception e)
				{
					TovarnaDialogu.Upozorneni(kontext, R.string.zprava_odpoved_chyba, false).show();
				}
			}
		};
		
		this.komunikator = SpravceBluetoothu.VratKomunikator();
		
		this.komunikator.ZrusUdalosti();
		this.komunikator.ZaregistrujUdalost(Nastaveni.ODPOVED_SONARY_VZDALENOSTI, vzdalenostiSonaruPrijaty);
		
		this.komunikator.ZacniPrijimat();
	}
	private void NastavSignaly()
	{
		this.komunikator.ZrusSignaly();
		this.komunikator.ZaregistrujSignaly(seznamSignalu);
	}
}
