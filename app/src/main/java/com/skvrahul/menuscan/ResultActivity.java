package com.skvrahul.menuscan;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

public class ResultActivity extends AppCompatActivity {
    List<String> foods = new ArrayList<>();
    //ProgressDialog dialog;
    List<FoodItemModel> foodModels=new ArrayList<>();
    int requestPending= 0;
    RecyclerView foodsRecylerView;
    private FoodItemAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        foodsRecylerView = (RecyclerView) findViewById(R.id.foodItemRV);
        foods = getIntent().getStringArrayListExtra("foods");
        process();
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
            String url = "https://kgsearch.googleapis.com/v1/entities:search?query="+foods.get(i)+"&key=AIzaSyAKSXq2h_SdSyadKsBX1y3EChyaPv12W2E&limit=1&indent=True";
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
            Log.i("text",desc);
            FoodItemModel fi = new FoodItemModel();
            fi.setDesc(desc);
            fi.setImgUrl(imUrl);
            fi.setTitle(title);
            foodModels.add(fi);
            Toast.makeText(getBaseContext(), title, Toast.LENGTH_LONG);
        } catch (JSONException e) {
            e.printStackTrace();
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
}
