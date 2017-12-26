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
 * Recyclerview GridlayoutManager to lay out poster images.
 * To run this application, please specify your own API key in
 * strings.xml file.
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


    private boolean menuIsInflated;
    private static final int LOADER_ID = 1;
    private static final int LOADER_ID_2 = 2;
    private boolean hideTrashMenu = true;
    private int count = 0;
    private MenuItem trashMenuItem;
    private MenuItem popItem;
    private MenuItem ratedItem;
    private static final String SORT_TYPE = "SortType";
    private static String sortType;
    private boolean firstResume = true;
    private String currSelection;

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
                editor = pref.edit();
                editor.putString("menu", "popular");
                editor.commit();
                volleyJsonObjectRequest(mPopURL);
            } else {
                editor = pref.edit();
                editor.putString("menu", "highestRated");
                editor.commit();
                volleyJsonObjectRequest(mTopRatedURL);
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
    protected void onStart() {
        super.onStart();

        logAndAppend(ON_START);

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
 //               currSelection = "popular";
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
//                currSelection = "highestRated";
                getSupportActionBar().setTitle(getString(R.string.top_rated_action_bar));
                editor.putString("menu", "highestRated");
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
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putString("priorMenuSelection", prevSelection);
//            savedInstanceState.putString("currentMenuSelection", currSelection);
        currSelection = pref.getString("menu", "");
        savedInstanceState.putString(SORT_TYPE, currSelection);
        Log.d(TAG, "currSelection in onSave = " + currSelection);

        logAndAppend("onSaveInstanceState");
//        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//            previousSelection = savedInstanceState.getInt(PRE_SELECTION);
        currSelection = savedInstanceState.getString(SORT_TYPE);
        Log.d(TAG, "currSelection in Restore = " + currSelection);
//            currentSelection = savedInstanceState.getInt(CUR_SELECTION);
        logAndAppend("onRestoreInstanceState");
//        super.onRestoreInstanceState(savedInstanceState);
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
    protected void onResume() {
        super.onResume();
        mGridLayoutManager.scrollToPosition(lastVisiblePos);
        logAndAppend(ON_RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lastVisiblePos = mGridLayoutManager.findFirstCompletelyVisibleItemPosition();
        logAndAppend(ON_PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        lastVisiblePos = mGridLayoutManager.findFirstCompletelyVisibleItemPosition();
        logAndAppend(ON_STOP);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (pref.contains("menu")) {
            if (pref.getString("menu", "").equals("popular")) {
//                volleyJsonObjectRequest(mPopURL);
                popItem.setChecked(true);
                getSupportActionBar().setTitle(getString(R.string.popular_action_bar));
            } else if (pref.getString("menu", "").equals("highestRated")) {
//                volleyJsonObjectRequest(mTopRatedURL);
                ratedItem.setChecked(true);
                getSupportActionBar().setTitle(getString(R.string.top_rated_action_bar));
            } else {
                Log.d(TAG, "NO match in shared pref");
            }
        }

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

//    @Override
//    public AsyncTaskLoader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {
//        return new AsyncTaskLoader<Cursor>(this) {
//
//            Cursor mFMovieData = null;
//
//            @Override
//            protected void onStartLoading() {
//                if (mFMovieData != null) {
//                    deliverResult(mFMovieData);
//                } else {
//                    forceLoad();
//                }
//            }
//
//            @Override
//            public Cursor loadInBackground() {
//
//                try {
//                    return getContentResolver().query(FavMoviesContract.FMovieEntry.CONTENT_URI,
//                            null,
//                            null,
//                            null,
//                            null);
//                } catch (Exception e) {
//                    Log.e(TAG, "Failed to asynchronously load data.");
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//
//            public void deliverResult(Cursor data) {
//                mFMovieData = data;
//                super.deliverResult(data);
//            }
//        };
//    }
//
//
//    @Override
//    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
//        //listOfFavoriteMovieIDs = new ArrayList<>();
//        listOfFavoriteMovieIDs.clear();
//        dataList.clear();
//        //dataList = new ArrayList<>();
//        String sTitle;
//        String sMID;
//        String sRelDate;
//        String sRating;
//        String sOverview;
//        byte[] sByteArray;
//        String sYear;
//        Bitmap sImage;
//
//        if (data != null && data.moveToFirst()) {
//            do {
//                sTitle = data.getString(data.getColumnIndex(FavMoviesContract.FMovieEntry.COLUMN_TITLE));
//                sMID = data.getString(data.getColumnIndex(
//                        FavMoviesContract.FMovieEntry.COLUMN_MOVIEID));
//                sRelDate = data.getString(data.getColumnIndex(
//                        FavMoviesContract.FMovieEntry.COLUMN_RELEASEDATE));
//                sYear = getYear(sRelDate);
//                sRating = data.getString(data.getColumnIndex(
//                        FavMoviesContract.FMovieEntry.COLUMN_RATING));
//                sOverview = data.getString(data.getColumnIndex(
//                        FavMoviesContract.FMovieEntry.COLUMN_RATING));
//                sByteArray = data.getBlob(data.getColumnIndex(
//                        FavMoviesContract.FMovieEntry.COLUMN_IMAGE));
//                sImage = getBitmapFromByte(sByteArray);
//
//                listOfFavoriteMovieIDs.add(sMID);
//                OfflineFavMovieDetails movieDetails = new OfflineFavMovieDetails(sTitle, sMID,
//                        sYear, sRating, sOverview, sImage);
//                dataList.add(movieDetails);
//
//            } while (data.moveToNext());
//            fMovieFlag = 1;
//        } else {
//            fMovieFlag = 0;
//            Toast.makeText(mContext, R.string.taphearttoastmsg, Toast.LENGTH_LONG).show();
//        }
//
//    }
//
//    @Override
//    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
//        logAndAppend("onLoaderReset");
//    }
//
//    public void offlineDataFetch() {
//
//
//    }
//
//    public String getYear(String relDate) {
//        String[] parseYear = relDate.split("-");
//        return parseYear[0];
//
//    }
//
//    public static Bitmap getBitmapFromByte(byte[] image) {
//
//        return BitmapFactory.decodeByteArray(image, 0, image.length);
//
//
//    }
}

