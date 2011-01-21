package com.limpidfox;

import java.io.File;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ProgramListActivity extends ListActivity
{
	String[] programs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		  loadPrograms();
		  setListAdapter(new ArrayAdapter<String>(this, R.layout.pgmlist, programs));
		  ListView lv = getListView();  
		  lv.setTextFilterEnabled(true);  
		  lv.setOnItemClickListener(new OnItemClickListener() {  
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			  {
				  returnResult(((TextView) view).getText().toString()+".hp97");
			  }
		  });	
	  }

	public void returnResult(String filename)
	{
		  Intent i = new Intent();
		  i.putExtra("PGMFILE", filename);
		  this.setResult(Calculator.ACTION_SELECTED, i);
		  finish();
	}
	
    private void loadPrograms()
    {
        File extFilesDir = Environment.getExternalStorageDirectory();
    	File hpDir = new File(extFilesDir+Calculator.HPDIR);
        File[] files = hpDir.listFiles();
        programs = new String[files.length];
        int i=0;
        for (File file : files)
        {
        	int periodIdx = file.getName().indexOf(".hp97");
        	programs[i++] = file.getName().substring(0,periodIdx);
        }
    }
	
}
