package cz.ghrabuvka.robotak.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import cz.ghrabuvka.robotak.R;
import cz.ghrabuvka.robotak.bluetooth.Prikaz;
import cz.ghrabuvka.robotak.bluetooth.SpravceBluetoothu;

public class TovarnaDialogu
{
	private TovarnaDialogu()
	{

	}
	
	public static Dialog ZapnutiBluetoothu(final Context kontext)
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(kontext);
		
		dialog.setTitle(kontext.getString(R.string.dialog_bluetooth_titulek));
		dialog.setMessage(kontext.getString(R.string.dialog_bluetooth_text));
		
		dialog.setPositiveButton(kontext.getString(R.string.dialog_bluetooth_zapnout), new OnClickListener()
		{	
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				SpravceBluetoothu.ZapniBluetooth(kontext);
			}
		});		
		dialog.setNegativeButton(kontext.getString(R.string.dialog_bluetooth_zrusit), new OnClickListener()
		{	
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		});
		
		
		return dialog.create();
	}

	public static ProgressDialog Nacitani(final Context kontext, String zprava, boolean lzeZrusit)
	{
		ProgressDialog dialog = new ProgressDialog(kontext);
		
		dialog.setIndeterminate(true);
		dialog.setCanceledOnTouchOutside(lzeZrusit);
		
		dialog.setTitle(kontext.getString(R.string.dialog_nacitani_titulek));
		dialog.setMessage(zprava);	
		
		return dialog;
	}
	
	public static Dialog SeznamPrikazu(final Context kontext, final ArrayList<Prikaz> seznamPrikazu)
	{
		Dialog dialog = new Dialog(kontext);
		
		dialog.setTitle(R.string.prikazy_menu_upravit_prikazy);
		dialog.setContentView(R.layout.seznam_prikazu_dialog);
		dialog.setCanceledOnTouchOutside(true);
		
		final SeznamPrikazu seznamPrikazuAdapter = new SeznamPrikazu(kontext, R.layout.seznam_prikazu_zaznam, seznamPrikazu);
		
		ListView seznamPrikazuZobrazeni = (ListView) dialog.findViewById(R.id.seznam_prikazu);
		seznamPrikazuZobrazeni.setAdapter(seznamPrikazuAdapter);
		
		Button pridaniZarizeni = (Button) dialog.findViewById(R.id.pridat_prikaz);
		pridaniZarizeni.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View tlacitko)
			{
				seznamPrikazuAdapter.PridejPrikaz(new Prikaz("test", "test příkazu"));
			}
		});
		
		seznamPrikazuAdapter.notifyDataSetChanged();
		
		return dialog;
	}

	public static Dialog UpraveniPrikazu(final SeznamPrikazu seznamPrikazuAdapter, final int pozice)
	{
		final ArrayList<Prikaz> seznamPrikazu = seznamPrikazuAdapter.VratSeznamPrikazu();
		final Context kontext = seznamPrikazuAdapter.getContext();
		
		AlertDialog.Builder stavecDialogu = new AlertDialog.Builder(kontext);
		
		stavecDialogu.setTitle(R.string.dialog_uprava_prikazu);
		
		stavecDialogu.setNeutralButton(R.string.dialog_uprava_prikazu_zpet, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int tlacitko)
			{
				dialog.cancel();
			}
		});	
		stavecDialogu.setNegativeButton(R.string.dialog_uprava_prikazu_smazat, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int tlacitko)
			{
				seznamPrikazuAdapter.OdeberPrikaz(pozice);
				dialog.cancel();
			}
		});		
		stavecDialogu.setPositiveButton(R.string.dialog_uprava_prikazu_ulozit, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int tlacitko)
			{
				Dialog dialogoveOkno = (Dialog) dialog;
				
				String nazevPrikazu = ((EditText) dialogoveOkno.findViewById(R.id.prikaz_nazev_pole)).getText().toString();
				String udajPrikazu = ((EditText) dialogoveOkno.findViewById(R.id.prikaz_prikaz_pole)).getText().toString();
				
				seznamPrikazuAdapter.ZmenPrikaz(new Prikaz(nazevPrikazu, udajPrikazu), pozice);
				
				dialogoveOkno.cancel();
			}
		});
		
		LayoutInflater design = ((Activity) kontext).getLayoutInflater();
		
		View zobrazeni = design.inflate(R.layout.uprava_prikazu_dialog, null);
		
		EditText nazevPrikazu = (EditText) zobrazeni.findViewById(R.id.prikaz_nazev_pole);
		nazevPrikazu.setText(seznamPrikazu.get(pozice).VratPopis());
		
		EditText udajPrikazu = (EditText) zobrazeni.findViewById(R.id.prikaz_prikaz_pole);
		udajPrikazu.setText(seznamPrikazu.get(pozice).VratPrikaz());
		
		stavecDialogu.setView(zobrazeni);
		
		return stavecDialogu.create();
	}
	
	public static Toast Upozorneni(final Context kontext, String zprava, boolean dlouheUpozorneni)
	{
		int delka = Toast.LENGTH_SHORT;
		
		if (dlouheUpozorneni)
		{
			delka = Toast.LENGTH_SHORT;
		}
		
		return Toast.makeText(kontext, zprava, delka);
	}	
	public static Toast Upozorneni(final Context kontext, int zpravaID, boolean dlouheUpozorneni)
	{
		return TovarnaDialogu.Upozorneni(kontext, kontext.getString(zpravaID), dlouheUpozorneni);
	}
}
