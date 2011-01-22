package com.limpidfox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ProgramSaveActivity extends Activity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pgnname);
	}
	
	public void btnPressed(View b)
    {
		EditText e = (EditText) findViewById(R.id.filename);
		Intent i = new Intent();
		i.putExtra("PGMFILE",e.getText().toString());
		setResult(Calculator.ACTION_SAVE,i);
		finish();
    }
	
}
