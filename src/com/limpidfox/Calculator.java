package com.limpidfox;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import java.util.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;

public class Calculator extends Activity implements IDisplayUpdateHandler 
{
	
	Handler mHandler = new Handler();
	List<String> _btnNames;
	HP67 _hp;
	RelativeLayout _vMain;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initButtonNames();
        _hp = new HP67();
        _hp.setDisplayHandler(this);
        _vMain = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.main, null);
        setContentView(_vMain);
    	ImageView card = (ImageView) findViewById(R.id.card);
    	card.setAlpha(0);
        TextView display = (TextView) findViewById(R.id.display);
        display.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HP97R.ttf"));
        setButtonListeners();
    }
    
    public void btnPressed(View b)
    {
    }
    
    public void btnClicked(View b)
    {
    	int id = b.getId();
    	if (id==R.id.btnPgm)
    	{
    		handleProgramButton();
    	}
    	else
    	{
        	processKeyInput(id);
    	}
        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(40);
    	setDisplay(_hp.GetDisplay());
    }
    
    public void processKeyInput(int key)
    {
       	_hp.ProcessKeyInput2(getBtnName(key));
    	setDisplay(_hp.GetDisplay());
    }
    
    public void handleProgramButton()
    {
    	ImageView card = (ImageView) findViewById(R.id.card);
		_hp.SetRunMode(!_hp.GetRunMode());
		if (!_hp.GetRunMode())
		{
			card.setAlpha(255);
		}
		else
		{
	    	if (_hp.GetInnerHP97().GetProgramLenth()>0)
	    	{
	    		card.setAlpha(255);
	    	}
	    	else
	    	{
	    		card.setAlpha(0);
	    	}
		}
    }
    
    public String getBtnName(int key)
    {
    	int offset = key - R.id.btn11;
    	return _btnNames.get(offset);
    }
    
    public void setDisplay(String txt)
    {
        TextView display = (TextView) this.findViewById(R.id.display);
		ImageView card = (ImageView) findViewById(R.id.card);
        display.setText(txt);    	
    }

	@Override
	public void DisplayUpdateHandler(Object sender, final DisplayEventArgs e) {
		mHandler.post(new Runnable(){
			public void run()
			{
				setDisplay(_hp.GetDisplay());
			}
		});
		// TODO Auto-generated method stub
		
	}

	private void setButtonListeners()
	{
        for (int i=0;i<_vMain.getChildCount();i++)
        {
        	if (_vMain.getChildAt(i) instanceof Button)
        	{
        		((Button)_vMain.getChildAt(i)).setOnTouchListener(
        				new OnTouchListener(){
        					@Override
        					public boolean onTouch(View v, MotionEvent event)
        					{
        						if (event.getAction()==MotionEvent.ACTION_DOWN)
        						{
       							    btnClicked(v);
       							    return false;
        						}
        						return false;
        					}
        				});
        	}
        }
	}
	
	private void initButtonNames()
	{
        _btnNames = new ArrayList<String>(35);
        _btnNames.add("btn11");
        _btnNames.add("btn12");
        _btnNames.add("btn13");
        _btnNames.add("btn14");
        _btnNames.add("btn15");
        _btnNames.add("btn21");
        _btnNames.add("btn22");
        _btnNames.add("btn23");
        _btnNames.add("btn24");
        _btnNames.add("btn25");
        _btnNames.add("btn31");
        _btnNames.add("btn32");
        _btnNames.add("btn33");
        _btnNames.add("btn34");
        _btnNames.add("btn35");
        _btnNames.add("btn41");
        _btnNames.add("btn42");
        _btnNames.add("btn43");
        _btnNames.add("btn44");
        _btnNames.add("btn51");
        _btnNames.add("btn52");
        _btnNames.add("btn53");
        _btnNames.add("btn54");
        _btnNames.add("btn61");
        _btnNames.add("btn62");
        _btnNames.add("btn63");
        _btnNames.add("btn64");
        _btnNames.add("btn71");
        _btnNames.add("btn72");
        _btnNames.add("btn73");
        _btnNames.add("btn74");
        _btnNames.add("btn81");
        _btnNames.add("btn82");
        _btnNames.add("btn83");
        _btnNames.add("btn84");
	}
}
