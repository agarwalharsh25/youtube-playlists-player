package com.example.temp;

public class VideoInfo {

    public int id;
    public String videoId;
    public String title;
    public Integer playlistId;
    public String description;
    public String publishDate;
    public Integer views;
    public String thumbnail;
    public String playlistName;
    public String playlistImage;
    public Integer categoryId;
    public String categoryName;

    public VideoInfo(int id, String videoId, String title, Integer playlistId, String description, String publishDate, Integer views, String thumbnail, String playlistName, String playlistImage, Integer categoryId, String categoryName)
    {
        this.id = id;
        this.videoId = videoId;
        this.title = title;
        this.playlistId = playlistId;
        this.description = description;
        this.publishDate = publishDate;
        this.views = views;
        this.thumbnail = thumbnail;
        this.playlistName = playlistName;
        this.playlistImage = playlistImage;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getId()
    {
        return id;
    }

    public String getVideoId()
    {
        return videoId;
    }

    public String getTitle()
    {
        return title;
    }

    public Integer getPlaylistId()
    {
        return playlistId;
    }

    public String getDescription()
    {
        return description;
    }

    public String getPublishDate()
    {
        return publishDate;
    }

    public Integer getViews() {
        return views;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getPlaylistName()
    {
        return playlistName;
    }

    public String getPlaylistImage()
    {
        return playlistImage;
    }

    public Integer getCategoryId()
    {
        return categoryId;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

}
