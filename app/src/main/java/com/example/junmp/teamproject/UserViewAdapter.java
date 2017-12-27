package com.example.junmp.teamproject;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by junmp on 2017-11-28.
 */

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.ViewHolder> {
    private ArrayList<Friend> mDataSet;
    private ArrayList<Friend> displayListItem = new ArrayList<>();
    private Context mContext;
    private String mUserId;
    add_friend add_friend;

    public ArrayList<Friend> filter(String searchText) {
        displayListItem.clear();
        if(searchText.length() == 0)
        {
        }
        else
        {
            for( Friend item : mDataSet)
            {
                if(item.getUserName().contains(searchText))
                {
                    displayListItem.add(item);
                }
            }
        }
        notifyDataSetChanged();

        return displayListItem;
    }


    public UserViewAdapter(ArrayList<Friend> dataList, Context context, String userId){
        mDataSet = dataList;
        mContext = context;
        mUserId = userId;
    }

    @Override
    public UserViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new UserViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewAdapter.ViewHolder holder, int position) {
        if(holder == null){
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_user, null);
            holder = new UserViewAdapter.ViewHolder(view);
        }
        final Friend item = mDataSet.get(position);

        holder.mNameView.setText(item.getUserName());
        holder.mIdView.setText(item.getUserId());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mNameView;
        TextView mIdView;
        Button mButton;
        check_friend check_friend;
        int btn_check=0;

        public ViewHolder(View view){
            super(view);

            mIdView = (TextView) view.findViewById(R.id.user_id);
            mNameView = (TextView) view.findViewById(R.id.user_name);
            mButton = view.findViewById(R.id.add_friend);
            mButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            check_friend = new check_friend();
            check_friend.execute(mUserId, mIdView.getText().toString());
        }

        private class check_friend extends AsyncTask<String,Void,String> {
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(String... arg0) {

                try {
                    String follower =  arg0[0];
                    String followed = arg0[1];

                    String link = "http://52.78.243.205/friend_check.php?follower=" + follower + "&followed=" + followed;
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
                if(result.length()==0){
                    add_friend = new add_friend();
                    add_friend.execute(mUserId, mIdView.getText().toString());
                }
                else{
                    Toast.makeText(mContext, "이미 친구상태입니다.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class add_friend extends AsyncTask<String,Void,String> {

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                String follower =  arg0[0];
                String followed = arg0[1];

                String link = "http://52.78.243.205/add_friend.php?follower=" + follower + "&followed=" + followed;
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
            Toast.makeText(mContext, "친구목록에 추가되었습니다.", Toast.LENGTH_LONG).show();
        }
    }


}
