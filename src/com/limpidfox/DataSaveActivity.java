package com.limpidfox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class DataSaveActivity extends Activity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dataname);
		((EditText)findViewById(R.id.filename)).setText(getIntent().getStringExtra("PGM"));
	}
	
	public void btnPressed(View v)
    {
		EditText pgm = (EditText) findViewById(R.id.filename);
		Intent i = new Intent();
		i.putExtra("PGMFILE",pgm.getText().toString());
		setResult(Calculator.ACTION_SAVE,i);
		finish();
    }
	
}
