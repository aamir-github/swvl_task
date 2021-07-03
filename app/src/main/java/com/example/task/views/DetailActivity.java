package com.example.task.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import com.example.task.R;
import com.example.task.adapters.FlickrPhotoAdapter;
import com.example.task.adapters.MoviesAdapter;
import com.example.task.api.ApiClient;
import com.example.task.api.EndPoint;
import com.example.task.model.Movie;
import com.example.task.model.flickr.Parent;
import com.example.task.model.flickr.Photo;
import com.example.task.util.Constants;
import com.example.task.util.MyDividerItemDecoration;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Photo> photoList;
    private FlickrPhotoAdapter mAdapter;
    private String movieTitle = "";
    private int page = 1;
    private int perPage = 10;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        initViews();
    }

    private void initViews() {

        Movie movie = (Movie) getIntent().getSerializableExtra(Constants.MOVIE_DETAILS);

        movieTitle = movie.getTitle();
        TextView title = findViewById(R.id.title);
        title.setText(movieTitle);

        TextView year = findViewById(R.id.year);
        year.setText(movie.getYear().toString());

        TextView genre = findViewById(R.id.genre);
        genre.setText(movie.getGenres().toString());

        RatingBar rating = findViewById(R.id.rating);
        rating.setRating(movie.getRating());

        LinearLayoutCompat castLayout = findViewById(R.id.cast_layout);
        for(String cast : movie.getCast()){
            TextView castName = new TextView(this);
            castName.setText(cast);
            castName.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.movie_year));
            castName.setTextColor(getResources().getColor(R.color.movie_year));
            castLayout.addView(castName);
        }

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recycler_view_flickr);
        photoList = new ArrayList<>();
        mAdapter = new FlickrPhotoAdapter(this , photoList);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //recyclerViews.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);

        getFlickrContent();
    }

    private void getFlickrContent() {

        showProgressBar(true);

        EndPoint endPoints = ApiClient.getClient().create(EndPoint.class);
        Observable<Parent> observable = endPoints.getFlickrImages(
                Constants.METHOD,
                Constants.API_KEY,
                Constants.FORMAT,
                Constants.NOJSONCALLBACK,
                movieTitle,
                page,
                perPage

        );
        observable.observeOn(AndroidSchedulers.mainThread())
                .onBackpressureBuffer() //to handle MissingBackpressureException, 6.0.13, july 20
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Parent>() {

                    @Override
                    public void onCompleted() {
                        showProgressBar(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(DetailActivity.this, "Flickr API error occured", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Parent response) {

                        List<Photo> items = response.getPhotos().getPhoto();
                        photoList.addAll(items);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void showProgressBar(boolean flag){
        if (flag) {
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }
}