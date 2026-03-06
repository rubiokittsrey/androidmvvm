package app.practice.androidmvvm.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EndPoints {

    @GET(APIs.POST_BY_ID)
    Call<Map<String, Object>> getPost(@Path("id") int id);

    @POST(APIs.CREATE_POST)
    Call<Map<String, Object>> createPost(@Body Map<String, Object> body);

    @PUT(APIs.UPDATE_POST)
    Call<Map<String, Object>> updatePost(@Path("id") int id, @Body Map<String, Object> body);

    @DELETE(APIs.DELETE_POST)
    Call<Void> deletePost(@Path("id") int id);
}
