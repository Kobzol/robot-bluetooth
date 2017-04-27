package cz.ghrabuvka.robotak.ui;

import cz.ghrabuvka.robotak.pomocne.Pomocnik;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.View;

public class ZobrazeniSonaru extends View
{
	private int[] vzdalenostiSonaru;
	
	private Paint malbaSonaru;
	private Paint malbaTextu;
	private Point stred = new Point();
	
	private int sirkaSonaru = 100;
	private int delkaSonaru = 500;
	
	private int mezera = 50;
	
	public ZobrazeniSonaru(Context kontext)
	{
		super(kontext);
		
		this.malbaSonaru = new Paint();
		this.malbaSonaru.setStyle(Style.FILL);
		
		this.malbaTextu = new Paint();
		this.malbaTextu.setColor(Color.BLACK);
		this.malbaTextu.setTextSize(Pomocnik.VratTransformovanePixely(kontext, 15.0f, TypedValue.COMPLEX_UNIT_SP));
		
		int skalovac = (int) Pomocnik.VratTransformovanePixely(kontext, 1.0f, TypedValue.COMPLEX_UNIT_DIP);
		
		this.sirkaSonaru = this.sirkaSonaru / skalovac;
		this.delkaSonaru = this.delkaSonaru / skalovac;
		this.mezera = this.mezera / skalovac;
	}
	
	public void NastavVzdalenostiSonaru(int[] vzdalenostiSonaru)
	{
		this.vzdalenostiSonaru = vzdalenostiSonaru;
	}
	
	@Override
	protected void onDraw(Canvas platno)
	{
		this.stred.x = platno.getWidth() / 2;
		this.stred.y = platno.getHeight() / 2;
		
		// levý sonar
		this.malbaSonaru.setColor(Pomocnik.VratUrovenBarvyPodleVzdalenosti(this.vzdalenostiSonaru[0]));
		platno.drawRect(mezera, stred.y - (delkaSonaru / 2), mezera + sirkaSonaru, stred.y + (delkaSonaru / 2), this.malbaSonaru);
		platno.drawText(String.valueOf(this.vzdalenostiSonaru[0]), mezera + sirkaSonaru + mezera, stred.y, this.malbaTextu);
		
		// přední sonar
		this.malbaSonaru.setColor(Pomocnik.VratUrovenBarvyPodleVzdalenosti(this.vzdalenostiSonaru[1]));
		platno.drawRect(stred.x - (delkaSonaru / 2), mezera, stred.x + (delkaSonaru / 2), mezera + sirkaSonaru, this.malbaSonaru);
		platno.drawText(String.valueOf(this.vzdalenostiSonaru[1]), stred.x - (this.malbaTextu.measureText(String.valueOf(this.vzdalenostiSonaru[1])) / 2), mezera + sirkaSonaru + mezera, this.malbaTextu);
		
		// pravý sonar
		this.malbaSonaru.setColor(Pomocnik.VratUrovenBarvyPodleVzdalenosti(this.vzdalenostiSonaru[2]));
		platno.drawRect(platno.getWidth() - mezera - sirkaSonaru, stred.y - (delkaSonaru / 2), platno.getWidth() - mezera, stred.y + (delkaSonaru / 2), this.malbaSonaru);
		platno.drawText(String.valueOf(this.vzdalenostiSonaru[2]), platno.getWidth() - mezera - sirkaSonaru - mezera - this.malbaTextu.measureText(String.valueOf(this.vzdalenostiSonaru[2])), stred.y, this.malbaTextu);
	}
}
