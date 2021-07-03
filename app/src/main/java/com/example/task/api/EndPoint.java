package com.example.task.api;

import androidx.annotation.NonNull;

import com.example.task.model.flickr.Parent;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface EndPoint {

    @NonNull
    @POST("/services/rest/")
    @Headers({"Content-Type: application/json", "Connection: close"})
    //Observable<String> getSatelliteImage(@Url String apiUrl);
    Observable<Parent> getFlickrImages(@Query("method") String method, @Query("api_key") String apiKey,
                                         @Query("format") String format, @Query("nojsoncallback") int jsonCallback,
                                         @Query("text") String title, @Query("page") int page,
                                         @Query("per_page") int perPage);
}
