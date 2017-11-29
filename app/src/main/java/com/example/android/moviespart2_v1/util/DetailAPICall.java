package com.example.android.moviespart2_v1.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
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
import com.example.android.moviespart2_v1.Review;
import com.example.android.moviespart2_v1.ReviewRecyclerAdapter;
import com.example.android.moviespart2_v1.Trailer;
import com.example.android.moviespart2_v1.TrailerRecyclerAdapter;
import com.example.android.moviespart2_v1.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.android.moviespart2_v1.R.id.recyclerView;

/**
 * Created by waiyi on 11/26/2017.
 */

public class DetailAPICall {
    private String TAG = "DetailAPICall";
    private String movieID;
    private String mRuntimeURL;
    private String mTrailerURL;
    private String mReviewURL;
    private String sRuntime;
    private TextView runtimeTxtview;
    private RecyclerView trailerRV;
    private RecyclerView reviewRV;
    private Context mContext;
    private ArrayList<Trailer> trailerArrayList;
    private ArrayList<Review> contentArrayList;
    private String trailerKey;
    private TrailerRecyclerAdapter mTrailerRAdapter;
    private ReviewRecyclerAdapter mReviewRAdapter;

    private static final String TAG_RUNTIME = "runtime";
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY_PARAMETER = "?api_key=";
    private static final String VID_ENDPOINT = "/videos";
    private static final String REVIEW_ENDPOINT = "/reviews";
    private static final String myAPIKey = "18b233b28421e37df0172410e899946d";

    public DetailAPICall(Context context, String movieid, TextView tv, RecyclerView rv, RecyclerView
            rvReview) {
        mContext = context;
        movieID = movieid;
        runtimeTxtview = tv;
        trailerRV = rv;
        reviewRV = rvReview;
        buildRuntimeURL();
        buildTrailerURL();
        buildReviewURL();
        callRuntimeAPI();
        callTrailerAPI();
        callReviewAPI();
    }

    private void buildRuntimeURL() {
        mRuntimeURL = BASE_URL + movieID + API_KEY_PARAMETER + myAPIKey;
    }

    private void buildTrailerURL() {
        mTrailerURL = BASE_URL + movieID + VID_ENDPOINT + API_KEY_PARAMETER + myAPIKey;
    }

    private void buildReviewURL() {
        mReviewURL = BASE_URL + movieID + REVIEW_ENDPOINT + API_KEY_PARAMETER + myAPIKey;
        Log.d(TAG, "mReviewURL=" + mReviewURL);
    }

    public void callRuntimeAPI() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, mRuntimeURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            sRuntime = String.valueOf(response.getInt(TAG_RUNTIME));
                            Toast.makeText(mContext, "sRuntime = " + sRuntime, Toast.LENGTH_LONG).show();
                            runtimeTxtview.setText(sRuntime + " minutes");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override

            public void onErrorResponse(VolleyError vError) {
                errorResponse(vError);

            }
        });

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);

    }

    public void callTrailerAPI() {

        trailerArrayList = new ArrayList<Trailer>();

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET, mTrailerURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            JSONArray arr = obj.getJSONArray("results");
                            Log.d(TAG, arr.toString());
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject videoResults = arr.getJSONObject(i);
                                if (videoResults.getString("type").equals("Trailer")) {
                                    trailerKey = videoResults.getString("key");
                                    Log.v(TAG, "trailerKey=" + trailerKey);
                                    Trailer aTrailer = new Trailer(trailerKey);
                                    trailerArrayList.add(aTrailer);
                                }
                            }
                            mTrailerRAdapter = new TrailerRecyclerAdapter(trailerArrayList);
                            trailerRV.setAdapter(mTrailerRAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                errorResponse(volleyError);
            }
        });
        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjRequest);
    }



    public void callReviewAPI() {

        contentArrayList = new ArrayList<Review>();

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, mReviewURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            JSONArray arr = obj.getJSONArray("results");
                            Log.d(TAG, arr.toString());
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject reviewResults = arr.getJSONObject(i);
                                String aContent = reviewResults.getString("content");
                                Log.d(TAG, "aContent = " + aContent);
                                Review aReview = new Review(aContent);
                                contentArrayList.add(aReview);
                                //String aContent = reviewResults.getString("content");
                                //Log.d(TAG, "aContent=" + aContent);
                                //contentArrayList.add(aContent);
                            }
                            mReviewRAdapter = new ReviewRecyclerAdapter(contentArrayList);
                            reviewRV.setAdapter(mReviewRAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError vErr) {
                errorResponse(vErr);
            }

        });
        VolleySingleton.getInstance(mContext).addToRequestQueue(objectRequest);

    }

    public void errorResponse(VolleyError error) {
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
}
