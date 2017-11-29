package com.example.android.moviespart2_v1.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.moviespart2_v1.Trailer;
import com.example.android.moviespart2_v1.TrailerRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import static com.example.android.moviespart2_v1.MainActivity.requestQueue;

/**
 * Created by waiyi on 10/14/2017.
 */

public class TrailerAPICall {

    private static final String forTest = "http://api.themoviedb.org/3/movie/321612/videos?api_key=18b233b28421e37df0172410e899946d";
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String ENDPOINT = "/videos";
    private static final String API_KEY_PARAMETER = "?api_key=";
    private static final String myAPIKey = "18b233b28421e37df0172410e899946d";
    private String TAG = "TrailerAPICall";
    private String movieID;
    private Context mContext;
    private String videoURL;
    private String trailerKey;
    private ArrayList<Trailer> trailerArrayList;
    private RecyclerView recyclerView;
    private int numOfKeys;

    public TrailerAPICall(Context context, String id, RecyclerView rvTrailer) {
        mContext = context;
        movieID = id;
        recyclerView = rvTrailer;
        requestJSONObj(buildURL());
    }

    private String buildURL() {
        videoURL = BASE_URL + movieID + ENDPOINT + API_KEY_PARAMETER + myAPIKey;
        Toast.makeText(mContext, "videoURL=" + videoURL, Toast.LENGTH_LONG).show();
        return videoURL;
    }

    private synchronized void requestJSONObj(String mURL) {

        trailerArrayList = new ArrayList<Trailer>();
        numOfKeys = 0;

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET, mURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            JSONArray arr = obj.getJSONArray("results");
                            Log.d(TAG, arr.toString());
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject videoResults = arr.getJSONObject(i);
                                if (videoResults.getString("type") == "Trailer") {
                                    trailerKey = videoResults.getString("key");
                                    Trailer aTrailer = new Trailer(trailerKey);
                                    trailerArrayList.add(aTrailer);
                                    //numOfKeys++;
                                    //keys.add(videoResults.getString("key"));
                                }
                            }
                            TrailerRecyclerAdapter mTrailerRAdapter = new TrailerRecyclerAdapter(trailerArrayList);
                            recyclerView.setAdapter(mTrailerRAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError || error instanceof NetworkError
                        || error instanceof TimeoutError) {


                    ContextThemeWrapper ctw = new ContextThemeWrapper(mContext,
                            com.example.android.moviespart2_v1.R.style.AlertDialogCustom);

                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                    alertDialogBuilder.setTitle("Network Communication Error");
                    alertDialogBuilder
                            .setMessage("Please check your Internet connection.")
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(mContext, "OK button clicked", Toast.LENGTH_LONG).show();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);

                } else if (error instanceof ParseError) {
                    Toast.makeText(mContext, "Parse error!",
                            Toast.LENGTH_LONG).show();
                }

                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        requestQueue.add(jsonObjRequest);

    }


}
