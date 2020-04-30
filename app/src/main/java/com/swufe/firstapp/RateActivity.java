package com.swufe.firstapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class RateActivity extends AppCompatActivity implements Runnable{
    private final String TAG="Rate";
    private float dollarRate=0.0f;
    private float euroRate=0.0f;
    private float wonRate=0.0f;
    private String updateDate="";
    EditText rmb;
    TextView show;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb=(EditText)findViewById(R.id.rmb);
        show=(TextView)findViewById(R.id.show);
        SharedPreferences sharedPreferences=getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollarRate=sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate=sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate=sharedPreferences.getFloat("won_rate",0.0f);
        updateDate=sharedPreferences.getString("update_date","");
        Date today=Calendar.getInstance().getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr=sdf.format(today);
        if(!today.equals(updateDate)) {
            Thread t = new Thread(this);
            t.start();
        }

        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {

//                SharedPreferences time=getSharedPreferences("mytime",Activity.MODE_PRIVATE);
//                int lastTime=time.getInt("systemTime",0);
//                Calendar newCalendar=Calendar.getInstance();
//                int newYear=newCalendar.get(Calendar.YEAR);
//                int newMonth=newCalendar.get(Calendar.MONTH)+1;
//                int newDay=newCalendar.get(Calendar.DAY_OF_MONTH);
//                int newTime=newYear*10000+newMonth*100+newDay;
//                show.setText(newTime+"");
                if(msg.what==5){
                    Bundle bd1=(Bundle)msg.obj;
                    dollarRate=bd1.getFloat("dollar-rate");
                    euroRate=bd1.getFloat("euro-rate");
                    wonRate=bd1.getFloat("won-rate");
                    SharedPreferences sharedPreferences=getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("update_date",todayStr);
                    editor.putFloat("dollar_rate",dollarRate);
                    editor.putFloat("euro_rate",euroRate);
                    editor.putFloat("won_rate",wonRate);
                    editor.apply();
                    Toast.makeText(RateActivity.this,"汇率已更新",Toast.LENGTH_LONG).show();

                    //getTime();
                }
                super.handleMessage(msg);
            }
        };
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
            return;
        }
        if(btn.getId()==R.id.btn_dollar){
            val=r*dollarRate;
        }
        else if(btn.getId()==R.id.btn_euro){
            val=r*euroRate;
        }
        else if(btn.getId()==R.id.btn_won){
            val=r*wonRate;
        }
        show.setText(val+"");
    }
    public void openOne(View btn){
        openConfig();
    }

    private void openConfig() {
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dollar_rate_key", dollarRate);
        config.putExtra("euro_rate_key", euroRate);
        config.putExtra("won_rate_key", wonRate);
        startActivityForResult(config, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_settings){
            openConfig();
        }
        else if(item.getItemId()==R.id.open_list){
            Intent list = new Intent(this, MyList2Activity.class);

            startActivity(list);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1 && resultCode==2){
            Bundle bundle=data.getExtras();
            dollarRate=bundle.getFloat("key_dollar",0.1f);
            euroRate=bundle.getFloat("key_euro",0.1f);
            wonRate=bundle.getFloat("key_won",0.1f);
            SharedPreferences sharedPreferences=getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.commit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void run() {
        Bundle bundle;

//        URL url= null;
//        try {
//            url = new URL("https://www.usd-cny.com/bankofchina.htm");
//            HttpsURLConnection https= (HttpsURLConnection) url.openConnection();
//            InputStream in=https.getInputStream();
//            String html=inputStream2String(in);
//            Document doc=Jsoup.parse(html);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        bundle=getFromBOC();
        Message msg=handler.obtainMessage(5);
        //msg.obj ="Hello from run()";
        msg.obj=bundle;
        handler.sendMessage(msg);

    }

    private Bundle getFromBOC() {
        Bundle bundle=new Bundle();
        Document doc=null;
        try {
            doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
            Elements tables=doc.getElementsByTag("table");
            Element table2=tables.get(1);
            //Log.i(TAG,"run:table0"+table0);
            Elements tds=table2.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=8){
                Element td1=tds.get(i);
                Element td2=tds.get(i+5);
                String str1=td1.text();
                String val=td2.text();
                if("美元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }
                else if("欧元".equals(str1)){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }
                else if("韩国元".equals(str1)){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }
    private Bundle getFromUsdCny() {
        Bundle bundle=new Bundle();
        Document doc=null;
        try {
            doc = Jsoup.connect("https://www.usd-cny.com/bankofchina.htm").get();
            Elements tables=doc.getElementsByTag("table");
            Element table0=tables.get(0);
            //Log.i(TAG,"run:table0"+table0);
            Elements tds=table0.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=6){
                Element td1=tds.get(i);
                Element td2=tds.get(i+5);
                String str1=td1.text();
                String val=td2.text();
                if("美元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }
                else if("欧元".equals(str1)){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }
                else if("韩元".equals(str1)){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

//    public void getTime(){
//        Calendar calendar=Calendar.getInstance();
//        int year=calendar.get(Calendar.YEAR);
//        int month=calendar.get(Calendar.MONTH)+1;
//        int day=calendar.get(Calendar.DAY_OF_MONTH);
//        int systemTime=year*10000+month*100+day;
//        SharedPreferences lastTime=getSharedPreferences("mytime",Activity.MODE_PRIVATE);
//        SharedPreferences.Editor edTime=lastTime.edit();
//        edTime.putInt("systemTime",systemTime);
//        edTime.commit();
//    }


    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize=1024;
        final char[] buffer=new char[bufferSize];
        final StringBuilder out=new StringBuilder();
        Reader in=new InputStreamReader(inputStream,"gb2312");
        for(;;){
            int rsz=in.read(buffer,0,buffer.length);
            if(rsz<0){
                break;
            }
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }
}
