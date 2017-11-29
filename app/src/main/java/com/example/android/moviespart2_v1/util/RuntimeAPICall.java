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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.moviespart2_v1.MainActivity;
import com.example.android.moviespart2_v1.MovieActivity;
import com.example.android.moviespart2_v1.VolleySingleton;
import com.example.android.moviespart2_v1.domain.MovieRuntime;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.id;
import static com.example.android.moviespart2_v1.MainActivity.requestQueue;
import static com.example.android.moviespart2_v1.R.id.runtime;

/**
 * Created by waiyi on 10/14/2017.
 */

public class RuntimeAPICall {

    private String TAG = "RuntimeAPICall";
    private String mId;
    private Context mContext;
    private String mRuntimeURL;
    private String sRuntime;
    private MovieRuntime movieRuntime;
    private TextView runtimeTxtview;
    private static final String TAG_RUNTIME = "runtime";
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY_PARAMETER = "?api_key=";
    private static final String VID_ENDPOINT = "/videos";
    private static final String myAPIKey = "18b233b28421e37df0172410e899946d";


    public RuntimeAPICall(Context context, String id, TextView tv) {
        mContext = context;
        mId = id;
        //runtimeTxtview = tv;
        buildURL();
    }

    private MovieRuntime getRuntime(JSONObject current) throws JSONException {
        MovieRuntime result = new MovieRuntime();
        int aRuntime = current.getInt("runtime");
        result.mRuntime = String.valueOf(aRuntime);
        return result;
    }

    //// TODO: 10/14/2017 rewrite code to use R.string.api_key

    private synchronized void buildURL() {
        mRuntimeURL = BASE_URL + mId + API_KEY_PARAMETER + myAPIKey;
        movieRuntime = new MovieRuntime();
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET, mRuntimeURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //MovieRuntime movieRuntime;
                        Log.d(TAG, response.toString());
                        try {
                            sRuntime = String.valueOf(response.getInt(TAG_RUNTIME));
                            Toast.makeText(mContext, "sRuntime = " + sRuntime, Toast.LENGTH_LONG).show();
                            runtimeTxtview.setText(sRuntime + " minutes");
                            //setsRuntime(sRuntime);


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
        //requestQueue.add(jsonObjRequest);
        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjRequest);

    }

//    public int getRuntime(){
//        Log.d(TAG, "runtime is " + mRuntime);
//        Toast.makeText(mContext, "I am in getRuntime() = " + mRuntime, Toast.LENGTH_LONG).show();
//        return mRuntime;
//    }

    public String getsRuntime(){
        return sRuntime;
    }

    public void setsRuntime(String min){
        movieRuntime.setmRuntime(min);
    }


}
