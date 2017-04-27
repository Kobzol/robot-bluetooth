package cz.ghrabuvka.robotak.robot;

import java.util.HashMap;

public class Motor
{
	public enum StavMotoru
	{
		 Stoji
		,JedeDopredu
		,JedeDozadu
		,Nerozpoznano
	}
	
	private StavMotoru stavMotoru;
	
	private static final HashMap<String, StavMotoru> stavyMotoru = new HashMap<String, StavMotoru>()
	{
		private static final long serialVersionUID = 3500813843421288320L;

		{
			put("00", StavMotoru.Stoji);
			put("10", StavMotoru.JedeDopredu);
			put("01", StavMotoru.JedeDozadu);
			put("11", StavMotoru.Stoji);
		}
	};
	
	public Motor(String stavMotoru)
	{
		if (Motor.stavyMotoru.containsKey(stavMotoru))
		{
			this.stavMotoru = Motor.stavyMotoru.get(stavMotoru);
		}
		else this.stavMotoru = Motor.StavMotoru.Nerozpoznano;
	}
	
	public StavMotoru VratStavMotoru()
	{
		return this.stavMotoru;
	}
}
