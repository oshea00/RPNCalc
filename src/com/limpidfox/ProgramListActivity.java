package com.limpidfox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class ProgramListActivity extends ListActivity
{
	List<String> programs;
    private static final int DELETE_ID = Menu.FIRST;
    ArrayAdapter<String> _list;
    Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		programs = new ArrayList<String>(10);
		_list = new ArrayAdapter<String>(this, R.layout.pgmlist, programs);
	    loadPrograms();
		setListAdapter(_list);
		ListView lv = getListView();
	    lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setTextFilterEnabled(true);  
		lv.setOnItemClickListener(new OnItemClickListener() {  
		    @Override
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		    {
		        returnResult(((TextView) view).getText().toString());
		    }
		});
        registerForContextMenu(getListView());
    }
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }
	
	@Override
	public void onContextMenuClosed(Menu menu) {
		super.onContextMenuClosed(menu);
		finish();
	}

	@Override
    public boolean onContextItemSelected(MenuItem item) {
    	
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                TextView t = (TextView) info.targetView;
                String name = t.getText().toString();
                deleteProgram(name);
                return true;
        }
        return false;
    }
	
	public void returnResult(String filename)
	{
		  Intent i = new Intent();
		  i.putExtra("PGMFILE", filename);
		  this.setResult(Calculator.ACTION_SELECTED, i);
		  finish();
	}
	
	private void deleteProgram(String filename)
	{
        File extFilesDir = Environment.getExternalStorageDirectory();
    	File pgm = new File(extFilesDir+Calculator.HPDIR+"/"+filename+".hp97");
    	pgm.delete();
	}
	
    private void loadPrograms()
    {
        File extFilesDir = Environment.getExternalStorageDirectory();
    	File hpDir = new File(extFilesDir+Calculator.HPDIR);
        File[] files = hpDir.listFiles();
        for (File file : files)
        {
        	int periodIdx = file.getName().indexOf(".hp97");
        	programs.add(file.getName().substring(0,periodIdx));
        }
        _list.notifyDataSetChanged();
    }
	
}
