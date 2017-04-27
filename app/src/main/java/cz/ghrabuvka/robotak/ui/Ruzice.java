package cz.ghrabuvka.robotak.ui;

import cz.ghrabuvka.robotak.pomocne.Pomocnik;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

public class Ruzice extends View
{
	private Drawable ruzice;
	
	private Paint malba;
	private final Point stred = new Point();
	
	private int ruziceSirka;
	private int ruziceVyska;
	
	private float skalovac;
	private float uhel = 80;
	
	public Ruzice(Context kontext, Drawable ruzice)
	{
		super(kontext);
		
		this.ruzice = ruzice;
		
		skalovac = Pomocnik.VratTransformovanePixely(kontext, 1.0f, TypedValue.COMPLEX_UNIT_DIP);
		
		ruziceSirka = (int) Pomocnik.VratSkalovanePixely(ruzice.getMinimumWidth(), skalovac);
	    ruziceVyska = (int) Pomocnik.VratSkalovanePixely(ruzice.getMinimumHeight(), skalovac);
	    
	    malba = new Paint();
	    
	    malba.setColor(Color.RED);
	    malba.setStyle(Style.FILL_AND_STROKE);
	    malba.setStrokeCap(Cap.ROUND);
	    malba.setStrokeJoin(Join.ROUND);
	    malba.setStrokeWidth(1.0f);
	    malba.setFlags(Paint.ANTI_ALIAS_FLAG);
	}
	
	@Override
	protected void onDraw(Canvas platno)
	{
		this.stred.x = this.getWidth() / 2;
		this.stred.y = this.getHeight() / 2;

		this.ruzice.setBounds(this.stred.x - (this.ruziceSirka / 2), this.stred.y - (this.ruziceVyska / 2), this.stred.x + (this.ruziceSirka / 2), this.stred.y + (this.ruziceVyska / 2));
		
		platno.rotate(this.uhel, this.stred.x, this.stred.y);
		this.ruzice.draw(platno);
	}
	
	public void NastavUhel(float uhel)
	{
		this.uhel = uhel;
	}
}
