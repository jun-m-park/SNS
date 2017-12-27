package com.example.junmp.teamproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{
    String userId;
    String userName;
    TextView musicView;
    private OnSelectedListener mListener;
    ArrayList<Post> post_list = new ArrayList<>();

    RecyclerView listView;
    View view;
    PostAdapter adapter;
    LinearLayoutManager manager;
    private static final String TAG_JSON="webnautes";
    private static final String TAG_POSTID="post_id";
    private static final String TAG_CONTENT="content";
    private static final String TAG_CREATE_TIME="create_time";
    private static final String TAG_LIKE_NUM="like_num";
    String mJsonString;

    LoadPost loadPost;

    Button change_profile_img;
    ImageView profile_img;
    Button logout;
    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String userId, String userName) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.userId = userId;
        fragment.userName = userName;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView nameView = view.findViewById(R.id.text_name);
        musicView = view.findViewById(R.id.text_music);

        change_profile_img = view.findViewById(R.id.change_profile_img);

        change_profile_img.setOnClickListener(this);

        logout = view.findViewById(R.id.btn_logout);
        logout.setOnClickListener(this);

        profile_img = view.findViewById(R.id.profile_img);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            profile_img.setBackground(new ShapeDrawable(new OvalShape()));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profile_img.setClipToOutline(true);
        }


        nameView.setText(userName);


        loadPost = new LoadPost();
        loadPost.execute("http://52.78.243.205/loadpost.php");

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if(view == logout){
            getActivity().finish();
        }
        else if(view == change_profile_img){
            Intent intent = new Intent(getContext(), UploadProfileIMGActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        }
    }


    public interface OnSelectedListener {
        // TODO: Update argument type and name
        void onPostSelected(Post item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class LoadPost extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result == null){
            }
            else {
                mJsonString = result;
                try {
                    showResult();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0]+"?id="+userId;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult() throws JSONException {
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=jsonArray.length()-1;i>=0;i--){
                JSONObject item = jsonArray.getJSONObject(i);

                String postid = item.getString(TAG_POSTID);
                String content = item.getString(TAG_CONTENT);
                String create_time = item.getString(TAG_CREATE_TIME);
                String like_num = item.getString(TAG_LIKE_NUM);
                post_list.add(new Post(content, postid, userId, create_time, Integer.parseInt(like_num), null));
            }

            listView = view.findViewById(R.id.post_frag_list);
            manager = new LinearLayoutManager(getActivity());
            listView.setLayoutManager(manager);
            adapter = new PostAdapter(post_list, getActivity(), userId);
            listView.setAdapter(adapter);

            getImage();

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    private void getImage() {
        final String userid = userId.trim();
        class GetImage extends AsyncTask<String,Void,Bitmap>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getContext(), "Loading Data", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                loading.dismiss();
                profile_img.setImageBitmap(b);
                extractmusic EM = new extractmusic();
                EM.execute(userid);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String id = params[0];
                String add = "http://52.78.243.205/ProfileImageDownload.php?id="+id;
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

        GetImage gi = new GetImage();
        gi.execute(userid);
    }

    private class extractmusic extends AsyncTask<String,Void,String> {

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                String id =  arg0[0];

                String link = "http://52.78.243.205/extractmusic.php?id=" + id;
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
            musicView.setText("최근 추천 음악: "+ result);
        }
    }

}