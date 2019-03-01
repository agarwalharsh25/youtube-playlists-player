package com.example.temp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.temp.RoundedCornersTransform.CornerType.LEFT;
import static com.example.temp.RoundedCornersTransform.CornerType.TOP;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<VideoInfo> videoInfoList = null;
    private List<PlaylistInfo> playlistInfoList = null;
    private Context context = null;

    public ListAdapter(List<VideoInfo> videoInfoList, Context context, VideoInfo videoInfo) {
        this.videoInfoList = videoInfoList;
        this.context = context;
    }

    public ListAdapter(List<PlaylistInfo> playlistInfoList, Context context, PlaylistInfo playlistInfo) {
        this.playlistInfoList = playlistInfoList;
        this.context = context;
    }

    private static class FullWidthImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView fullWidthImage;

        private FullWidthImageViewHolder(View view) {
            super(view);
            fullWidthImage = view.findViewById(R.id.fullWidthImage);
        }
    }

    private static class ImageTileViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageTile;
        private TextView title;

        private ImageTileViewHolder(View view) {
            super(view);
            imageTile = view.findViewById(R.id.imageTile);
            title = view.findViewById(R.id.title);
        }
    }

    private static class VideoInfoTileViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageTile;
        private TextView title, playlist, views, description;

        private VideoInfoTileViewHolder(View view) {
            super(view);
            imageTile = view.findViewById(R.id.imageTile);
            title = view.findViewById(R.id.title);
            playlist = view.findViewById(R.id.playlist);
            views = view.findViewById(R.id.views);
            description = view.findViewById(R.id.description);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==1) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.full_width_image, parent, false);
            return new FullWidthImageViewHolder(itemView);
        } else if(viewType==2) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_tile, parent, false);
            return new ImageTileViewHolder(itemView);
        } else if(viewType==3) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.video_info_tile, parent, false);
            return new VideoInfoTileViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if(viewType==1) {
            if (videoInfoList!=null&&videoInfoList.get(position).getThumbnail()!=null&&!videoInfoList.get(position).getThumbnail().isEmpty())
                Picasso.get().load(videoInfoList.get(position).getThumbnail()).fit().into(((FullWidthImageViewHolder) holder).fullWidthImage);
        } else if(viewType==2) {
            if (playlistInfoList!=null&&playlistInfoList.get(position).getPlaylistImage()!=null&&!playlistInfoList.get(position).getPlaylistImage().isEmpty()) {
                Picasso.get().load(playlistInfoList.get(position).getPlaylistImage()).fit().transform(new RoundedCornersTransform(12, 0, TOP)).into(((ImageTileViewHolder) holder).imageTile);
                ((ImageTileViewHolder)holder).title.setText(playlistInfoList.get(position).getPlaylistName());
            } else if (videoInfoList!=null&&videoInfoList.get(position).getThumbnail()!=null&&!videoInfoList.get(position).getThumbnail().isEmpty()) {
                Picasso.get().load(videoInfoList.get(position).getThumbnail()).fit().transform(new RoundedCornersTransform(12, 0, TOP)).into(((ImageTileViewHolder) holder).imageTile);
                ((ImageTileViewHolder)holder).title.setText(videoInfoList.get(position).getTitle());
            }
        } else if(viewType==3) {
            if (videoInfoList!=null&&videoInfoList.get(position).getThumbnail()!=null&&!videoInfoList.get(position).getThumbnail().isEmpty()) {
                Picasso.get().load(videoInfoList.get(position).getThumbnail()).fit().transform(new RoundedCornersTransform(12, 0, LEFT)).into(((VideoInfoTileViewHolder) holder).imageTile);
                ((VideoInfoTileViewHolder)holder).title.setText(videoInfoList.get(position).getTitle());
                ((VideoInfoTileViewHolder)holder).playlist.setText(videoInfoList.get(position).getPlaylistName());
                String views = String.valueOf(videoInfoList.get(position).getViews());
                if (videoInfoList.get(position).getViews()!=1)
                    views = views + " views";
                else
                    views = views + " view";
                ((VideoInfoTileViewHolder)holder).views.setText(views);
                ((VideoInfoTileViewHolder)holder).description.setText(Html.fromHtml(videoInfoList.get(position).getDescription()).toString());
                ((VideoInfoTileViewHolder)holder).description.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (videoInfoList!=null)
            return videoInfoList.size();
        return playlistInfoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (videoInfoList!=null)
            return videoInfoList.get(position).getId();
        return playlistInfoList.get(position).getId();
    }

}