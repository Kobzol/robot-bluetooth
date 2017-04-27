package cz.ghrabuvka.robotak.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import cz.ghrabuvka.robotak.nastaveni.Nastaveni;
import cz.ghrabuvka.robotak.udalosti.ZarizeniNalezeno;
import cz.ghrabuvka.robotak.udalosti.ZarizeniPripojeno;

public final class SpravceBluetoothu
{
	private final static BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	private final static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private static BluetoothSocket spojeni;
	private static Komunikator komunikator;
	
	private static BroadcastReceiver vyhledavac;
	
	private SpravceBluetoothu()
	{
		
	}

	public static boolean JeZapnutBluetooth()
	{
		if (adapter != null && adapter.isEnabled())
		{
			return true;
		}
		else return false;
	}
	public static boolean JePripojenoZarizeni()
	{
		if (spojeni != null && spojeni.isConnected())
		{
			return true;
		}
		else return false;
	}
	public static boolean ProbihaVyhledavani()
	{
		if (adapter != null && adapter.isDiscovering())
		{
			return true;
		}
		else return false;
	}
	
	public static BluetoothDevice VratPripojeneZarizeni()
	{
		if (SpravceBluetoothu.JePripojenoZarizeni())
		{
			return spojeni.getRemoteDevice();
		}
		else return null;
	}
	
	public static void ZapniBluetooth(Context kontext)
	{
		if (adapter != null && !adapter.isEnabled())
		{
	    	((Activity) kontext).startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
		}	
	}	
	
	public static void ZacniVyhledavatZarizeni(Context kontext, final ZarizeniNalezeno poNalezeni)
	{
		if (SpravceBluetoothu.ProbihaVyhledavani())
		{
			adapter.cancelDiscovery();
		}
		
		adapter.startDiscovery();
    	
		if (vyhledavac == null)
		{
			vyhledavac = new BroadcastReceiver()
	    	{
	    	    public void onReceive(Context kontext, Intent zamer)
	    	    {
	    	        if (BluetoothDevice.ACTION_FOUND.equals(zamer.getAction()))
	    	        {
	    	        	poNalezeni.ZpracujNoveZarizeni((BluetoothDevice) zamer.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
	    	        }
	    	    }
	    	};
	    	
	    	kontext.registerReceiver(vyhledavac, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		}
	}
	public static void PrestanVyhledavatZarizeni()
	{
		adapter.cancelDiscovery();
	}
	
	public static void PripojSeKZarizeni(final BluetoothDevice zarizeni, final ZarizeniPripojeno poPripojeni)
	{
		if (SpravceBluetoothu.JeZapnutBluetooth())
		{
			if (SpravceBluetoothu.ProbihaVyhledavani())
			{
				SpravceBluetoothu.PrestanVyhledavatZarizeni();
			}
			
			if (SpravceBluetoothu.JePripojenoZarizeni() && spojeni.getRemoteDevice().equals(zarizeni))
			{
				poPripojeni.PripojeniKZarizeniUspesne();
			}
			else
			{
				SpravceBluetoothu.OdpojSeOdZarizeni();
				
				BluetoothDevice ciloveZarizeni = adapter.getRemoteDevice(zarizeni.getAddress());
				try
				{
					spojeni = ciloveZarizeni.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
					
					new PripojovacKZarizeni().execute(spojeni, poPripojeni);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					poPripojeni.PripojeniKZarizeniNeuspesne();
				}
			}
			
		}
	}
	
	public static Komunikator VratKomunikator()
	{
		if (komunikator == null)
		{
			komunikator = new Komunikator(spojeni, Nastaveni.ZPRAVA_TERMINACNI_ZNAK);
		}
		
		return komunikator;
	}
	
	public static void OdpojSeOdZarizeni()
	{
		if (JePripojenoZarizeni())
		{
			try
			{
				if (spojeni.getInputStream() != null)
				{
					try { spojeni.getInputStream().close(); }
					catch (Exception e) {}
				}
				
				if (spojeni.getOutputStream() != null)
				{
					try { spojeni.getOutputStream().close(); }
					catch (Exception e) {}
				}
				
				spojeni.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				spojeni = null;
				komunikator = null;
			}
		}
	}
	public static void UvolniProstredky(Context kontext)
	{
		if (vyhledavac != null)
		{
			kontext.unregisterReceiver(vyhledavac);
			vyhledavac = null;
		}
		
		if (komunikator != null)
		{
			komunikator.ZrusUdalosti();
			komunikator.PrestanPrijimat();
		}
		
		OdpojSeOdZarizeni();
		
		if (ProbihaVyhledavani())
		{
			PrestanVyhledavatZarizeni();
		}
	}
}
