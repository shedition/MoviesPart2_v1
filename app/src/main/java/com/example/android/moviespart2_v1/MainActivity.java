package com.example.android.moviespart2_v1;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
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
import com.android.volley.toolbox.Volley;
import com.example.android.moviespart2_v1.data.FavMoviesContract;
import com.example.android.moviespart2_v1.util.SimpleItemTouchHelperCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.DecompositionType.SUPER;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
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
    private ArrayList<Movie> mPosterImages;
    private ArrayList<FavoriteMovie> mFavPosterImages;
    private RecyclerAdapter mAdapter;
    private FavoriteMovieRecyclerAdapter fAdapter;
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
    private String mPopURL;
    private String mTopRatedURL;

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

    private int SORT_MODE = 1;
    private final String TAG_SORT = "sort";
    private List<String> listOfFavoriteMovieIDs;

    private static final int LOADER_ID = 1;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.example.android.moviespart2_v1.R.menu.menu_main, menu);
        menu.findItem(R.id.sortby_popularity).setChecked(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
        if (listOfFavoriteMovieIDs.size() == 0) {
            Toast.makeText(mContext, "Tap the heart icon to favorite a movie.", Toast.LENGTH_LONG).show();
            menu.findItem(R.id.favorites).setEnabled(false);
        } else {
            menu.findItem(R.id.favorites).setEnabled(true);
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

        mRecyclerView = (RecyclerView) findViewById(com.example.android.moviespart2_v1.R.id.recyclerView);
        mItemImage = (ImageView) findViewById(com.example.android.moviespart2_v1.R.id.item_image);

        mPopURL = BASE_URL + POP_ENDPOINT + API_KEY_PARAMETER + MY_API_KEY;
        mTopRatedURL = BASE_URL + TOP_RATED_ENDPOINT + API_KEY_PARAMETER + MY_API_KEY;

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        if (savedInstanceState != null && savedInstanceState.containsKey("SORT_MODE")) {
            switch (savedInstanceState.getInt("SORT_MODE")) {
                case 1:
                    volleyJsonObjectRequest(mPopURL);
                    break;
                case 2:
                    volleyJsonObjectRequest(mTopRatedURL);
                    break;
                case 3:
                    if (listOfFavoriteMovieIDs.size() != 0) {
                        volleyGetFavoriteMovies();
                    }
                    break;
            }
        } else {
            volleyJsonObjectRequest(mPopURL);
        }
//        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        logAndAppend(ON_START);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case com.example.android.moviespart2_v1.R.id.sortby_highestrated:
                if (item.isChecked())
                    item.setChecked(false);
                else item.setChecked(true);
                volleyJsonObjectRequest(mTopRatedURL);
                SORT_MODE = 1;
                break;
            case com.example.android.moviespart2_v1.R.id.sortby_popularity:
                if (item.isChecked())
                    item.setChecked(false);
                else item.setChecked(true);
                volleyJsonObjectRequest(mPopURL);
                SORT_MODE = 2;
                break;
            case R.id.favorites:
                getSupportLoaderManager().initLoader(LOADER_ID, null, this);
                if (!volleyGetFavoriteMovies()) {
                    item.setEnabled(false);
                } else {
                    item.setEnabled(true);
                    item.setChecked(true);
                }
                SORT_MODE = 3;
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("SORT_MODE", SORT_MODE);
        logAndAppend("onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        SORT_MODE = savedInstanceState.getInt("SORT_MODE");
        logAndAppend("onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
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

    private boolean volleyGetFavoriteMovies() {
//        mGridLayoutManager = new GridLayoutManager(this, 2);
//        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mFavPosterImages = new ArrayList<>();
        String mFavMovieURL = BASE_URL + FAV_ENDPOINT;
        final Context context = this;

//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
//                ItemTouchHelper.RIGHT)) {
//
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
//                                  RecyclerView.ViewHolder target){
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){
//                int id = viewHolder.getAdapterPosition();
//                Log.d(TAG, "id of viewholder swiped = " + id);
//                Toast.makeText(mContext, "id of viewholder = " + id, Toast.LENGTH_LONG).show();
//                String movieID = listOfFavoriteMovieIDs.get(id);
//                Log.d(TAG, "movieID to delete = " + movieID);
//                Toast.makeText(mContext, "movieID to delete = " + movieID, Toast.LENGTH_LONG).show();
//                Uri uri = FavMoviesContract.FMovieEntry.CONTENT_URI;
//                uri = uri.buildUpon().appendPath(movieID).build();
//                getContentResolver().delete(uri, null, null);
//                getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
//            }
//
//        }).attachToRecycler(mRecyclerView);

//        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(fAdapter);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(mRecyclerView);

        if (listOfFavoriteMovieIDs.size() == 0) {
            Toast.makeText(mContext, "Tap the heart icon to favorite a movie.", Toast.LENGTH_LONG).show();
            return false;
        }

        for (int i = 0; i < listOfFavoriteMovieIDs.size(); i++) {
            String fURL = mFavMovieURL + listOfFavoriteMovieIDs.get(i) + API_KEY_PARAMETER + MY_API_KEY;
            Log.d(TAG, "mFavMovieURL = " + mFavMovieURL);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fURL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                FavoriteMovie favoriteMovie = new FavoriteMovie(obj);
                                mFavPosterImages.add(favoriteMovie);
                                fAdapter = new FavoriteMovieRecyclerAdapter(mFavPosterImages);
                                mRecyclerView.setAdapter(fAdapter);
//                                ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(fAdapter);
//                                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//                                touchHelper.attachToRecyclerView(mRecyclerView);

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

            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                @Override
                public boolean isItemViewSwipeEnabled(){
                    return true;
                }


                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition();
                    Log.d(TAG, "IDlistBeginning: " + listOfFavoriteMovieIDs.size());
                    String movieID = listOfFavoriteMovieIDs.get(position);
                    Log.d(TAG, "onSwiped string = " + movieID);
//                    listOfFavoriteMovieIDs.remove(position);
                    Log.d(TAG, "IDListAfterSwiped " + listOfFavoriteMovieIDs.size());
                    fAdapter.remove(position);

//                    fAdapter.notifyItemRemoved(position);
//                    fAdapter.notifyItemRangeChanged(position, fAdapter.getItemCount());
                    Uri uri = FavMoviesContract.FMovieEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(movieID).build();
                    getContentResolver().delete(uri, null, null);
                    getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);

                }
            };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(mRecyclerView);

        }

//        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(fAdapter);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(mRecyclerView);

        return true;

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

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        listOfFavoriteMovieIDs = new ArrayList<>();

        if (data != null && data.moveToFirst()) {
            do {
                String mID = data.getString(data.getColumnIndex(
                        FavMoviesContract.FMovieEntry.COLUMN_MOVIEID));
                listOfFavoriteMovieIDs.add(mID);
            } while (data.moveToNext());
//            invalidateOptionsMenu();
        } else {
            invalidateOptionsMenu();
        }

//        data.close();

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }


//    private void activateSwipeToDelete() {
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
//                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
//                                  RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//                ContentResolver contentResolver = getContentResolver();
//                int id = viewHolder.itemView.getId();
//                String movieId = listOfFavoriteMovieIDs.get(id);
//                Uri uri = FavMoviesContract.FMovieEntry.CONTENT_URI;
//                uri = uri.buildUpon().appendPath(movieId).build();
//                contentResolver.delete(uri, null, null);
//
//            }
//
//            @Override
//            public boolean isLongPressDragEnabled() {
//                return true;
//            }
//
//            @Override
//            public boolean isItemViewSwipeEnabled() {
//                return true;
//            }
//        });
//        //volleyGetFavoriteMovies();
//    }


}

