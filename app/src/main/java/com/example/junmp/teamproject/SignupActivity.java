package com.example.junmp.teamproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class SignupActivity extends AppCompatActivity {
    EditText text_id;
    EditText text_pw;
    EditText text_pwcheck;
    EditText text_name;

    String id;
    String pw;
    String pwcheck;
    String name;
    Button btn_signup;

    checkDB check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        text_id = findViewById(R.id.text_id);
        text_pw = findViewById(R.id.text_pw);
        text_pwcheck = findViewById(R.id.text_pwcheck);
        text_name = findViewById(R.id.text_name);

        btn_signup = findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = text_id.getText().toString();
                pw = text_pw.getText().toString();
                name = text_name.getText().toString();

                pwcheck = text_pwcheck.getText().toString();

                if(!pw.equals(pwcheck)){
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SignupActivity.this);

                    alertBuilder
                            .setTitle("알림")
                            .setMessage("비밀번호가 일치하지 않습니다.")
                            .setCancelable(true)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //finish();
                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }
                else{
                    check = new checkDB();
                    check.execute(id);
                }
            }
        });
    }
    private class registDB extends AsyncTask<String,Void,String> {

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                String id =  arg0[0];
                String pw = arg0[1];
                String name = arg0[2];

                String link = "http://52.78.243.205/test2.php?u_id=" + id + "&u_pw=" + pw + "&name=" + name;
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
            Toast.makeText(SignupActivity.this, "회원가입에 성공하였습니다.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class checkDB extends AsyncTask<String,Void,String> {

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                String id =  arg0[0];

                String link = "http://52.78.243.205/test3.php?ID=" + id;
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
            if(id.equals(result)){
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SignupActivity.this);

                alertBuilder
                        .setTitle("알림")
                        .setMessage("해당 아이디의 사용자가 존재합니다.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
            else{
                registDB rdb = new registDB();
                rdb.execute(id, pw, name);
            }
        }
    }
}
