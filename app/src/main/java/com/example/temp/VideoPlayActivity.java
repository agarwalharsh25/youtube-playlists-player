package com.example.temp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.temp.RoundedCornersTransform.CornerType.CIRCLE;
import static com.example.temp.RoundedCornersTransform.CornerType.TOP;

public class VideoPlayActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private String videoId = "";
//    private String title = "", description = "", publishDate = "", thumbnail = "", playlistName = "", categoryName = "", playlistImage = "";
//    private Integer views = 0, playlistId = 0, categoryId = 0;
//    private TextView videoTitle, videoViews, videoDescription, playlistTitle;
//    private ImageView playlistIcon;
    private int playlistId = 0;
    private Set<String> videoIdSet = new HashSet<>();

    List<VideoInfo> relatedVideosInfoList = new ArrayList<>();
    ListAdapter relatedVideosAdapter;
    RecyclerView relatedVideosListView;
    RelativeLayout relatedVideosRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        ImageButton backButton = (ImageButton)this.findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final String title, description, publishDate, thumbnail, playlistName, categoryName, playlistImage;
        final int views, categoryId;
        TextView videoTitle, videoViews, videoDate, videoDescription, playlistTitle;
        ImageView playlistIcon;
        RelativeLayout playlistTile;

        Intent i = getIntent();
        videoId = i.getStringExtra("videoId");
        title = i.getStringExtra("title");
        playlistId = i.getIntExtra("playlistId", 0);
        description = i.getStringExtra("description");
        publishDate = i.getStringExtra("publishDate");
        views = i.getIntExtra("views", 0);
        thumbnail = i.getStringExtra("thumbnail");
        playlistName = i.getStringExtra("playlistName");
        playlistImage = i.getStringExtra("playlistImage");
        categoryId = i.getIntExtra("categoryId", 0);
        categoryName = i.getStringExtra("categoryName");

        videoIdSet.add(videoId);

        videoTitle = findViewById(R.id.videoTitle);
        videoViews = findViewById(R.id.videoViews);
        videoDate = findViewById(R.id.videoDate);
        videoDescription = findViewById(R.id.videoDescription);
        playlistTitle = findViewById(R.id.playlistTitle);
        playlistIcon = findViewById(R.id.playlistIcon);
        playlistTile = findViewById(R.id.playlistTile);

        try {
            DateFormat dfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            DateFormat dfOutput = new SimpleDateFormat("MMM dd, yyyy");
            Date date = dfInput.parse(publishDate);
            String pubDate = "Published on " + dfOutput.format(date);
            videoDate.setText(pubDate);
        } catch (Exception e) {
            Log.e("Error", "Date Formatting", e);
            videoDate.setText("");
        }

        videoTitle.setText(Html.fromHtml(title));
        if (views!=1)
            videoViews.setText(Html.fromHtml(String.valueOf(views)+" views"));
        else
            videoViews.setText(Html.fromHtml(String.valueOf(views)+" view"));
        videoDescription.setText(Html.fromHtml(description));
        videoDescription.setMovementMethod(LinkMovementMethod.getInstance());
        playlistTitle.setText(Html.fromHtml(playlistName));
        Picasso.get().load(playlistImage).fit().transform(new RoundedCornersTransform(0, 0, CIRCLE)).into(playlistIcon);

        playlistTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.getInstance().checkConnection()) {
                    Intent i = new Intent(getApplicationContext(), VideoPlaylistActivity.class);
                    i.putExtra("playlistId", playlistId);
                    i.putExtra("playlistImage", playlistImage);
                    i.putExtra("playlistName", playlistName);
                    i.putExtra("categoryId", categoryId);
                    i.putExtra("categoryName", categoryName);
                    startActivity(i);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(MainActivity.getInstance().findViewById(R.id.mainLayout), "Can't Connect to Internet. Try Again Later.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        relatedVideosListView = findViewById(R.id.relatedVideosListView);
        relatedVideosListView.setNestedScrollingEnabled(false);
        relatedVideosRelativeLayout = findViewById(R.id.relatedVideosRelativeLayout);

        relatedVideosAdapter = new ListAdapter(relatedVideosInfoList, this, null);
        RecyclerView.LayoutManager relatedVideosLayoutManager = new LinearLayoutManager(this);
        relatedVideosListView.setLayoutManager(relatedVideosLayoutManager);
        relatedVideosListView.setItemAnimator(new DefaultItemAnimator());
        relatedVideosListView.setAdapter(relatedVideosAdapter);

        relatedVideosInfoList.clear();

        relatedVideosListView.addOnItemTouchListener(new RecyclerTouchListener(this, relatedVideosListView, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                VideoInfo videoInfo = relatedVideosInfoList.get(position);

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

        new getCategoryTopVideosInfoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://acropetal-gases.000webhostapp.com/app/category_top_videos_json.php?categoryId="+String.valueOf(categoryId));

        youTubeView = findViewById(R.id.youtube_view);
        initialiseYoutube();
    }

    public interface RecyclerViewItemClickListener {
        void onClick(View view, int position);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
//            youTubePlayer = player;
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            player.cueVideo(videoId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(getString(R.string.youtube_developer_key), this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    private void initialiseYoutube() {

        // Initializing video player with developer key
        youTubeView.initialize(getString(R.string.youtube_developer_key), this);

    }

    class getCategoryTopVideosInfoList extends AsyncTask<String,Void,String>
    {

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
                        if (!videoIdSet.contains(video_id)) {
                            videoIdSet.add(video_id);
                            relatedVideosInfoList.add(videoInfo);
                        }
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
            new getPlaylistVideosInfoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://acropetal-gases.000webhostapp.com/app/video_json.php?playlistId="+String.valueOf(playlistId));
        }
    }

    class getPlaylistVideosInfoList extends AsyncTask<String,Void,String>
    {

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
                        if (!videoIdSet.contains(video_id)) {
                            videoIdSet.add(video_id);
                            relatedVideosInfoList.add(videoInfo);
                        }
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
            relatedVideosAdapter.notifyDataSetChanged();
            relatedVideosRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

}
