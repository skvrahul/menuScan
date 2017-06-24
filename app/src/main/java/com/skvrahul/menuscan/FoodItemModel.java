package com.skvrahul.menuscan;

import java.util.List;

/**
 * Created by skvrahul on 25/6/17.
 */

class FoodItemModel{
    private String Title;
    private String ImgUrl;
    private String Desc;
    int calories;
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

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
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
