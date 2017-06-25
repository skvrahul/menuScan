package com.skvrahul.menuscan;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

/**
 * Created by skvrahul on 25/6/17.
 */

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.MyViewHolder> {
    private List<FoodItemModel> foods;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTV, caloriesTV,descSV;
        public ImageView imageIV;
        public MyViewHolder(View view) {
            super(view);
            titleTV = (TextView) view.findViewById(R.id.titleTV);
            caloriesTV = (TextView) view.findViewById(R.id.caloriesTV);
            descSV = (TextView) view.findViewById(R.id.descSV);
            imageIV = (ImageView) view.findViewById(R.id.imageIV);
        }
    }
    public FoodItemAdapter(List<FoodItemModel> foods){
        this.foods = foods;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FoodItemModel foodItem = foods.get(position);
        holder.titleTV.setText(foodItem.getTitle());
        holder.caloriesTV.setText(foodItem.getCalories()+" ");
        holder.descSV.setText(foodItem.getDesc());
        Log.i("picasso", "onBindViewHolder: "+foodItem.getImgUrl());
        Picasso.with(holder.imageIV.getContext()).load(foodItem.getImgUrl()).into(holder.imageIV);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_item_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }
    public int genCals(){
        Random r = new Random();
        return r.nextInt(400);
    }
}
