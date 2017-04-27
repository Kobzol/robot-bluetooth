package cz.ghrabuvka.robotak.pomocne;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import cz.ghrabuvka.robotak.bluetooth.Odpoved;
import cz.ghrabuvka.robotak.nastaveni.Nastaveni;

public class Pomocnik
{
	private Pomocnik()
	{
		
	}
	
	public static float VratTransformovanePixely(Context kontext, float pixely, int typPixelu)
	{
		return TypedValue.applyDimension(typPixelu, pixely, kontext.getResources().getDisplayMetrics());
	}
	
	public static float VratSkalovanePixely(float pixely, float skalovac)
	{
		return pixely / skalovac;
	}
	
	public static int VratUrovenBarvyPodleVzdalenosti(int vzdalenost)
	{
		float[] hsvCervena = new float[3];	// [0] je 0
		Color.colorToHSV(Color.RED, hsvCervena);
		
		float[] hsvZelena = new float[3];	// [0] je 120	
		Color.colorToHSV(Color.GREEN, hsvZelena);

		float uroven = ((vzdalenost % (Nastaveni.MAXIMALNI_VZDALENOST_SONARU + 1)) / (float) Nastaveni.MAXIMALNI_VZDALENOST_SONARU);
		
		float rozdil = hsvZelena[0] - hsvCervena[0];
		
		hsvZelena[0] = hsvCervena[0] + (rozdil * uroven);		
		
		return Color.HSVToColor(hsvZelena);
	}
	
	public static int[] VratVzdalenostiSonaru(Odpoved odpoved)
	{
		String[] vzdalenostiRetezce = odpoved.VratHodnotu().split(String.valueOf(Nastaveni.ZPRAVA_ROZDELOVAC_HODNOT));
		
		int[] vzdalenosti = new int[vzdalenostiRetezce.length];
		
		for (int i = 0; i < vzdalenostiRetezce.length; i++)
		{
			try
			{
				vzdalenosti[i] = Integer.parseInt(vzdalenostiRetezce[i]);
			}
			catch (Exception e)
			{
				vzdalenosti[i] = 0;
			}
		}
		
		return vzdalenosti;
	}
}
