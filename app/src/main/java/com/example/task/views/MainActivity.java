package com.example.task.views;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.example.task.adapters.MoviesAdapter;
import com.example.task.MyApplication;
import com.example.task.util.Constants;
import com.example.task.util.MyDividerItemDecoration;
import com.example.task.R;
import com.example.task.model.Movie;
import com.example.task.util.JsonUtil;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieAdapterListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    //private List<Contact> contactList;
    private MoviesAdapter mAdapter;
    private SearchView searchView;

    // url to fetch contacts json
    private static final String URL = "https://api.androidhive.info/json/contacts.json";

    private List<Movie> movieList;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        recyclerView = findViewById(R.id.recycler_view);
        //contactList = new ArrayList<>();
        movieList = new ArrayList<>();
        mAdapter = new MoviesAdapter(this, movieList, this);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        //fetchContacts();
        fetchRecordFromJsonFile();
    }

    public boolean fetchRecordFromJsonFile() {

        //getting country dial code from json file
        //String jsonString = JsonUtil.getJson("movies.json", this);
        String jsonString = JsonUtil.getJson(Constants.JsonFILENAME, this);

        try {

            //Todo: check it
            JSONObject jsonObject = new JSONObject(jsonString);
            final JSONArray jsonArray = jsonObject.getJSONArray("movies");

            //final JSONArray jsonArray = new JSONArray(jsonString);

            List<Movie> items = new ArrayList<>();
            //Todo: i is declared as globally
            for (i = 0; i < jsonArray.length(); i++) {

                //preparing inner cast list
                final List<String> castList = new ArrayList<>();
                JSONArray arrayCast = jsonArray.getJSONObject(i).getJSONArray("cast");
                for(int j = 0; j < arrayCast.length(); j++) {
                    castList.add(arrayCast.getString(j));
                }

                //preparing inner genres list
                final List<String> genresList = new ArrayList<>();
                JSONArray arrayGenres = jsonArray.getJSONObject(i).getJSONArray("genres");
                for(int k = 0; k < arrayGenres.length(); k++) {
                    genresList.add(arrayGenres.getString(k));
                }

                //preparing main external list "movieList", value assigned outside of for loop
                /*items.add(new Movie() {{
                    setTitle(jsonArray.getJSONObject(i).getString("title"));
                    setYear(jsonArray.getJSONObject(i).getInt("year"));
                    setCast(castList);
                    setGenres(genresList);
                    setRating(jsonArray.getJSONObject(i).getInt("rating"));
                }});*/

                Movie movie = new Movie();
                movie.setTitle(jsonArray.getJSONObject(i).getString("title"));
                movie.setYear(jsonArray.getJSONObject(i).getInt("year"));
                movie.setCast(castList);
                movie.setGenres(genresList);
                movie.setRating(jsonArray.getJSONObject(i).getInt("rating"));

                items.add(movie);
            }

            movieList.clear();
            movieList.addAll(items);

            //view.setCountryCodeSpinner(listCodes);
            mAdapter.notifyDataSetChanged();

            return true;

        } catch (JSONException e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * fetches json by making http calls
     *//*
    private void fetchContacts() {
        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //Todo: try it to local json
                        List<Contact> items = new Gson().fromJson(response.toString(), new TypeToken<List<Contact>>() {
                        }.getType());

                        // adding contacts to contacts list
                        *//*contactList.clear();
                        contactList.addAll(items);*//*

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onMovieSelected(Movie movie) {
        //Toast.makeText(getApplicationContext(), "Selected: " + movie.getTitle() + ", " + movie.getYear(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Constants.MOVIE_DETAILS, movie);
        startActivity(intent);
    }
}
