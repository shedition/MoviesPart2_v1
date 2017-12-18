package com.example.android.moviespart2_v1;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
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

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private ArrayList<Movie> mPosterImages = new ArrayList<>();
    private ArrayList<FavoriteMovie> mFavPosterImages = new ArrayList<>();
    private List<String> listOfFavoriteMovieIDs = new ArrayList<>();
    //private List<OfflineFavMovieDetails> dataList;
    private ArrayList<OfflineFavMovieDetails> dataList = new ArrayList<>();
    private RecyclerAdapter mAdapter;
    private FavoriteMovieRecyclerAdapter fAdapter;
    private OfflineRecyclerAdapter oAdapter;
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

    private final static String MENU_SELECTED = "selected";
    private int selected = -1;
    private static int fMovieFlag = 0;
    MenuItem menuitem;




    private boolean menuIsInflated;
    private static final int LOADER_ID = 1;
    private static final int LOADER_ID_2 = 2;
    private boolean hideTrashMenu = true;
    private int count = 0;
    private MenuItem trashMenuItem;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.example.android.moviespart2_v1.R.menu.menu_main, menu);
        menu.findItem(R.id.sortby_popularity).setChecked(true);
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
        volleyJsonObjectRequest(mPopURL);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        logAndAppend(ON_START);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case com.example.android.moviespart2_v1.R.id.sortby_popularity:
                if (item.isChecked())
                    item.setChecked(false);
                else item.setChecked(true);
                selected = id;
                hideTrashMenu = true;
                trashMenuItem.setVisible(false);
                volleyJsonObjectRequest(mPopURL);
                break;
            case com.example.android.moviespart2_v1.R.id.sortby_highestrated:
                if (item.isChecked())
                    item.setChecked(false);
                else item.setChecked(true);
                selected = id;
                hideTrashMenu = true;
                trashMenuItem.setVisible(false);
                volleyJsonObjectRequest(mTopRatedURL);
                break;
            case R.id.favorites:
                selected = id;
                hideTrashMenu = false;
                if (item.isChecked()) {
                    item.setChecked(false);
                    Toast.makeText(mContext, "Already checked", Toast.LENGTH_SHORT).show();
                } else {
                    item.setChecked(true);
                    volleyGetFavoriteMovies();
                }
                trashMenuItem.setVisible(true);

                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(MENU_SELECTED, selected);
        logAndAppend("onSaveInstanceState");
//        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        selected = savedInstanceState.getInt(MENU_SELECTED);
        logAndAppend("onRestoreInstanceState");
//        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        count++;

        if (selected == -1) {
            return true;
        }

        switch (selected) {
            case R.id.sortby_popularity:
                menuitem = (MenuItem) menu.findItem(R.id.sortby_popularity);
                menuitem.setChecked(true);
                volleyJsonObjectRequest(mPopURL);
                trashMenuItem.setVisible(false);
                break;
            case R.id.sortby_highestrated:
                menuitem = (MenuItem) menu.findItem(R.id.sortby_highestrated);
                menuitem.setChecked(true);
                volleyJsonObjectRequest(mTopRatedURL);
                trashMenuItem.setVisible(false);
                break;
            case R.id.favorites:
                menuitem = (MenuItem) menu.findItem(R.id.favorites);
                menuitem.setChecked(true);
                volleyGetFavoriteMovies();
                trashMenuItem.setVisible(true);

                break;

        }

        logAndAppend("onPrepareOptionsMenu");
        logAndAppend("count = " + count);

        return super.onPrepareOptionsMenu(menu);
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

    private boolean volleyGetFavoriteMovies() {

        String mFavMovieURL = BASE_URL + FAV_ENDPOINT;
        final Context context = this;

        if (listOfFavoriteMovieIDs.size() == 0) {
            fMovieFlag = 0;
            Toast.makeText(mContext, R.string.taphearttoastmsg, Toast.LENGTH_LONG).show();
            mRecyclerView.setAdapter(fAdapter);
            return false;
        } else {
            mFavPosterImages.clear();

            for (int j = 0; j < listOfFavoriteMovieIDs.size(); j++) {

                String fURL = mFavMovieURL + listOfFavoriteMovieIDs.get(j) + API_KEY_PARAMETER + MY_API_KEY;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject obj = new JSONObject(response.toString());
                                    FavoriteMovie favoriteMovie = new FavoriteMovie(obj);
                                    mFavPosterImages.add(favoriteMovie);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                fAdapter = new FavoriteMovieRecyclerAdapter(mFavPosterImages);
                                mRecyclerView.setAdapter(fAdapter);

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof NoConnectionError || error instanceof NetworkError
                                || error instanceof TimeoutError) {
                            getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
                            oAdapter = new OfflineRecyclerAdapter(dataList);
                            mRecyclerView.setAdapter(oAdapter);

//                            ContextThemeWrapper ctw = new ContextThemeWrapper(context,
//                                    com.example.android.moviespart2_v1.R.style.AlertDialogCustom);
//
//                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
//                            alertDialogBuilder.setTitle("Network Communication Error");
//                            alertDialogBuilder
//                                    .setMessage("Please check your Internet connection.")
//                                    .setCancelable(true)
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            MainActivity.this.finish();
//                                        }
//                                    });
//
//                            AlertDialog alertDialog = alertDialogBuilder.create();
//                            alertDialog.show();
//                            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);

                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(), "Parse error!",
                                    Toast.LENGTH_LONG).show();
                        }

                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });

//                requestQueue.getCache().clear();

                Toast.makeText(mContext, "To delete, do a long swipe from left to right, or right to left.",
                        Toast.LENGTH_SHORT).show();
                requestQueue.add(jsonObjectRequest);

                fMovieFlag = 1;

                ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

                    @Override
                    public boolean isItemViewSwipeEnabled() {
                        return true;
                    }

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                        final int position = viewHolder.getAdapterPosition();
                        String movieID = listOfFavoriteMovieIDs.get(position);
                        fAdapter.remove(position);

                        Uri uri = FavMoviesContract.FMovieEntry.CONTENT_URI;
                        uri = uri.buildUpon().appendPath(movieID).build();
                        getContentResolver().delete(uri, null, null);
                        getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);


                    }
                };

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(mRecyclerView);
            }

            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        logAndAppend(ON_RESUME);
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        logAndAppend(ON_PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        logAndAppend(ON_STOP);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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

    @Override
    public AsyncTaskLoader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mFMovieData = null;

            @Override
            protected void onStartLoading() {
                if (mFMovieData != null) {
                    deliverResult(mFMovieData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(FavMoviesContract.FMovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mFMovieData = data;
                super.deliverResult(data);
            }
        };
    }
//        switch (id) {
//            case 1:
//                return new AsyncTaskLoader<Cursor>(this) {
//                    @Override
//                    public Cursor loadInBackground() {
//                        try {
//                            return getContentResolver().query(FavMoviesContract.FMovieEntry.CONTENT_URI,
//                                    null,
//                                    null,
//                                    null,
//                                    null);
//                        } catch (Exception e) {
//                            Log.e(TAG, "Failed to asynchronously load data.");
//                            e.printStackTrace();
//                            return null;
//                        }
//                    }
//                };
////            break;
//            case 2:
//                return new AsyncTaskLoader<Cursor>(this) {
//                    @Override
//                    public Cursor loadInBackground() {
//                        return null;
//                    }
//                };
//            break;
//
//        }
//    }


    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        //listOfFavoriteMovieIDs = new ArrayList<>();
        listOfFavoriteMovieIDs.clear();
        dataList.clear();
        //dataList = new ArrayList<>();
        String sTitle;
        String sMID;
        String sRelDate;
        String sRating;
        String sOverview;
        byte[] sByteArray;
        String sYear;
        Bitmap sImage;

        if (data != null && data.moveToFirst()) {
            do {
                sTitle = data.getString(data.getColumnIndex(FavMoviesContract.FMovieEntry.COLUMN_TITLE));
                sMID = data.getString(data.getColumnIndex(
                        FavMoviesContract.FMovieEntry.COLUMN_MOVIEID));
                sRelDate = data.getString(data.getColumnIndex(
                        FavMoviesContract.FMovieEntry.COLUMN_RELEASEDATE));
                sYear = getYear(sRelDate);
                sRating = data.getString(data.getColumnIndex(
                        FavMoviesContract.FMovieEntry.COLUMN_RATING));
                sOverview = data.getString(data.getColumnIndex(
                        FavMoviesContract.FMovieEntry.COLUMN_RATING));
                sByteArray = data.getBlob(data.getColumnIndex(
                        FavMoviesContract.FMovieEntry.COLUMN_IMAGE));
                sImage = getBitmapFromByte(sByteArray);

                listOfFavoriteMovieIDs.add(sMID);
                OfflineFavMovieDetails movieDetails = new OfflineFavMovieDetails(sTitle, sMID,
                        sYear, sRating, sOverview, sImage);
                dataList.add(movieDetails);

            } while (data.moveToNext());
            fMovieFlag = 1;
        } else {
            fMovieFlag = 0;
            Toast.makeText(mContext, R.string.taphearttoastmsg, Toast.LENGTH_LONG).show();
        }

    }
//        int id = loader.getId();
//        switch (id) {
//            case 1:
//                listOfFavoriteMovieIDs = new ArrayList<>();
//
//                if (data != null && data.moveToFirst()) {
//                    do {
//                        String mID = data.getString(data.getColumnIndex(
//                                FavMoviesContract.FMovieEntry.COLUMN_MOVIEID));
//                        listOfFavoriteMovieIDs.add(mID);
//                    } while (data.moveToNext());
//                    fMovieFlag = 1;
//                } else {
//                    fMovieFlag = 0;
//                    Toast.makeText(mContext, R.string.taphearttoastmsg, Toast.LENGTH_LONG).show();
//                }
//                break;
//            case 2:
//                break;
//
//
//        }
//    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        logAndAppend("onLoaderReset");
    }

    public void offlineDataFetch() {


    }

    public String getYear(String relDate) {
        String[] parseYear = relDate.split("-");
        return parseYear[0];

    }

    public static Bitmap getBitmapFromByte(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}

