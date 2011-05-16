package com.limpidfox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import java.util.*;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import java.io.*;

import android.content.res.*;

public class Calculator extends Activity implements IDisplayUpdateHandler, IWriteDataHandler 
{
    private static final int ACTIVITY_PGMLIST=0;
    private static final int ACTIVITY_PGMSAVE=1;
    private static final int ACTIVITY_DATASAVE=2;
    private static final int LOAD_PGM_ID = Menu.FIRST;
    private static final int SAVE_PGM_ID = Menu.FIRST + 1;
    private static final int SAVE_DATA_ID = Menu.FIRST + 2;
    public static final String HPDIR = "/HP Programs";
    public static final int ACTION_SELECTED=1;
    public static final int ACTION_SAVE=2;
    
	 Handler mHandler = new Handler();
	List<String> _btnNames;
	HP67 _hp;
	RelativeLayout _vMain;
	String _pgmfile;
	String _datafile;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initButtonNames();
        _hp = new HP67();
        _hp.setDisplayHandler(this);
        _hp.setWriteDataHandler(this);
        _vMain = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.main, null);
        setContentView(_vMain);
    	ImageView card = (ImageView) findViewById(R.id.card);
    	card.setAlpha(0);
        TextView display = (TextView) findViewById(R.id.display);
        display.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HP97R.ttf"));
        setButtonListeners();
        unpackPrograms();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, LOAD_PGM_ID, 0, R.string.menu_load);
        menu.add(0, SAVE_PGM_ID, 0, R.string.menu_save);
        menu.add(0, SAVE_DATA_ID, 0, R.string.menu_savedata);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case LOAD_PGM_ID:
                loadProgram();
                return true;
            case SAVE_PGM_ID:
                saveProgram();
                return true;
            case SAVE_DATA_ID:
                saveData();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private boolean isDataCardAvailable()
    {
    	boolean mExternalStorageAvailable = false;
    	boolean mExternalStorageWriteable = false;
    	String state = Environment.getExternalStorageState();
    	if (Environment.MEDIA_MOUNTED.equals(state)) 
    	{    // We can read and write the media    
    		mExternalStorageAvailable = mExternalStorageWriteable = true;
    	} 
    	else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
    	{    // We can only read the media    
    		mExternalStorageAvailable = true;    
    		mExternalStorageWriteable = false;
    	} 
    	else 
    	{
    		mExternalStorageAvailable = mExternalStorageWriteable = false;
    	}
    
        if (mExternalStorageAvailable && mExternalStorageWriteable)
        	return true;
        else
        	return false;
    }
    
    private void unpackPrograms()
    {
        if (!isDataCardAvailable())
        	return;

        File extFilesDir = Environment.getExternalStorageDirectory();
    	File hpDir = new File(extFilesDir+HPDIR);
        hpDir.mkdir();

        if (!(new File(extFilesDir+HPDIR+"/Moon Lander.hp97")).exists())
        {
            Resources res = getResources();
            copyToFile(res.openRawResource(R.raw.moon),extFilesDir+HPDIR+"/Moon Lander.hp97");
            copyToFile(res.openRawResource(R.raw.sd15),extFilesDir+HPDIR+"/SD15 Diagnostic.hp97");
        }
    }
    
    private void loadProgram()
    {
        Intent i = new Intent(this, ProgramListActivity.class);
        startActivityForResult(i, ACTIVITY_PGMLIST);
    }
    
    private void saveProgram()
    {
        Intent i = new Intent(this, ProgramSaveActivity.class);
        i.putExtra("PGM", _pgmfile);
        i.putExtra("A", ((TextView)findViewById(R.id.A)).getText());
        i.putExtra("B", ((TextView)findViewById(R.id.B)).getText());
        i.putExtra("C", ((TextView)findViewById(R.id.C)).getText());
        i.putExtra("D", ((TextView)findViewById(R.id.D)).getText());
        i.putExtra("E", ((TextView)findViewById(R.id.E)).getText());
        i.putExtra("a", ((TextView)findViewById(R.id.a)).getText());
        i.putExtra("b", ((TextView)findViewById(R.id.b)).getText());
        i.putExtra("c", ((TextView)findViewById(R.id.c)).getText());
        i.putExtra("d", ((TextView)findViewById(R.id.d)).getText());
        i.putExtra("e", ((TextView)findViewById(R.id.e)).getText());
        startActivityForResult(i, ACTIVITY_PGMSAVE);
    }
    
    private void saveData()
    {
        Intent i = new Intent(this, DataSaveActivity.class);
        i.putExtra("PGM", _datafile);
        i.putExtra("A", ((TextView)findViewById(R.id.A)).getText());
        i.putExtra("B", ((TextView)findViewById(R.id.B)).getText());
        i.putExtra("C", ((TextView)findViewById(R.id.C)).getText());
        i.putExtra("D", ((TextView)findViewById(R.id.D)).getText());
        i.putExtra("E", ((TextView)findViewById(R.id.E)).getText());
        i.putExtra("a", ((TextView)findViewById(R.id.a)).getText());
        i.putExtra("b", ((TextView)findViewById(R.id.b)).getText());
        i.putExtra("c", ((TextView)findViewById(R.id.c)).getText());
        i.putExtra("d", ((TextView)findViewById(R.id.d)).getText());
        i.putExtra("e", ((TextView)findViewById(R.id.e)).getText());
        startActivityForResult(i, ACTIVITY_DATASAVE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode==ACTIVITY_PGMLIST && resultCode == ACTION_SELECTED)
        {
        	HP97 hp = _hp.GetInnerHP97();
            File extFilesDir = Environment.getExternalStorageDirectory();
        	String pgmFile = intent.getStringExtra("PGMFILE");
        	_pgmfile = pgmFile;
        	HP97Program pgm = HP97ProgramRepo.load(extFilesDir+HPDIR+"/"+pgmFile+".hp97");
        	if (pgm.IsProgram)
        	{
	        	TextView pgmName = (TextView) findViewById(R.id.pgmName);
	        	pgmName.setText(pgmFile);
	        	TextView A = (TextView) findViewById(R.id.A);
	        	A.setText(pgm.LabelA);
	        	TextView a = (TextView) findViewById(R.id.a);
	        	a.setText(pgm.Labela);
	        	TextView B = (TextView) findViewById(R.id.B);
	        	B.setText(pgm.LabelB);
	        	TextView b = (TextView) findViewById(R.id.b);
	        	b.setText(pgm.Labelb);
	        	TextView C = (TextView) findViewById(R.id.C);
	        	C.setText(pgm.LabelC);
	        	TextView c = (TextView) findViewById(R.id.c);
	        	c.setText(pgm.Labelc);
	        	TextView D = (TextView) findViewById(R.id.D);
	        	D.setText(pgm.LabelD);
	        	TextView d = (TextView) findViewById(R.id.d);
	        	d.setText(pgm.Labeld);
	        	TextView E = (TextView) findViewById(R.id.E);
	        	E.setText(pgm.LabelE);
	        	TextView e = (TextView) findViewById(R.id.e);
	        	e.setText(pgm.Labele);
	        	hp.SetProgram(pgm);
	        	hp._pgmStep = 0;
	        	setDisplay(_hp.GetDisplay());        
	        	ImageView card = (ImageView) findViewById(R.id.card);
	        	card.setAlpha(255);
	        	_vMain.invalidate();
        	}
        	else
        	{
        		_datafile = new String(_pgmfile);
        		HP97Program data = HP97DataRepo.load(extFilesDir+HPDIR+"/"+pgmFile+".hp97");
        		hp.SetData(data);
        	}
        }
        if (requestCode==ACTIVITY_PGMSAVE && resultCode == ACTION_SAVE)
        {
            File extFilesDir = Environment.getExternalStorageDirectory();
        	String pgmFile = intent.getStringExtra("PGMFILE");
        	HP97Program pgm = _hp.GetInnerHP97().GetProgram();
        	pgm.LabelA = intent.getStringExtra("A");
        	pgm.LabelB = intent.getStringExtra("B");
        	pgm.LabelC = intent.getStringExtra("C");
        	pgm.LabelD = intent.getStringExtra("D");
        	pgm.LabelE = intent.getStringExtra("E");
        	pgm.Labela = intent.getStringExtra("a");
        	pgm.Labelb = intent.getStringExtra("b");
        	pgm.Labelc = intent.getStringExtra("c");
        	pgm.Labeld = intent.getStringExtra("d");
        	pgm.Labele = intent.getStringExtra("e");
        	TextView pgmName = (TextView) findViewById(R.id.pgmName);
        	pgmName.setText(pgmFile);
        	TextView A = (TextView) findViewById(R.id.A);
        	A.setText(pgm.LabelA);
        	TextView a = (TextView) findViewById(R.id.a);
        	a.setText(pgm.Labela);
        	TextView B = (TextView) findViewById(R.id.B);
        	B.setText(pgm.LabelB);
        	TextView b = (TextView) findViewById(R.id.b);
        	b.setText(pgm.Labelb);
        	TextView C = (TextView) findViewById(R.id.C);
        	C.setText(pgm.LabelC);
        	TextView c = (TextView) findViewById(R.id.c);
        	c.setText(pgm.Labelc);
        	TextView D = (TextView) findViewById(R.id.D);
        	D.setText(pgm.LabelD);
        	TextView d = (TextView) findViewById(R.id.d);
        	d.setText(pgm.Labeld);
        	TextView E = (TextView) findViewById(R.id.E);
        	E.setText(pgm.LabelE);
        	TextView e = (TextView) findViewById(R.id.e);
        	e.setText(pgm.Labele);
       	    HP97ProgramRepo.save(pgm,extFilesDir+HPDIR+"/"+pgmFile+".hp97");
        	setDisplay(_hp.GetDisplay());      
        }
        if (requestCode==ACTIVITY_DATASAVE && resultCode == ACTION_SAVE)
        {
            File extFilesDir = Environment.getExternalStorageDirectory();
        	String pgmFile = intent.getStringExtra("PGMFILE");
        	HP97Program data = _hp.GetInnerHP97().GetData();
       		HP97DataRepo.save(data,extFilesDir+HPDIR+"/"+pgmFile+".hp97");
        	setDisplay(_hp.GetDisplay());      
        }
    }

    private void copyToFile(InputStream in, String filename)
    {
    	try
    	{
        	OutputStream out = new FileOutputStream(filename);
        	byte[] buffer = new byte[1024];
        	int len;
        	while ((len = in.read(buffer))>0)
        	{
        		out.write(buffer, 0, len);
        	}
        	in.close();
        	out.close();
    	}
    	catch (Exception ex)
    	{
    	}
    	
    }
    
	public void btnPressed(View b)
    {
    	// Do nothing method referenced in the main.xml for button clicks.
    	// Left here in case I want to respond to full key press in the future.
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
        display.setText(txt);    	
    }

	public void DisplayUpdateHandler(Object sender, final DisplayEventArgs e) {
		mHandler.post(new Runnable(){
			public void run()
			{
				setDisplay(_hp.GetDisplay());
			}
		});
		// TODO Auto-generated method stub
		
	}

	public void WriteDataHandler(Object sender, final DisplayEventArgs e) {
		mHandler.post(new Runnable(){
			public void run()
			{
                saveData();
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
