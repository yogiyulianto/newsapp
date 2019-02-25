package com.example.newsapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    String API_KEY = "0b7e92a424284aff9ac3a6ee5a5c4974";
    String NEWS_SOURCE = "bbc-news";
    ListView listView;
    ProgressBar loader;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
    static  final String KEY_AUTHOR = "author";
    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_URL = "url";
    static final String KEY_URLTOIMAGE = "urlToImage";
    static final String KEY_PUBLISHEDAT = "publishedAt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lv_news);
        loader = findViewById(R.id.pb_loader);
        listView.setEmptyView(loader);

        if (Function.isNetworkAvailable(getApplicationContext())){
            DownloadNews newsTask = new DownloadNews();
            newsTask.execute();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

        class DownloadNews extends AsyncTask<String, Void, String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            protected String doInBackground(String... args) {
                String xml = "";

                String urlParameters = "";
                xml = Function.excuteGet("https://newsapi.org/v1/articles?source="+NEWS_SOURCE+"&sortBy=top&apiKey="+API_KEY, urlParameters);
                return xml;
            }
            @Override
            protected void onPostExecute(String xml) {
                if (xml.length() > 10) {
                    try {
                        JSONObject jsonResponse = new JSONObject(xml);
                        JSONArray jsonArray = jsonResponse.optJSONArray("articles");
                        for (int i = 0;i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(KEY_AUTHOR, jsonObject.optString(KEY_AUTHOR).toString());
                            map.put(KEY_TITLE, jsonObject.optString(KEY_TITLE).toString());
                            map.put(KEY_DESCRIPTION, jsonObject.optString(KEY_DESCRIPTION).toString());
                            map.put(KEY_URL, jsonObject.optString(KEY_URL).toString());
                            map.put(KEY_URLTOIMAGE, jsonObject.optString(KEY_URLTOIMAGE).toString());
                            map.put(KEY_PUBLISHEDAT, jsonObject.optString(KEY_PUBLISHEDAT).toString());
                            dataList.add(map);
                        }
                    } catch (JSONException e){
                        Toast.makeText(getApplicationContext(), "Unecpected error", Toast.LENGTH_SHORT).show();
                    }

                    ListNewsAdapter adapter = new ListNewsAdapter(MainActivity.this, dataList);
                    listView.setAdapter((ListAdapter) adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent i = new Intent(MainActivity.this, DetailActivity.class);
                            i.putExtra("url", dataList.get(+position).get(KEY_URL));
                            startActivity(i);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "No news found", Toast.LENGTH_SHORT).show();
                }
            }
        }





        }



