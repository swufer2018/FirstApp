package com.swufe.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    public Log log;
    TextView temp;
    TextView out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button btn=(Button)findViewById(R.id.button);
        btn.setOnClickListener(this);
        temp=(TextView)findViewById(R.id.inpText);
        out=(TextView)findViewById(R.id.out);
    }

    @Override
    public void onClick(View button) {
        log.i("main","transform");
        String str=(String)temp.getText().toString();
        if(str.length()==0){
            out.setText("outPut:"+"error");
        }
        else{
            double num=Double.parseDouble(str);
            num=num*1.8+32;
            out.setText("outPut:"+num);

        }

    }
}
