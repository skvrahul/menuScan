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
import java.util.List;
import java.util.Random;

public class ResultActivity extends AppCompatActivity {
    List<String> foods = new ArrayList<>();
    //ProgressDialog dialog;
    List<FoodItemModel> foodModels=new ArrayList<>();
    int requestPending= 0;
    RecyclerView foodsRecylerView;
    private FoodItemAdapter adapter;
    List<String> type_dict = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    }
    private void process() {
        Log.i("text", "process: called.foods size"+foods.size());
        RequestQueue rQueue = Volley.newRequestQueue(ResultActivity.this);
//        dialog = new ProgressDialog(getBaseContext());
//        dialog.setMessage("Loading....");
//        dialog.show();
        for(int i=0;i<foods.size();i++){
//            Uri.Builder builder = new Uri.Builder();
//            builder.scheme("https")
//                    .authority("kgsearch.googleapis.com")
//                    .appendPath("v1")
//                    .appendPath("entities:search")
//                    .appendQueryParameter("query", foods.get(i))
//                    .appendQueryParameter("key", "AIzaSyAKSXq2h_SdSyadKsBX1y3EChyaPv12W2E")
//                    .appendQueryParameter("limit","1");
//            String url = builder.build().toString();
            String url = "https://kgsearch.googleapis.com/v1/entities:search?query="+parseChars(foods.get(i))+"&key=AIzaSyAKSXq2h_SdSyadKsBX1y3EChyaPv12W2E&limit=1&indent=True";
            Log.i("URL", url);
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    requestPending--;
                    Log.i("text", "rp: "+requestPending);
                    parseJsonData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
                    //dialog.dismiss();
                }
            });
            requestPending++;
            Log.i("text", "rp: "+requestPending);
            rQueue.add(request);

        }
        //String url = "to

    }

    private void parseJsonData(String response) {
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

            Log.i("text",desc);
            FoodItemModel fi = new FoodItemModel();
            fi.setDesc(desc);
            fi.setImgUrl(imUrl);
            fi.setTitle(title);
            fi.setType(type);
            fi.setCalories(genCals());
            if(isFood(type)){
                foodModels.add(fi);
            }
            Toast.makeText(getBaseContext(), "Found "+foodModels.size()+" dishes", Toast.LENGTH_SHORT);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("Exception", "parseJsonData: "+response);
        }
        if(requestPending==0){
            //dialog.dismiss();
            setupRecyclerView();
        }
    }
    private void setupRecyclerView(){
        if(foodModels.size()>0){
            Log.i("text","SetupRV");
            adapter = new FoodItemAdapter(foodModels);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,true);

            foodsRecylerView.setLayoutManager(mLayoutManager);
            foodsRecylerView.setItemAnimator(new DefaultItemAnimator());
            foodsRecylerView.setAdapter(adapter);
        }else{
            Log.i("text","size0");
        }
    }
    public boolean isFood(String type){
        type = type.toUpperCase();
        for(String item:type_dict){
            if(item.equals(type))
                return true;
        }
        return false;
    }
    public void initDict(){
        type_dict.add("CAKE");
        type_dict.add("DESERT");
        type_dict.add("DISH");
        type_dict.add("FOOD");
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
