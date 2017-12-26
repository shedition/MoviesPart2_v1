package com.example.android.moviespart2_v1;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by waiyi on 12/18/2017.
 */

public class FMovieActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "FMovieActivity";
    private static final String BASE_URL = "http://api.themoviedb.org/3";
    private static final String FAV_ENDPOINT = "/movie/";
    private static final String mFMovieBaseURL = BASE_URL + FAV_ENDPOINT;
    private static final String API_KEY_PARAMETER = "?api_key=";
    private static Context mContext;
    private static Activity mActivity;
    private static String MY_API_KEY;
    private MenuItem favItem;

    private RecyclerView fRecyclerview;
    private GridLayoutManager fGridLayoutManager;


    //    private List<String> movieIDList = new ArrayList<>();
    private static ArrayList<String> movieIDList = new ArrayList<>();
    private static ArrayList<OfflineFavMovieDetails> dataList = new ArrayList<>();
    //    private ArrayList<String> movieIDList = new ArrayList<>();
//    private ArrayList<OfflineFavMovieDetails> dataList = new ArrayList<>();
    private ArrayList<FavoriteMovie> mFavPosterImages = new ArrayList<>();
    private FavoriteMovieRecyclerAdapter fAdapter;
    private OfflineRecyclerAdapter oAdapter;
    private ImageView fItemImage;
    private String menuSelection;
    public static RequestQueue requestQueue;
    private static final int LOADER_ID = 1;
    //    private String SORT_TYPE_P;
//    private String SORT_TYPE_C;
    private static final String SORT_TYPE = "SortType";
    private String sortSelection;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
//    private String popSort = "sort by popularity";
//    private String highestRatedSort = "sort by highest rated";


    private static final String ON_CREATE = "onCreate";
    private static final String ON_START = "onStart";
    private static final String ON_RESUME = "onResume";
    private static final String ON_PAUSE = "onPause";
    private static final String ON_STOP = "onStop";
    private static final String ON_RESTART = "onRestart";
    private static final String ON_DESTROY = "onDestroy";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_favoritemovies, menu);
        favItem = menu.findItem(R.id.favorites);
        menu.findItem(R.id.menu_trash).setVisible(false);
        menu.findItem(R.id.favorites).setChecked(true);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logAndAppend(ON_CREATE);
        mContext = getApplicationContext();
        mActivity = FMovieActivity.this;
        MY_API_KEY = mContext.getString(R.string.api_key);
        fRecyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        fRecyclerview.setHasFixedSize(true);
        fGridLayoutManager = new GridLayoutManager(this, 2);
        fRecyclerview.setLayoutManager(fGridLayoutManager);
        fItemImage = (ImageView) findViewById(R.id.item_image);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        pref = getApplication().getSharedPreferences("MenuOptions", MODE_APPEND);
        editor = pref.edit();

//        Intent intentExtras = getIntent();
//
//        Bundle extrasBundle = intentExtras.getExtras();
//        if(!extrasBundle.isEmpty()){
////            boolean hasSortByString = extrasBundle.containsKey("sort_type") ;
//            SORT_TYPE_P = extrasBundle.getString("sort_type");
//        } else {
//            Log.d(TAG, "Bundle passed in from Intent is empty");
//        }
        getSupportLoaderManager().initLoader(LOADER_ID, null, FMovieActivity.this);
        Log.d(TAG, "movieIDList = C " + movieIDList.size());
        Log.d(TAG, "dataList = C " + dataList.size());
//        volleyGetFavoriteMovies();
//        if (volleyGetFavoriteMovies()){
//            Log.d(TAG, "volley success ");
//        } else {
//            Log.d(TAG, "volley failed");
//        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        Bundle bundle;

        switch (id) {
            case R.id.sortby_popularity:
                item.setChecked(true);
                editor.putString("menu", "popular");
                editor.commit();

//                if (item.isChecked())
//                    item.setChecked(false);
//                else item.setChecked(true);
                sortSelection = "popular";
                intent = new Intent(this, MainActivity.class);
                intent.putExtra(SORT_TYPE, sortSelection);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
//                intentExtras = new Intent(this, MainActivity.class);
//                bundle = new Bundle();
//                bundle.putString("sort_type", SORT_TYPE_C);
//                intentExtras.putExtra("MENU_ITEM", menuSelection);
//                startActivity(intentExtras);
                return true;
            case R.id.sortby_highestrated:
                item.setChecked(true);
                editor.putString("menu", "highestRated");
                editor.commit();
//                if (item.isChecked())
//                    item.setChecked(false);
//                else item.setChecked(true);
                sortSelection = "highestRated";
                intent = new Intent(this, MainActivity.class);
                intent.putExtra(SORT_TYPE, sortSelection);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                editor.putString("menu", "highestRated");
//                editor.commit();

                startActivity(intent);
//                intentExtras = new Intent(this, MainActivity.class);
//                bundle = new Bundle();
//                bundle.putString("sort_type", SORT_TYPE_C);
//                intentExtras.putExtra("MENU_ITEM", menuSelection);
//                startActivity(intentExtras);
                return true;
            case R.id.favorites:
                if (item.isChecked())
                    item.setChecked(false);
                else item.setChecked(true);
                getSupportLoaderManager().restartLoader(LOADER_ID, null, FMovieActivity.this);
//                volleyGetFavoriteMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

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
        movieIDList.clear();
        dataList.clear();
        String sTitle;
        String sMID;
        String sRelDate;
        String sRating;
        String sOverview;
        byte[] sByteArray;
        String sYear;
        Bitmap sImage;

        Log.d(TAG, "data count = " + data.getCount());

        if (data != null && data.moveToFirst()) {
            Log.d(TAG, "I am here");
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
                        FavMoviesContract.FMovieEntry.COLUMN_SYNOPSIS));
                sByteArray = data.getBlob(data.getColumnIndex(
                        FavMoviesContract.FMovieEntry.COLUMN_IMAGE));
                sImage = getBitmapFromByte(sByteArray);

                movieIDList.add(sMID);
                OfflineFavMovieDetails movieDetails = new OfflineFavMovieDetails(sTitle, sMID,
                        sYear, sRating, sOverview, sImage);
                dataList.add(movieDetails);
                Log.d(TAG, "movieIDList = L " + movieIDList.size());
                Log.d(TAG, "dataList = L " + dataList.size());

            } while (data.moveToNext());

        } else {
            Log.d(TAG, "Cursor is null or cant be move to first");

//            Toast.makeText(mContext, R.string.taphearttoastmsg, Toast.LENGTH_SHORT).show();
        }
        volleyGetFavoriteMovies();

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");

    }


    public String getYear(String relDate) {
        String[] parseYear = relDate.split("-");
        return parseYear[0];

    }

    public static Bitmap getBitmapFromByte(byte[] image) {

        return BitmapFactory.decodeByteArray(image, 0, image.length);

    }

    public boolean volleyGetFavoriteMovies() {

        final Context context = this;
        Log.d(TAG, "movieIDList in V = " + movieIDList.size());

//        getSupportLoaderManager().initLoader(LOADER_ID, null, FMovieActivity.this);

        if (movieIDList.size() == 0) {
            Toast.makeText(mContext, R.string.taphearttoastmsg, Toast.LENGTH_SHORT).show();
            fRecyclerview.setAdapter(fAdapter);
            return false;
        } else {
            mFavPosterImages.clear();
            Toast.makeText(mContext, R.string.longSwipeToDelete, Toast.LENGTH_SHORT).show();
            for (int i = 0; i < movieIDList.size(); i++) {
                String fURL = mFMovieBaseURL + movieIDList.get(i) + API_KEY_PARAMETER +
                        MY_API_KEY;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fURL,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            FavoriteMovie favoriteMovie = new FavoriteMovie(obj);
                            mFavPosterImages.add(favoriteMovie);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "mFavPosterImages = " + mFavPosterImages.size());
                        fAdapter = new FavoriteMovieRecyclerAdapter(mFavPosterImages);
                        fRecyclerview.setAdapter(fAdapter);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError || error instanceof NetworkError
                                || error instanceof TimeoutError) {
                            getSupportLoaderManager().restartLoader(LOADER_ID, null,
                                    FMovieActivity.this);
                            oAdapter = new OfflineRecyclerAdapter(dataList);
                            fRecyclerview.setAdapter(oAdapter);
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(), "Parse error!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });
                requestQueue.add(jsonObjectRequest);

                ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        final int position = viewHolder.getAdapterPosition();
                        String movieID = movieIDList.get(position);
                        fAdapter.remove(position);
                        Uri uri = FavMoviesContract.FMovieEntry.CONTENT_URI;
                        uri = uri.buildUpon().appendPath(movieID).build();
                        getContentResolver().delete(uri, null, null);
                        getSupportLoaderManager().restartLoader(LOADER_ID, null, FMovieActivity.this);
                    }
                };
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(fRecyclerview);
            }
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        logAndAppend(ON_RESUME);

        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
//        volleyGetFavoriteMovies();
    }


    @Override
    protected void onStart() {
        super.onStart();
//        getSupportLoaderManager().initLoader(LOADER_ID, null, FMovieActivity.this);
//        favItem.setChecked(true);
        logAndAppend(ON_START);

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
        favItem.setChecked(true);
        logAndAppend(ON_RESTART);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logAndAppend(ON_DESTROY);
    }

    private void logAndAppend(String lifecycleEvent) {
        Log.d(TAG, "Lifecycle Event FMovie: " + lifecycleEvent);
    }


}