package cz.ghrabuvka.robotak.ui;

import android.content.Context;
import android.widget.Button;
import cz.ghrabuvka.robotak.bluetooth.Prikaz;

public class TlacitkoPrikazu extends Button
{
	private Prikaz prikaz;
	
	public TlacitkoPrikazu(Context kontext, Prikaz prikaz)
	{
		super(kontext);
		
		this.prikaz = prikaz;
		this.setText(prikaz.VratPopis());
	}

	public Prikaz VratPrikaz()
	{
		return prikaz;
	}
}
