package net.dunrou.mobile.network;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Stephen on 2018/8/28.
 */

public interface InsNetwork {
    @GET("/api/cook/list")
    Observable<String> getPostInfoRx(@Query("type") String type, @Query("postid") String postid);

    @GET("top250")
    Observable<HttpResult<String>> getTopMovie(@Query("start") int start, @Query("count") int count);

    @FormUrlEncoded
    @POST("/api/login")
    Observable<HttpResult<String>> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/register")
    Observable<HttpResult<String>> register(@Field("email") String email, @Field("password") String password);

    @GET("/parkdata/bayinfo")
    Observable<String> test(@Query("user") int user);


}
