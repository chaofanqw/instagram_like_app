package net.dunrou.mobile;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Stephen on 2018/8/28.
 */

public interface InsNetwork {
    @GET("/api/cook/list")
    Observable<String> getPostInfoRx(@Query("type") String type, @Query("postid") String postid);

    @GET("top250")
    Observable<HttpResult<String>> getTopMovie(@Query("start") int start, @Query("count") int count);
}
