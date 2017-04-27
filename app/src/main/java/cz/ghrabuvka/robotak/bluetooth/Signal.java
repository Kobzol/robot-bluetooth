package cz.ghrabuvka.robotak.bluetooth;

public class Signal extends Prikaz
{
	private static final long serialVersionUID = 5992621545714820760L;
	
	private int perioda;
	
	public Signal(String prikaz, String hodnota, int perioda)
	{
		super("", prikaz, hodnota);
		this.perioda = perioda;
	}
	
	public int VratPeriodu()
	{
		return this.perioda;
	}
}
