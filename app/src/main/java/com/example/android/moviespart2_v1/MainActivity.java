package com.example.android.moviespart2_v1;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.example.android.moviespart2_v1.data.FavMoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.defaultHeight;
import static android.R.attr.id;
import static android.icu.lang.UCharacter.JoiningGroup.E;
import static android.media.CamcorderProfile.get;

/**
 * This project uses Volley for network requests and
 * Recyclerview GridlayoutManager for displaying the poster images.
 * The implementation of this app uses multiple activities: MainActivity is used to
 * display popular and top rated sorts. MovieActivity is used to display the movie details screen.
 * FMovieActivity is used to display user's favorites movies, and FavoriteMovieActivity displays
 * the details of the favorite movie. OfflineActivity is called from onClick() in OfflineRecyclerAdapter
 * when there is no network connectivity detected and will render the details of the tapped movie
 * based on the data in the database.
 *
 * Using multiple activities to implement this app is far from ideal
 * as it results in a fair amount of redundant code. It is the author's intention to redesign the app
 * in the future using fragments and broadcast receivers.
 *
 * App tested mainly with Pixel API 25.
 *
 * To run this application, please specify your own API key in <string name="api_key"> in
 * res/values/strings.xml.
 */

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private ArrayList<Movie> mPosterImages = new ArrayList<>();
    private RecyclerAdapter mAdapter;
    private Context mContext;
    private ImageView mItemImage;
    private Activity mActivity;
    public static RequestQueue requestQueue;

    private static final String BASE_URL = "http://api.themoviedb.org/3";
    private static final String POP_ENDPOINT = "/movie/popular";
    private static final String TOP_RATED_ENDPOINT = "/movie/top_rated";
    private static final String FAV_ENDPOINT = "/movie/";
    private static final String API_KEY_PARAMETER = "?api_key=";
    private static String MY_API_KEY;
    private static String mPopURL;
    private static String mTopRatedURL;

    private static final String TAG = "MainActivity";

    /* Constant values for the names of each respective lifecycle callback */
    private static final String ON_CREATE = "onCreate";
    private static final String ON_START = "onStart";
    private static final String ON_RESUME = "onResume";
    private static final String ON_PAUSE = "onPause";
    private static final String ON_STOP = "onStop";
    private static final String ON_RESTART = "onRestart";
    private static final String ON_DESTROY = "onDestroy";
    private static final String ON_SAVE_INSTANCE_STATE = "onSaveInstanceState";

    private boolean hideTrashMenu = true;
    private int count = 0;
    private MenuItem trashMenuItem;
    private MenuItem popItem;
    private MenuItem ratedItem;
    private static final String SORT_TYPE = "SortType";
    private static final String CURRENT_POS = "CurrentPosition";
    private static String sortType;
    private boolean firstResume = true;
    private String currSelection;
    private Parcelable state;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private int lastVisiblePos;
    private Menu mOptionsMenu;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.example.android.moviespart2_v1.R.menu.menu_main, menu);
        popItem = menu.findItem(R.id.sortby_popularity);
        popItem.setChecked(true);
        ratedItem = menu.findItem(R.id.sortby_highestrated);

        if (hideTrashMenu == true) {
            trashMenuItem = menu.findItem(R.id.menu_trash);
            trashMenuItem.setVisible(false);
            hideTrashMenu = false;
        }

        return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.example.android.moviespart2_v1.R.layout.activity_main);
        logAndAppend(ON_CREATE);

        mContext = getApplicationContext();
        mActivity = MainActivity.this;


        MY_API_KEY = mContext.getString(R.string.api_key);
        mPopURL = BASE_URL + POP_ENDPOINT + API_KEY_PARAMETER + MY_API_KEY;
        mTopRatedURL = BASE_URL + TOP_RATED_ENDPOINT + API_KEY_PARAMETER + MY_API_KEY;

        mRecyclerView = (RecyclerView) findViewById(com.example.android.moviespart2_v1.R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mItemImage = (ImageView) findViewById(com.example.android.moviespart2_v1.R.id.item_image);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        pref = getApplication().getSharedPreferences("MenuOptions", MODE_APPEND);
        if (savedInstanceState != null) {
            if (savedInstanceState.getString(SORT_TYPE).equals("popular")) {
                lastVisiblePos = savedInstanceState.getInt(CURRENT_POS);
                editor = pref.edit();
                editor.putString("menu", "popular");
                editor.commit();
                volleyJsonObjectRequest(mPopURL);
                mGridLayoutManager.scrollToPositionWithOffset(lastVisiblePos, 0);
            } else {
                lastVisiblePos = savedInstanceState.getInt(CURRENT_POS);
                editor = pref.edit();
                editor.putString("menu", "topRated");
                editor.commit();
                volleyJsonObjectRequest(mTopRatedURL);
                mGridLayoutManager.scrollToPositionWithOffset(lastVisiblePos, 0);
                invalidateOptionsMenu();
            }
        } else {
            editor = pref.edit();
            editor.putString("menu", "popular");
            editor.commit();
            volleyJsonObjectRequest(mPopURL);
        }

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if (pref.getString("menu", "").equals("popular")) {
            popItem.setChecked(true);
            getSupportActionBar().setTitle(getString(R.string.popular_action_bar));
        } else {
            ratedItem.setChecked(true);
            getSupportActionBar().setTitle(getString(R.string.top_rated_action_bar));
        }

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        logAndAppend("onOptionsItemSelected");
        firstResume = false;

        switch (id) {
            case com.example.android.moviespart2_v1.R.id.sortby_popularity:
                if (item.isChecked())
                    item.setChecked(false);
                else item.setChecked(true);
                getSupportActionBar().setTitle(getString(R.string.popular_action_bar));
                editor.putString("menu", "popular");
                editor.commit();
                hideTrashMenu = true;
                trashMenuItem.setVisible(false);
                volleyJsonObjectRequest(mPopURL);
                return true;
            case com.example.android.moviespart2_v1.R.id.sortby_highestrated:
                if (item.isChecked())
                    item.setChecked(false);
                else item.setChecked(true);
                getSupportActionBar().setTitle(getString(R.string.top_rated_action_bar));
                editor.putString("menu", "topRated");
                editor.commit();
                hideTrashMenu = true;
                trashMenuItem.setVisible(false);
                volleyJsonObjectRequest(mTopRatedURL);
                return true;
            case R.id.favorites:
                trashMenuItem.setVisible(false);
                hideTrashMenu = true;
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                Intent intent = new Intent(MainActivity.this, FMovieActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        currSelection = pref.getString("menu", "");
        lastVisiblePos = mGridLayoutManager.findFirstCompletelyVisibleItemPosition();
        savedInstanceState.putString(SORT_TYPE, currSelection);
        savedInstanceState.putInt(CURRENT_POS, lastVisiblePos);
        Log.d(TAG, "currSelection in onSave = " + currSelection);

        logAndAppend("onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currSelection = savedInstanceState.getString(SORT_TYPE);
        lastVisiblePos = savedInstanceState.getInt(CURRENT_POS);
        Log.d(TAG, "currSelection in Restore = " + currSelection);
        logAndAppend("onRestoreInstanceState");
    }

    private void volleyJsonObjectRequest(String mURL) {

        final Context context = this;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, mURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, response.toString());
                        try {
                            mPosterImages.clear();
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


    @Override
    protected void onStart() {
        super.onStart();
        logAndAppend(ON_START);

    }

    @Override
    protected void onResume() {
        super.onResume();
        logAndAppend(ON_RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lastVisiblePos = mGridLayoutManager.findLastVisibleItemPosition();
        logAndAppend(ON_PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        lastVisiblePos = mGridLayoutManager.findLastVisibleItemPosition();
        logAndAppend(ON_STOP);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        restoreRVPosition();
        logAndAppend(ON_RESTART);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logAndAppend(ON_DESTROY);
    }

    private void logAndAppend(String lifecycleEvent) {
        Log.d(TAG, "Lifecycle Event: " + lifecycleEvent);
    }

    private void restoreRVPosition(){
        if (pref.contains("menu")) {
            if (pref.getString("menu", "").equals("popular")) {
                Log.d(TAG, "onRestart - popular");
                popItem.setChecked(true);
                getSupportActionBar().setTitle(getString(R.string.popular_action_bar));
                volleyJsonObjectRequest(mPopURL);
                mGridLayoutManager.scrollToPositionWithOffset(lastVisiblePos, 0);
            } else if (pref.getString("menu", "").equals("topRated")) {
                Log.d(TAG, "onRestart - topRated");
                ratedItem.setChecked(true);
                getSupportActionBar().setTitle(getString(R.string.top_rated_action_bar));
                volleyJsonObjectRequest(mTopRatedURL);
                mGridLayoutManager.scrollToPositionWithOffset(lastVisiblePos, 0);
            } else {
                Log.d(TAG, "NO match in shared pref");
            }
        }
    }

}

