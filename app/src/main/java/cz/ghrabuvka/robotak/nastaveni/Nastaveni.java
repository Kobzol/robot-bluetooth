package cz.ghrabuvka.robotak.nastaveni;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public final class Nastaveni
{
	public static final String SOUBOR_PRIKAZY = "prikazy.bin";
	
	public static final char ZPRAVA_TERMINACNI_ZNAK = '\0';
	public static final char ZPRAVA_ROZDELOVAC_ZPRAVY = ';';
	public static final char ZPRAVA_ROZDELOVAC_HODNOT = '-';
	public static final char ZPRAVA_PRAZDNY_ZNAK = 255;
	
	public static final char ZPRAVA_PRIJEM_TERMINACNI_ZNAK = '\0';	
	public static final long POCET_MILISEKUND_MEZI_ZPRAVAMI = 50;
	
	public static final String ODPOVED_KOMPAS_UHEL = "a";
	public static final String ODPOVED_SONARY_VZDALENOSTI = "b";
	public static final String ODPOVED_MOTORY_STAV = "c";
	
	public static final String PRIKAZ_KOMPAS_ZMER = "kompasZmer";
	public static final String PRIKAZ_SONARY_ZMER = "sonaryZmer";
	public static final String PRIKAZ_MOTORY_STAV = "motoryStav";
	
	public static final int MAXIMALNI_VZDALENOST_SONARU = 500;
	
	private Nastaveni()
	{

	}
	
	public static void UlozData(Context context, List<?> seznamObjektu, String nazevSouboru)
	{
		FileOutputStream fos;
		try
		{
			fos = context.openFileOutput(nazevSouboru, android.content.Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < seznamObjektu.size(); i++)
			{
				oos.writeObject(seznamObjektu.get(i));
			}
			oos.close();
			fos.close();
		
		} 
		catch (Exception e)
		{
			
		}
	}
	
	public static List<?> NactiData(Context kontext, String nazevSouboru)
	{
		List<Object> seznamObjektu = new ArrayList<Object>();
		
		FileInputStream fis = null;
    	ObjectInputStream ois = null;
		try 
		{			
			fis = kontext.openFileInput(nazevSouboru);			
			ois = new ObjectInputStream(fis);
			
			while (true)
			{	
				seznamObjektu.add(ois.readObject());
			}
		}
		catch (Exception e)
		{
			
		}
		finally 
		{
			try
			{
				ois.close();
				fis.close();
			}
			catch (Exception e)
			{
				
			}
		}				
		
		return seznamObjektu;
	}
}
