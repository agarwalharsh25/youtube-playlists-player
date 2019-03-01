package com.example.temp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class HomeFragment extends Fragment {

    List<VideoInfo> popularVideosInfoList = new ArrayList<>(), recentlyAddedInfoList = new ArrayList<>();
    List<PlaylistInfo> trendingPlaylistsInfoList = new ArrayList<>();
    RecyclerView popularVideosListView, trendingPlaylistsListView, recentlyAddedListView;
    ListAdapter popularVideosAdapter, trendingPlaylistsAdapter, recentlyAddedAdapter;
    RelativeLayout popularVideosRelativeLayout, trendingPlaylistsRelativeLayout, recentlyAddedRelativeLayout;
    ProgressBar progressBar;

    List<CategoryInfo> categoryInfoList = new ArrayList<>();
    final List<ListAdapter> playlistAdapterArray = new ArrayList<>();
    final List<List<PlaylistInfo>> playlistInfoListArray = new ArrayList<>();
    RelativeLayout relativeView;
    final List<RecyclerView> categories = new ArrayList<>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        popularVideosListView = view.findViewById(R.id.popularVideosListView);
        trendingPlaylistsListView = view.findViewById(R.id.trendingPlaylistsListView);
        recentlyAddedListView = view.findViewById(R.id.recentlyAddedListView);

        progressBar = view.findViewById(R.id.progressBar);

        popularVideosRelativeLayout = view.findViewById(R.id.popularVideosRelativeLayout);
        trendingPlaylistsRelativeLayout = view.findViewById(R.id.trendingPlaylistsRelativeLayout);
        recentlyAddedRelativeLayout = view.findViewById(R.id.recentlyAddedRelativeLayout);

        popularVideosAdapter = new ListAdapter(popularVideosInfoList, getContext(), null);
        RecyclerView.LayoutManager popularVideosLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        popularVideosListView.setLayoutManager(popularVideosLayoutManager);
        popularVideosListView.setItemAnimator(new DefaultItemAnimator());
        popularVideosListView.setAdapter(popularVideosAdapter);

        trendingPlaylistsAdapter = new ListAdapter(trendingPlaylistsInfoList, getContext(), null);
        RecyclerView.LayoutManager trendingPlaylistsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        trendingPlaylistsListView.setLayoutManager(trendingPlaylistsLayoutManager);
        trendingPlaylistsListView.setItemAnimator(new DefaultItemAnimator());
        trendingPlaylistsListView.setAdapter(trendingPlaylistsAdapter);

        recentlyAddedAdapter = new ListAdapter(recentlyAddedInfoList, getContext(), null);
        RecyclerView.LayoutManager recentlyAddedLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recentlyAddedListView.setLayoutManager(recentlyAddedLayoutManager);
        recentlyAddedListView.setItemAnimator(new DefaultItemAnimator());
        recentlyAddedListView.setAdapter(recentlyAddedAdapter);

        popularVideosInfoList.clear();
        trendingPlaylistsInfoList.clear();
        recentlyAddedInfoList.clear();

        relativeView = view.findViewById(R.id.relativeView);

        popularVideosListView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), popularVideosListView, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                VideoInfo videoInfo = popularVideosInfoList.get(position);

                if (MainActivity.getInstance().checkConnection()) {
                    Intent i = new Intent(getContext(), VideoPlayActivity.class);
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

        trendingPlaylistsListView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), trendingPlaylistsListView, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                PlaylistInfo playlistInfo = trendingPlaylistsInfoList.get(position);

                if (MainActivity.getInstance().checkConnection()) {
                    Intent i = new Intent(getContext(), VideoPlaylistActivity.class);
                    i.putExtra("playlistId", playlistInfo.getPlaylistId());
                    i.putExtra("playlistImage", playlistInfo.getPlaylistImage());
                    i.putExtra("playlistName", playlistInfo.getPlaylistName());
                    i.putExtra("categoryId", playlistInfo.getCategoryId());
                    i.putExtra("categoryName", playlistInfo.getCategoryName());
                    startActivity(i);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(MainActivity.getInstance().findViewById(R.id.mainLayout), "Can't Connect to Internet. Try Again Later.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

            }
        }));

        recentlyAddedListView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recentlyAddedListView, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                VideoInfo videoInfo = recentlyAddedInfoList.get(position);

                if (MainActivity.getInstance().checkConnection()) {
                    Intent i = new Intent(getContext(), VideoPlayActivity.class);
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

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                popularVideosInfoList.clear();
                popularVideosRelativeLayout.setVisibility(View.GONE);
                recentlyAddedInfoList.clear();
                recentlyAddedRelativeLayout.setVisibility(View.GONE);
                trendingPlaylistsInfoList.clear();
                trendingPlaylistsRelativeLayout.setVisibility(View.GONE);
                categoryInfoList.clear();
                playlistAdapterArray.clear();
                playlistInfoListArray.clear();
                categories.clear();
                relativeView.setVisibility(View.GONE);
                relativeView.removeAllViews();
                new getPopularVideosInfoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://acropetal-gases.000webhostapp.com/app/top_videos_json.php");
                new getTrendingPlaylistsInfoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://acropetal-gases.000webhostapp.com/app/trending_playlists_json.php");
                new getRecentlyAddedInfoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://acropetal-gases.000webhostapp.com/app/recently_added_json.php");
                new getCategoryInfoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://acropetal-gases.000webhostapp.com/app/category_json.php");
                pullToRefresh.setRefreshing(false);
            }
        });

        new getPopularVideosInfoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://acropetal-gases.000webhostapp.com/app/top_videos_json.php");
        new getTrendingPlaylistsInfoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://acropetal-gases.000webhostapp.com/app/trending_playlists_json.php");
        new getRecentlyAddedInfoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://acropetal-gases.000webhostapp.com/app/recently_added_json.php");
        new getCategoryInfoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://acropetal-gases.000webhostapp.com/app/category_json.php");

    }

    public interface RecyclerViewItemClickListener {
        void onClick(View view, int position);
    }

    class getPopularVideosInfoList extends AsyncTask<String,Void,String>
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
                        VideoInfo videoInfo = new VideoInfo(1, video_id, title, playlistId, description, publishDate, views, thumbnail, playlistName, playlistImage, categoryId, categoryName);
                        popularVideosInfoList.add(videoInfo);
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
            popularVideosAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            popularVideosRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    class getTrendingPlaylistsInfoList extends AsyncTask<String,Void,String>
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
                    JSONArray jsonArray = jsonObject.getJSONArray("playlists");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        Integer playlistId = c.getInt("id");
                        String playlistName = c.getString("playlistName");
                        String playlistImage = c.getString("playlistImage");
                        Integer categoryId = c.getInt("categoryId");
                        String categoryName = c.getString("categoryName");
                        PlaylistInfo playlistInfo = new PlaylistInfo(2, playlistId, playlistName, playlistImage, categoryId, categoryName);
                        trendingPlaylistsInfoList.add(playlistInfo);
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
            trendingPlaylistsAdapter.notifyDataSetChanged();
            trendingPlaylistsRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    class getRecentlyAddedInfoList extends AsyncTask<String,Void,String>
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
                        VideoInfo videoInfo = new VideoInfo(2, video_id, title, playlistId, description, publishDate, views, thumbnail, playlistName, playlistImage, categoryId, categoryName);
                        recentlyAddedInfoList.add(videoInfo);
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
            recentlyAddedAdapter.notifyDataSetChanged();
            recentlyAddedRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    void call() {
        if (getContext()!=null) {
            for (int i = 0; i < categoryInfoList.size(); i++) {
                final int j = i;
                List<PlaylistInfo> playlistInfoList = new ArrayList<>();
                playlistInfoListArray.add(playlistInfoList);
                playlistAdapterArray.add(new ListAdapter(playlistInfoList, getContext(), null));
                TextView t1 = new TextView(getContext());
                t1.setId(categoryInfoList.size() + i);
                t1.setText(categoryInfoList.get(i).getCategoryName());
                t1.setPadding(0, 36, 0, 0);
                t1.setTextColor(getResources().getColor(R.color.colorTextColor));
                t1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                t1.setTypeface(ResourcesCompat.getFont(getContext(), R.font.lato));
                RelativeLayout.LayoutParams t1params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (i > 0) {
                    t1params.addRule(RelativeLayout.BELOW, i - 1);
                } else {
                    t1params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                }
                t1params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                t1.setLayoutParams(t1params);
                relativeView.addView(t1);

                RecyclerView r = new RecyclerView(getContext());
                r.setId(i);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            r.setId(getResources().getIdentifier(languages.get(i)+"View", "id", "com.example.avinash.com.prasarbharti.parsarbharti.airnews.com.avinash.parsarbharti.airnews.parsarbharti.airnews.com.avinash.parsarbharti.airnews.parsarbharti.airnews.testairnews2"));
                params.addRule(RelativeLayout.BELOW, categoryInfoList.size() + i);
//            params.addRule(RelativeLayout.BELOW,getResources().getIdentifier(languages.get(i-1)+"View", "id", "com.example.avinash.com.prasarbharti.parsarbharti.airnews.com.avinash.parsarbharti.airnews.parsarbharti.airnews.com.avinash.parsarbharti.airnews.parsarbharti.airnews.testairnews2"));
                r.setLayoutParams(params);
                relativeView.addView(r);
                categories.add(r);

//            TextView t2 = new TextView(getContext());
////            t2.setId(languages.size()+languages.size()+i);
//            t2.setText("More ");
////            t2.setPadding(0,36,0,0);
//            t2.setTextColor(getResources().getColor(R.color.colorTextColor));
//            t2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//            RelativeLayout.LayoutParams t2params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            if (i>0) {
//                t2params.addRule(RelativeLayout.ABOVE,i);
//            } else {
//                t2params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                t2.setVisibility(View.GONE);
//            }
//
//            t2params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            t2.setLayoutParams(t2params);
//            relativeView.addView(t2);
//            t2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(getContext(), VideoChannelActivity.class);
//                    i.putExtra("cid", titleArray.get(j).getCategory());
//                    i.putExtra("channel", titleArray.get(j).getName());
//                    startActivity(i);
//                }
//            });

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                categories.get(i).setLayoutManager(mLayoutManager);
                categories.get(i).setItemAnimator(new DefaultItemAnimator());
                categories.get(i).setAdapter(playlistAdapterArray.get(i));

                categories.get(i).addOnItemTouchListener(new RecyclerTouchListener(getContext(), categories.get(i), new RecyclerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        PlaylistInfo playlistInfo = playlistInfoListArray.get(j).get(position);
                        if (MainActivity.getInstance().checkConnection()) {
                            Intent i = new Intent(getContext(), VideoPlaylistActivity.class);
                            i.putExtra("playlistId", playlistInfo.getPlaylistId());
                            i.putExtra("playlistImage", playlistInfo.getPlaylistImage());
                            i.putExtra("playlistName", playlistInfo.getPlaylistName());
                            i.putExtra("categoryId", playlistInfo.getCategoryId());
                            i.putExtra("categoryName", playlistInfo.getCategoryName());
                            startActivity(i);
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(MainActivity.getInstance().findViewById(R.id.mainLayout), "Can't Connect to Internet. Try Again Later.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }

                    }
                }));


                playlistInfoListArray.get(i).clear();
                if (categoryInfoList.get(i).getCategoryId() != null) {
                    new getPlaylistsInfoList(playlistInfoListArray.get(i), playlistAdapterArray.get(i)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://acropetal-gases.000webhostapp.com/app/playlist_json.php?cat=" + String.valueOf(categoryInfoList.get(i).getCategoryId()));
                }
                playlistAdapterArray.get(i).notifyDataSetChanged();
            }
        }
    }

    class getCategoryInfoList extends AsyncTask<String,Void,String>
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

                try {

                    JSONObject jsonObject = new JSONObject(sb.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("categories");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        Integer categoryId = c.getInt("id");
                        String categoryName = c.getString("category");
                        if (i == 0)
                            categoryInfoList.add(new CategoryInfo(2, null, null));
                        CategoryInfo categoryInfo = new CategoryInfo(2, categoryId, categoryName);
                        categoryInfoList.add(categoryInfo);
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
            call();
        }
    }

    class getPlaylistsInfoList extends AsyncTask<String,Void,String>
    {
        private List<PlaylistInfo> playlistInfoList;
        private ListAdapter playlistAdapter;

        private getPlaylistsInfoList(List<PlaylistInfo> playlistInfoList, ListAdapter playlistAdapter) {
            this.playlistInfoList = playlistInfoList;
            this.playlistAdapter = playlistAdapter;
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
                    JSONArray jsonArray = jsonObject.getJSONArray("playlists");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        Integer playlistId = c.getInt("id");
                        String playlistName = c.getString("playlistName");
                        String playlistImage = c.getString("playlistImage");
                        Integer categoryId = c.getInt("categoryId");
                        String categoryName = c.getString("categoryName");
                        PlaylistInfo playlistInfo = new PlaylistInfo(2, playlistId, playlistName, playlistImage, categoryId, categoryName);
                        playlistInfoList.add(playlistInfo);
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
            relativeView.setVisibility(View.VISIBLE);
            playlistAdapter.notifyDataSetChanged();
        }
    }

}