package com.skvrahul.menuscan;

import java.util.List;

/**
 * Created by skvrahul on 25/6/17.
 */

class FoodItemModel{
    private String Title;
    private String ImgUrl;
    private String Desc;
    private String Type;
    private String calories;
    private String serving;

    public String getType() {
        return Type;
    }

    public String getServing() {
        return serving;
    }

    public void setServing(String serving) {
        this.serving = serving;
    }

    public void setType(String type) {
        Type = type;
    }

    List<String> ingredients;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void FoodItemModel(){

    }
}
