package com.example.android.moviespart2_v1;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This project uses Volley for network requests and
 * Recyclerview GridlayoutManager to lay out poster images.
 * To run this application, please specify your own API key in
 * strings.xml file.
 */

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private ArrayList<Movie> mPosterImages;
    private RecyclerAdapter mAdapter;
    private Context mContext;
    private ImageView mItemImage;
    private Activity mActivity;
    public static RequestQueue requestQueue;

    private static final String BASE_URL = "http://api.themoviedb.org/3";
    private static final String POP_ENDPOINT = "/movie/popular";
    private static final String TOP_RATED_ENDPOINT = "/movie/top_rated";
    private static final String API_KEY_PARAMETER = "?api_key=";
    private String mPopURL;
    private String mTopRatedURL;

    private String TAG = "MainActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.example.android.moviespart2_v1.R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.moviespart2_v1.R.layout.activity_main);

        mContext = getApplicationContext();
        mActivity = MainActivity.this;

        mRecyclerView = (RecyclerView) findViewById(com.example.android.moviespart2_v1.R.id.recyclerView);
        mItemImage = (ImageView) findViewById(com.example.android.moviespart2_v1.R.id.item_image);

        mPopURL = BASE_URL + POP_ENDPOINT + API_KEY_PARAMETER +
                mContext.getString(com.example.android.moviespart2_v1.R.string.api_key);
        mTopRatedURL = BASE_URL + TOP_RATED_ENDPOINT + API_KEY_PARAMETER +
                mContext.getString(com.example.android.moviespart2_v1.R.string.api_key);

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        volleyJsonObjectRequest(mPopURL);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case com.example.android.moviespart2_v1.R.id.sortby_highestrated:
                volleyJsonObjectRequest(mTopRatedURL);
                break;
            case com.example.android.moviespart2_v1.R.id.sortby_popularity:
                volleyJsonObjectRequest(mPopURL);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void volleyJsonObjectRequest(String mURL) {

        mGridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mPosterImages = new ArrayList<>();

        final Context context = this;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, mURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, response.toString());
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            JSONArray arr = obj.getJSONArray("results");
                            Log.d(TAG, arr.toString());
                            //loop thru the JSONArray "results"
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject movieResults = arr.getJSONObject(i);
                                Movie mMovie = new Movie(movieResults);
                                mPosterImages.add(mMovie);
                            }
                            mAdapter = new RecyclerAdapter(mPosterImages);
                            mRecyclerView.setAdapter(mAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError || error instanceof NetworkError
                        || error instanceof TimeoutError) {


                    ContextThemeWrapper ctw = new ContextThemeWrapper(context,
                            com.example.android.moviespart2_v1.R.style.AlertDialogCustom);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                    alertDialogBuilder.setTitle("Network Communication Error");
                    alertDialogBuilder
                            .setMessage("Please check your Internet connection.")
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.this.finish();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);

                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Parse error!",
                            Toast.LENGTH_LONG).show();
                }

                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });


        requestQueue.add(jsonObjectRequest);


    }
}

