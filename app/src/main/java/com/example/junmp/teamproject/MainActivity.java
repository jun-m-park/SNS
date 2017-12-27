package com.example.junmp.teamproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    EditText id_text;
    EditText pw_text;

    String a;

    Button btn_login;
    Button btn_signup;

    phpdo task;
    private String db_result;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id_text = (EditText)findViewById(R.id.id);
        pw_text = (EditText)findViewById(R.id.pw);

        btn_login = (Button)findViewById(R.id.login);
        btn_signup = (Button)findViewById(R.id.signup);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = id_text.getText().toString();
                String pw = pw_text.getText().toString();

                task = new phpdo();
                task.execute(id, pw);
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(signup);
            }
        });
    }
    private class phpdo extends AsyncTask<String,Void,String> {

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                String id =  arg0[0];
                String pw = arg0[1];

                String link = "http://52.78.243.205/test.php?ID=" + id + "&PW=" + pw;
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            db_result = result;

            if(db_result.equals("1")){
                searchname SN = new searchname();
                SN.execute(id);
            }
            else{
                Toast.makeText(MainActivity.this, "일치하는 아이디가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class searchname extends AsyncTask<String,Void,String> {

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                String id =  arg0[0];

                String link = "http://52.78.243.205/searchname.php?ID=" + id;
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
            intent.putExtra("userId", id);
            intent.putExtra("userName", result);
            startActivity(intent);
        }
    }
}
