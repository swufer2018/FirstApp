package com.swufe.firstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoticeActivity extends ListActivity implements Runnable, AdapterView.OnItemClickListener{
    HashMap<String,String> map = new HashMap<String, String>();
    List<HashMap<String,String>> retList = new ArrayList<HashMap<String, String>>();
    Handler handler;
    EditText inpNotice;
    ListView listView;
    private ArrayList<HashMap<String,String>> listItems;
    private SimpleAdapter listItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        initListView();
        this.setListAdapter(listItemAdapter);
        Thread t = new Thread(this);
        t.start();
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message message) {
                if (message.what == 8) {
                    List<HashMap<String,String>> list2 = (List<HashMap<String, String>>) message.obj;
                    listItemAdapter = new SimpleAdapter(NoticeActivity.this, list2,
                            R.layout.activity_notice,
                            new String[] { "NoticeTitle","NoticeUrl"},
                            new int[] {R.id.NoticeTitle,R.id.NoticeUrl }
                    );
                    setListAdapter(listItemAdapter);
                }
                super.handleMessage(message);
            }
        };
        getListView().setOnItemClickListener(this);
        inpNotice = (EditText)findViewById(R.id.inpNotice);
        inpNotice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                if(s.length()>0){

                }
            }
        });
    }
    private void initListView(){
        listItems = new ArrayList<HashMap<String, String>>();
        for(int m = 0; m < 10; m++){
            map.put("NoticeTitle", "Title: " +m);
            map.put("NoticeUrl", "Url: " +m);
            listItems.add(map);
        }
        listItemAdapter = new SimpleAdapter(this, listItems,
                R.layout.activity_notice,
                new String[] { "NoticeTitle","NoticeUrl"},
                new int[] {R.id.NoticeTitle, R.id.NoticeUrl }
        );
    }
    public void run(){
        Document document = null;
        try {
            for (int i = 56; i >0; i--) {
                document = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg" + "/" + i + ".htm").get();
                Elements elements2 = document.getElementsByTag("ul");
                Element li = elements2.get(17);
                Elements lisT = li.getElementsByTag("span");
                Elements lisH = li.getElementsByTag("a href");
                for(int j = 0; j < lisT.size(); j++){
                    Element title = lisT.get(j);
                    String lisTStr = title.text();
                    map.put("NoticeTitle",lisTStr);

                }
                for (int k = 0; k < lisH.size(); k++){
                    Element url = lisH.get(k);
                    String lisHStr = url.text().substring(6,26);
                    map.put("NoticeUrl",lisHStr);
                }
            }
            retList.add(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message message = handler.obtainMessage(8);
        message.obj = retList;
        handler.sendMessage(message);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,String> getUrl = (HashMap<String,String>) getListView().getItemAtPosition(position);
        String getUrlStr = getUrl.get("NoticeUrl");
        Intent web = new Intent(this,NoticeActivity.class);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://it.swufe.edu.cn" + getUrlStr)));
    }
}
