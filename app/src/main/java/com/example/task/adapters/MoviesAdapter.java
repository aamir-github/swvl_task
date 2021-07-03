package com.example.task.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.task.R;
import com.example.task.model.Movie;

/**
 * Created by ravi on 16/11/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Movie> movieList;
    private List<Movie> movieListFiltered;
    private MovieAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        public RatingBar rating;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            year = view.findViewById(R.id.year);
            genre = view.findViewById(R.id.genre);
            rating = view.findViewById(R.id.rating);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected movie in callback
                    listener.onMovieSelected(movieListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public MoviesAdapter(Context context, List<Movie> movieList, MovieAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.movieList = movieList;
        this.movieListFiltered = movieList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Movie movie = movieListFiltered.get(position);
        holder.title.setText(movie.getTitle());
        holder.year.setText(movie.getYear().toString());
        holder.genre.setText(movie.getGenres().toString());
        holder.rating.setRating(movie.getRating());

        /*Glide.with(context)
                .load(movie.getImage())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);*/
    }

    @Override
    public int getItemCount() {
        return movieListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    movieListFiltered = movieList;
                } else {

                    int yearBeingChecked = 2009;
                    int yearCount = 0;

                    List<Movie> filteredList = new ArrayList<>();
                    for (Movie row : movieList) {

                        for (String genre : row.getGenres()) {
                            if (genre.toLowerCase().contains(charString.toLowerCase())) {
                                if (yearBeingChecked == row.getYear()) {

                                    //if there are already five records for a year
                                    if (yearCount >= 5) {
                                        //continue;

                                        Movie itemOnHold = null;
                                        boolean changedHappened = false;

                                        int z = filteredList.size() - 5;
                                        while (z < filteredList.size()) {

                                            if (!changedHappened) {
                                                if (row.getRating() > filteredList.get(z).getRating()) {
                                                    itemOnHold = filteredList.get(z);
                                                    filteredList.set(z, row);
                                                    changedHappened = true;
                                                }
                                                
                                            } else {
                                                if(itemOnHold.getRating() > filteredList.get(z).getRating()) {

                                                    Movie temp = filteredList.get(z);
                                                    filteredList.set(z, itemOnHold);
                                                    itemOnHold = temp;
                                                }
                                            }

                                            z++;
                                        }

                                        break;
                                    }
                                } else {
                                    yearBeingChecked = row.getYear();
                                    yearCount = 0;
                                }

                                filteredList.add(row);
                                yearCount++;

                                break;
                            }
                        }
                    }

                    movieListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = movieListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults
                    filterResults) {
                movieListFiltered = (ArrayList<Movie>) filterResults.values;
                notifyDataSetChanged();
            }
        }

                ;
    }

    public interface MovieAdapterListener {
        void onMovieSelected(Movie movie);
    }
}
