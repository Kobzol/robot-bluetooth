package cz.ghrabuvka.robotak.bluetooth;

import java.io.Serializable;

import cz.ghrabuvka.robotak.nastaveni.Nastaveni;

public class Prikaz implements Serializable
{
	private static final long serialVersionUID = 6850638270740681546L;
	
	private static char rozdelovac = Nastaveni.ZPRAVA_ROZDELOVAC_ZPRAVY;
	private static char prazdnaHodnota = Nastaveni.ZPRAVA_PRAZDNY_ZNAK;
	
	private String popis;	
	private String prikaz;
	private String hodnota;

	public Prikaz()
	{
		this("Příkaz", "", "");
	}
	public Prikaz(String popis, String prikaz)
	{
		this(popis, prikaz, "");
	}
	public Prikaz(String popis, String prikaz, String hodnota)
	{
		this.popis = popis;
		this.prikaz = prikaz;
		this.hodnota = hodnota;
	}
	
	public String VratPopis()
	{
		return this.popis;
	}
	public String VratPrikaz()
	{
		return this.prikaz;
	}

	public String VratCelyPrikaz()
	{
		return this.VratCelyPrikaz(this.hodnota);
	}
	public String VratCelyPrikaz(String hodnota)
	{
		if (hodnota.equals(""))
		{
			hodnota = String.valueOf(prazdnaHodnota);
		}
		
		return this.prikaz + Prikaz.rozdelovac + hodnota;
	}
}
