package com.swufe.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RateActivity extends AppCompatActivity {
    private final String TAG="Rate";
    private float dollarRate=0.1f;
    private float euroRate=0.2f;
    private float wonRate=0.3f;
    EditText rmb;
    TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb=(EditText)findViewById(R.id.rmb);
        show=(TextView)findViewById(R.id.show);
    }
    public void onClick(View btn){
        String str=rmb.getText().toString();
        float r=0;
        float val=0;
        if(str.length()>0){
            r=Float.parseFloat(str);
        }
        else{
            Toast.makeText(this,"Input Error",Toast.LENGTH_SHORT).show();
        }
        if(btn.getId()==R.id.btn_dollar){
            val=r*(1/7.1f);
        }
        else if(btn.getId()==R.id.btn_euro){
            val=r*(1/7.8f);
        }
        else if(btn.getId()==R.id.btn_won){
            val=r*174f;
        }
        show.setText(val+"");
    }
    public void openOne(View btn){
        Intent hello=new Intent(this,ConfigActivity.class);
        startActivity(hello);
    }
}
