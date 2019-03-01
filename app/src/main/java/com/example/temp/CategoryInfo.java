package com.example.temp;

public class CategoryInfo {

    public int id;
    public Integer categoryId;
    public String categoryName;

    public CategoryInfo(int id, Integer categoryId, String categoryName)
    {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getId()
    {
        return id;
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