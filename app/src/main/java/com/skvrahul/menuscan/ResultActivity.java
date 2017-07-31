package com.skvrahul.menuscan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static android.R.attr.track;
import static android.R.attr.type;

public class ResultActivity extends AppCompatActivity {
    List<String> foods = new ArrayList<>();
    //ProgressDialog dialog;
    List<FoodItemModel> foodModels=new ArrayList<>();
    int requestPending= 0;
    RecyclerView foodsRecylerView;
    private FoodItemAdapter adapter;
    List<String> type_dict = new ArrayList<>();
    String TAG =  "ResultActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        foods = new ArrayList<>();
        foodModels=new ArrayList<>();
        foods.clear();
        foodModels.clear();
        initDict();
        setTitle("Dishes");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        foodsRecylerView = (RecyclerView) findViewById(R.id.foodItemRV);
        foods = getIntent().getStringArrayListExtra("foods");
        process();
    }
    @Override
    protected void onStop() {
        super.onStop();
        foods = new ArrayList<>();
        foodModels=new ArrayList<>();
        foods.clear();
        foodModels.clear();
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        foods = new ArrayList<>();
        foodModels=new ArrayList<>();
        foods.clear();
        foodModels.clear();
        adapter.notifyDataSetChanged();
    }

    private void process() {
        Log.i(TAG, "process: foods size"+foods.size());
        RequestQueue rQueue = Volley.newRequestQueue(ResultActivity.this);
        for(int i=0;i<foods.size();i++){

            String p =parseChars(foods.get(i));
            if(p.length()>3){
                String KGurl = "https://kgsearch.googleapis.com/v1/entities:search?query="+p+"&key=AIzaSyAKSXq2h_SdSyadKsBX1y3EChyaPv12W2E&limit=1&indent=True";
                Log.i( TAG, "URL"+KGurl);
                StringRequest request = new StringRequest(KGurl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        requestPending--;
                        Log.i(TAG, "requestsPending: "+requestPending);
                        parsGoogleJsonData(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
                        //dialog.dismiss();
                    }
                });

                requestPending++;
                Log.i(TAG, "requestsPending: "+requestPending);
                rQueue.add(request);
            }


        }

    }

    private void parsGoogleJsonData(String response) {
        try {
            JSONObject object = new JSONObject(response);
            JSONArray ja = object.getJSONArray("itemListElement");
            String title = ja.getJSONObject(0).getJSONObject("result").getString("name");
            String imUrl = ja.getJSONObject(0).getJSONObject("result").getJSONObject("image").getString("contentUrl");
            String desc =  ja.getJSONObject(0).getJSONObject("result").getJSONObject("detailedDescription").getString("articleBody");
            String type = "";

            try{
                type = ja.getJSONObject(0).getJSONObject("result").getString("description");
            }catch (Exception e){
                type = "none";
            }

            Log.i(TAG,"desc"+desc);
            Log.i(TAG,"Type"+type);

            final FoodItemModel fi = new FoodItemModel();
            fi.setDesc(desc);
            fi.setImgUrl(imUrl);
            fi.setTitle(title);
            fi.setType(type);
            String calURL = "https://api.nutritionix.com/v1_1/search/"+fi.getTitle()+"?fields=item_name%2Cnf_calories&appId=4fa3d6f4&appKey=a0bbc2ccb30f0a44b0ea41196b78ca9f";
            StringRequest calRequest = new StringRequest(calURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String calResponse) {
                    try{
                        JSONObject object = new JSONObject(calResponse);
                        JSONArray ja = object.getJSONArray("hits");
                        fi.setServing(ja.getJSONObject(0).getJSONObject("fields").getString("item_name"));
                        fi.setCalories(ja.getJSONObject(0).getJSONObject("fields").getString("nf_calories"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(isFood(fi)){
                        foodModels.add(fi);
                        if(requestPending==0){
                            //dialog.dismiss();
                            setupRecyclerView();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, "onErrorResponse: Error with fetching calories");
                }
            });
            RequestQueue rQueue = Volley.newRequestQueue(ResultActivity.this);
            rQueue.add(calRequest);
            Toast.makeText(getBaseContext(), "Found "+foodModels.size()+" dishes", Toast.LENGTH_SHORT);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "parseJsonData: "+response);
        }

    }
    private void setupRecyclerView(){
        if(foodModels.size()>0){
            Log.i(TAG,"SetupRV");
            adapter = new FoodItemAdapter(foodModels);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);

            foodsRecylerView.setLayoutManager(mLayoutManager);
            foodsRecylerView.setItemAnimator(new DefaultItemAnimator());
            foodsRecylerView.setAdapter(adapter);
        }else{
            Log.i(TAG,"size0");
        }
    }
    public boolean isFood(FoodItemModel fi){
        String type = fi.getType().toUpperCase();
        String desc = fi.getDesc().toUpperCase();
        for(String item:type_dict){
            if(type.contains(item) || desc.contains(item))
                return true;
        }
        return false;
    }
    public void initDict(){
        type_dict.add("CAKE");
        type_dict.add("DESERT");
        type_dict.add("DISH");
        type_dict.add("FOOD");
        type_dict.add("PASTRY");
        type_dict.add("BREAD");
        type_dict.add("SAUCE");
        type_dict.add("CURRY");
        type_dict.add("DELICACY");
        type_dict.add("DAIRY");
    }
    public String parseChars(String q){
        q =q.replace(' ','+');
        q =q.replace('&', '+');
        q =q.replaceAll("\\d","");
        return q;
    }

    public int genCals(){
        Random r = new Random();
        return r.nextInt(150)+250;
    }
}
