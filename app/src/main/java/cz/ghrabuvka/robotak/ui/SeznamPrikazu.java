package cz.ghrabuvka.robotak.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import cz.ghrabuvka.robotak.R;
import cz.ghrabuvka.robotak.bluetooth.Prikaz;
import cz.ghrabuvka.robotak.nastaveni.Nastaveni;

public class SeznamPrikazu extends ArrayAdapter<Prikaz>
{	
	private ArrayList<Prikaz> seznamPrikazu;
	
	public SeznamPrikazu(Context kontext, int idTextovehoPole, ArrayList<Prikaz> seznamPrikazu)
	{
		super(kontext, idTextovehoPole, seznamPrikazu);
		
		this.seznamPrikazu = seznamPrikazu;
	}
	
	public ArrayList<Prikaz> VratSeznamPrikazu()
	{
		return this.seznamPrikazu;
	}
	
	@Override
	public View getView(final int pozice, View prvek, ViewGroup rodic)
	{
		View v = prvek;
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inflater.inflate(R.layout.seznam_prikazu_zaznam, null);
		
		Prikaz prikaz = seznamPrikazu.get(pozice);
		
		if (prikaz != null)
		{
			TextView nazevPrikazu = (TextView) v.findViewById(R.id.prikaz_info);	
			if (nazevPrikazu != null)
			{
				nazevPrikazu.setText(prikaz.VratPopis() + " (" + prikaz.VratPrikaz() + ")");
			}
			
			final SeznamPrikazu adapter = this;
			Button upraveniPrikazu = (Button) v.findViewById(R.id.prikaz_upravit);	
			if (upraveniPrikazu != null)
			{
				upraveniPrikazu.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View tlacitko)
					{
						TovarnaDialogu.UpraveniPrikazu(adapter, pozice).show();
					}
				});
			}
		}
		
		return v;
	}
	
	private void AktualizujUdaje()
	{
		this.notifyDataSetChanged();
		Nastaveni.UlozData(this.getContext(), seznamPrikazu, Nastaveni.SOUBOR_PRIKAZY);
	}
	
	public void PridejPrikaz(Prikaz prikaz)
	{
		if (!this.seznamPrikazu.contains(prikaz))
		{
			this.seznamPrikazu.add(prikaz);
			AktualizujUdaje();
		}
	}
	public void OdeberPrikaz(int pozice) 
	{
		this.seznamPrikazu.remove(pozice);
		AktualizujUdaje();
	}
	public void ZmenPrikaz(Prikaz novyPrikaz, int pozice)
	{
		this.seznamPrikazu.set(pozice, novyPrikaz);
		AktualizujUdaje();
	}
}