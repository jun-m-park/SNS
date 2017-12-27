package com.example.junmp.teamproject;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by junmp on 2017-11-30.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private ArrayList<Post> mDataSet;
    private Context mContext;
    private String mUserId;

    String music_name;


    Handler threadHandler = new Handler();

    public PostAdapter(ArrayList<Post> dataList, Context context, String userId){
        mDataSet = dataList;
        mContext = context;
        mUserId = userId;
    }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder holder, int position) {
        if(holder == null){
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_post, null);
            holder = new ViewHolder(view);
        }
        final Post item = mDataSet.get(position);

        holder.mCaptionView.setText(item.getCaption());
        holder.mCreateTime.setText(item.getTime());
        holder.mLikeView.setText(item.getLike()+"명이 공감합니다.");


        holder.buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Select SM = new Select();
                SM.execute(item.getPostid());
            }
        });
        holder.buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchlike SL = new searchlike();
                SL.execute(item.getPostid());
            }
        });

        getImage_post1(holder.mContentImageView, holder.mProfileImageView, item.getPostid(), holder.mNameView);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView mProfileImageView;
        TextView mNameView;
        ImageView mContentImageView;
        TextView mCaptionView;
        TextView mCreateTime;
        Button buttonStart;
        Button buttonLike;
        TextView mLikeView;

        TextView textMaxTime;
        TextView textCurrentPosition;


        public ViewHolder(View view) {
            super(view);

            mNameView = view.findViewById(R.id.user_name);
            mCaptionView = view.findViewById(R.id.caption);
            mCreateTime = view.findViewById(R.id.create_time);
            mContentImageView = view.findViewById(R.id.post_img);
            mProfileImageView = view.findViewById(R.id.profile_img);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mProfileImageView.setBackground(new ShapeDrawable(new OvalShape()));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mProfileImageView.setClipToOutline(true);
            }

            textMaxTime = view.findViewById(R.id.maxtime);
            textCurrentPosition = view.findViewById(R.id.mintime);
            buttonStart = view.findViewById(R.id.play);

            buttonLike = view.findViewById(R.id.btn_like);
            mLikeView = view.findViewById(R.id.like_num);
        }
    }

    public void Thread(){
        Runnable task = new Runnable(){
            public void run(){
                while(FeedActivity.ms.player.isPlaying()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    FeedActivity.seekBar.setProgress(FeedActivity.ms.player.getCurrentPosition());
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void getImage_post1(final ImageView imageView1, final ImageView imageView2, final String postid, final TextView mnameview) {
        class GetImagePost extends AsyncTask<String, Void, Bitmap> {
            ProgressDialog loading;
            Bitmap sample;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(mContext, "Loading Data", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                loading.dismiss();
                sample = b;

                imageView1.setImageBitmap(b);

                getImage_post2(imageView2, postid, mnameview);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String post_id = params[0];

                String add = "http://52.78.243.205/PostImageDownload.php?postid=" + post_id;

                URL url = null;
                Bitmap image = null;
                try {
                    url = new URL(add);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            }
        }

        GetImagePost gi = new GetImagePost();
        gi.execute(postid);
    }

    private void getImage_post2(final ImageView imageView, final String postid, final TextView mnameview) {
        class GetImagePost extends AsyncTask<String, Void, Bitmap> {
            Bitmap sample;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                sample = b;

                imageView.setImageBitmap(b);

                getname(postid, mnameview);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String post_id = params[0];

                String add = "http://52.78.243.205/ppdownload.php?postid=" + post_id;

                URL url = null;
                Bitmap image = null;
                try {
                    url = new URL(add);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            }
        }

        GetImagePost gi = new GetImagePost();
        gi.execute(postid);
    }

    private void getname(String postid, final TextView mnameview) {
        class selectname extends AsyncTask<String,Void,String> {
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(String... arg0) {

                try {
                    String postid =  arg0[0];

                    String link = "http://52.78.243.205/selectname.php?postid=" + postid;
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
                mnameview.setText(result);
            }
        }

        selectname SN = new selectname();
        SN.execute(postid);
    }

    class Select extends AsyncTask<String,Void,String> {
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                String post_id =  arg0[0];

                String link = "http://52.78.243.205/playmusic.php?id=" + post_id;
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
            onMusicListener mMusicListener;

            mMusicListener = new onMusicListener() {
                @Override
                public void restartMusic(String uri) {
                    FeedActivity.ms.player.stop();
                    FeedActivity.ms.player = new MediaPlayer();
                    FeedActivity.ms.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        FeedActivity.ms.player.setDataSource(uri);
                        FeedActivity.ms.player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FeedActivity.ms.player.start();

                    FeedActivity.isMusic = true;
                }
                @Override
                public void startMusic(String uri) {
                    FeedActivity.ms.player = new MediaPlayer();
                    FeedActivity.ms.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        FeedActivity.ms.player.setDataSource(uri);
                        FeedActivity.ms.player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FeedActivity.ms.player.start();

                    FeedActivity.isMusic = true;
                }

                @Override
                public void stopMusic() {
                    FeedActivity.ms.player.stop();
                    FeedActivity.isMusic = false;
                }
            };

            String url =  "http://52.78.243.205/musiclist/"+result;

            FeedActivity.nameMusic.setText("현재 곡: "+result);

            if(FeedActivity.isMusic){
                Toast.makeText(mContext, "새로운 음악을 실행합니다.", Toast.LENGTH_LONG).show();
                mMusicListener.restartMusic(url);
            }
            else{
                Toast.makeText(mContext, "음악을 실행합니다.", Toast.LENGTH_LONG).show();
                mMusicListener.startMusic(url);
            }
            FeedActivity.seekBar.setMax(FeedActivity.ms.player.getDuration());

            int temp = FeedActivity.ms.player.getDuration();
            int m = temp / 60000;
            int s = (temp % 60000) / 1000;
            String strTime = String.format("%02d:%02d", m, s);
            FeedActivity.maxTime.setText(strTime);

            Thread();
        }
    }

    interface onMusicListener{
        void restartMusic(String uri);
        void startMusic(String uri);
        void stopMusic();
    }

    private class searchlike extends AsyncTask<String,Void,String> {

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                String post_id =  arg0[0];

                String link = "http://52.78.243.205/searchlike.php?id=" + post_id;
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
            Toast.makeText(mContext,"당신이 이 사진에 공감합니다.", Toast.LENGTH_LONG).show();
        }
    }
}
