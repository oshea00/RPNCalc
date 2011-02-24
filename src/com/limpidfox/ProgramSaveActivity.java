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
		((EditText)findViewById(R.id.filename)).setText(getIntent().getStringExtra("PGM"));
		((EditText)findViewById(R.id.Alabel)).setText(getIntent().getStringExtra("A"));
		((EditText)findViewById(R.id.Blabel)).setText(getIntent().getStringExtra("B"));
		((EditText)findViewById(R.id.Clabel)).setText(getIntent().getStringExtra("C"));
		((EditText)findViewById(R.id.Dlabel)).setText(getIntent().getStringExtra("D"));
		((EditText)findViewById(R.id.Elabel)).setText(getIntent().getStringExtra("E"));
		((EditText)findViewById(R.id.alabel)).setText(getIntent().getStringExtra("a"));
		((EditText)findViewById(R.id.blabel)).setText(getIntent().getStringExtra("b"));
		((EditText)findViewById(R.id.clabel)).setText(getIntent().getStringExtra("c"));
		((EditText)findViewById(R.id.dlabel)).setText(getIntent().getStringExtra("d"));
		((EditText)findViewById(R.id.elabel)).setText(getIntent().getStringExtra("e"));
	}
	
	public void btnPressed(View v)
    {
		EditText pgm = (EditText) findViewById(R.id.filename);
		EditText A = (EditText) findViewById(R.id.Alabel);
		EditText B = (EditText) findViewById(R.id.Blabel);
		EditText C = (EditText) findViewById(R.id.Clabel);
		EditText D = (EditText) findViewById(R.id.Dlabel);
		EditText E = (EditText) findViewById(R.id.Elabel);
		EditText a = (EditText) findViewById(R.id.alabel);
		EditText b = (EditText) findViewById(R.id.blabel);
		EditText c = (EditText) findViewById(R.id.clabel);
		EditText d = (EditText) findViewById(R.id.dlabel);
		EditText e = (EditText) findViewById(R.id.elabel);
		Intent i = new Intent();
		i.putExtra("PGMFILE",pgm.getText().toString());
		i.putExtra("A",A.getText().toString());
		i.putExtra("B",B.getText().toString());
		i.putExtra("C",C.getText().toString());
		i.putExtra("D",D.getText().toString());
		i.putExtra("E",E.getText().toString());
		i.putExtra("a",a.getText().toString());
		i.putExtra("b",b.getText().toString());
		i.putExtra("c",c.getText().toString());
		i.putExtra("d",d.getText().toString());
		i.putExtra("e",e.getText().toString());
		setResult(Calculator.ACTION_SAVE,i);
		finish();
    }
	
}
