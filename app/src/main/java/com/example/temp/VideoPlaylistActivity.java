package com.example.temp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VideoPlaylistActivity extends AppCompatActivity {

    private int categoryId = 0;

    List<VideoInfo> playlistVideosInfoList = new ArrayList<>();
    ListAdapter playlistVideosAdapter;
    RecyclerView playlistVideosListView;
    RelativeLayout mainBody;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playlist);

        ImageButton backButton = (ImageButton)this.findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String playlistName, categoryName, playlistImage;
        final int playlistId;
        TextView playlistTitle, categoryTitle;
        ImageView playlistCover;

        Intent i = getIntent();
        playlistId = i.getIntExtra("playlistId", 0);
        playlistImage = i.getStringExtra("playlistImage");
        playlistName = i.getStringExtra("playlistName");
        categoryId = i.getIntExtra("categoryId", 0);
        categoryName = i.getStringExtra("categoryName");

        progressBar = findViewById(R.id.progressBar);
        playlistTitle = findViewById(R.id.playlistTitle);
        categoryTitle = findViewById(R.id.categoryTitle);
        playlistCover = findViewById(R.id.playlistCover);

        playlistTitle.setText(Html.fromHtml(playlistName));
        categoryTitle.setText(Html.fromHtml(categoryName));
        Picasso.get().load(playlistImage).fit().into(playlistCover);

        playlistVideosListView = findViewById(R.id.playlistVideosListView);
        playlistVideosListView.setNestedScrollingEnabled(false);
        mainBody = findViewById(R.id.mainBody);

        playlistVideosAdapter = new ListAdapter(playlistVideosInfoList, this, null);
        RecyclerView.LayoutManager relatedVideosLayoutManager = new LinearLayoutManager(this);
        playlistVideosListView.setLayoutManager(relatedVideosLayoutManager);
        playlistVideosListView.setItemAnimator(new DefaultItemAnimator());
        playlistVideosListView.setAdapter(playlistVideosAdapter);

        playlistVideosInfoList.clear();

        playlistVideosListView.addOnItemTouchListener(new RecyclerTouchListener(this, playlistVideosListView, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                VideoInfo videoInfo = playlistVideosInfoList.get(position);

                if (MainActivity.getInstance().checkConnection()) {
                    Intent i = new Intent(getApplicationContext(), VideoPlayActivity.class);
                    i.putExtra("videoId", videoInfo.getVideoId());
                    i.putExtra("title", videoInfo.getTitle());
                    i.putExtra("playlistId", videoInfo.getPlaylistId());
                    i.putExtra("description", videoInfo.getDescription());
                    i.putExtra("publishDate", videoInfo.getPublishDate());
                    i.putExtra("views", videoInfo.getViews());
                    i.putExtra("thumbnail", videoInfo.getThumbnail());
                    i.putExtra("playlistName", videoInfo.getPlaylistName());
                    i.putExtra("playlistImage", videoInfo.getPlaylistImage());
                    i.putExtra("categoryId", videoInfo.getCategoryId());
                    i.putExtra("categoryName", videoInfo.getCategoryName());
                    startActivity(i);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(MainActivity.getInstance().findViewById(R.id.mainLayout), "Can't Connect to Internet. Try Again Later.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

            }
        }));

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                playlistVideosInfoList.clear();
                mainBody.setVisibility(View.GONE);
                new getPlaylistVideosInfoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://acropetal-gases.000webhostapp.com/app/video_json.php?playlistId="+String.valueOf(playlistId));
                pullToRefresh.setRefreshing(false);
            }
        });

        new getPlaylistVideosInfoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://acropetal-gases.000webhostapp.com/app/video_json.php?playlistId="+String.valueOf(playlistId));

    }

    public interface RecyclerViewItemClickListener {
        void onClick(View view, int position);
    }

    class getPlaylistVideosInfoList extends AsyncTask<String,Void,String>
    {

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();

                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try{

                    JSONObject jsonObject = new JSONObject(sb.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("videos");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        String video_id = c.getString("video_id");
                        String title = c.getString("title");
                        Integer playlistId = c.getInt("playlistId");
                        String description = c.getString("description");
                        String publishDate = c.getString("publishDate");
                        Integer views = c.getInt("views");
                        String thumbnail = c.getString("thumbnail");
                        String playlistName = c.getString("playlistName");
                        String playlistImage = c.getString("playlistImage");
                        Integer categoryId = c.getInt("categoryId");
                        String categoryName = c.getString("categoryName");
                        VideoInfo videoInfo = new VideoInfo(3, video_id, title, playlistId, description, publishDate, views, thumbnail, playlistName, playlistImage, categoryId, categoryName);
                        playlistVideosInfoList.add(videoInfo);
                    }

                } catch (Exception e) {
                    Log.e("JSON", "Exception: " + e.getMessage());
                }

            } catch (MalformedURLException e) {
                Log.e("", "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e("", "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e("", "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e("", "Exception: " + e.getMessage());
            }

            return params[0];
        }

        protected void onPostExecute(String text) {
            playlistVideosAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            mainBody.setVisibility(View.VISIBLE);
        }
    }
}
