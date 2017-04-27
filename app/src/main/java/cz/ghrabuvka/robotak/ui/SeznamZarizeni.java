package cz.ghrabuvka.robotak.ui;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cz.ghrabuvka.robotak.R;

public class SeznamZarizeni extends ArrayAdapter<BluetoothDevice>
{	
	private List<BluetoothDevice> seznamZarizeni;
	
	public SeznamZarizeni(Context kontext, int idTextovehoPole, ArrayList<BluetoothDevice> seznamZarizeni)
	{
		super(kontext, idTextovehoPole, seznamZarizeni);
		
		this.seznamZarizeni = seznamZarizeni;
	}
	
	@Override
	public View getView(int pozice, View prvek, ViewGroup rodic)
	{
		View v = prvek;
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inflater.inflate(R.layout.seznam_zarizeni_zaznam, null);
		
		BluetoothDevice zarizeni = seznamZarizeni.get(pozice);
		
		if (zarizeni != null)
		{
			TextView nazevZarizeni = (TextView) v.findViewById(R.id.zarizeni);	
			if (nazevZarizeni != null)
			{
				nazevZarizeni.setText(zarizeni.getName() + " [" + zarizeni.getAddress() + "]");
			}
		}
		
		return v;
	}
	
	public void PridejZarizeni(BluetoothDevice zarizeni)
	{
		if (!this.seznamZarizeni.contains(zarizeni))
		{
			this.seznamZarizeni.add(zarizeni);
			this.notifyDataSetChanged();
		}
	}
}