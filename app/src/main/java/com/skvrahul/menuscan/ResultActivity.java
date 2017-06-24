package com.skvrahul.menuscan;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    List<String> foods;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
    }

    private void process() {
        String url = "https://kgsearch.googleapis.com/v1/entities:search?query=gnocchi&key=AIzaSyAKSXq2h_SdSyadKsBX1y3EChyaPv12W2E&limit=1&indent=True";
        dialog = new ProgressDialog(getBaseContext());
        dialog.setMessage("Loading....");
        dialog.show();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJsonData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(ResultActivity.this);
        rQueue.add(request);
    }

    private void parseJsonData(String response) {
        try {
            JSONObject object = new JSONObject(response);
            JSONArray ja = object.getJSONArray("itemListElement");
            String title = ja.getJSONObject(0).getJSONObject("result").getString("name");
            Toast.makeText(getBaseContext(), title, Toast.LENGTH_LONG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.dismiss();
    }
}
