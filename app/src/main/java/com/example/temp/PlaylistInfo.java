package com.example.temp;

public class PlaylistInfo {

    public int id;
    public Integer playlistId;
    public String playlistName;
    public String playlistImage;
    public Integer categoryId;
    public String categoryName;

    public PlaylistInfo(int id, Integer playlistId, String playlistName, String playlistImage, Integer categoryId, String categoryName)
    {
        this.id = id;
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.playlistImage = playlistImage;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getId()
    {
        return id;
    }

    public Integer getPlaylistId()
    {
        return playlistId;
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
