package net.dunrou.mobile;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Stephen on 2018/8/28.
 */

public class InsService {

    public static final String BASE_URL = "https://api.douban.com/v2/movie/";

    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit retrofit;
    private InsNetwork insSeverice;

    //构造方法私有
    private InsService() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        insSeverice = retrofit.create(InsNetwork.class);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final InsService INSTANCE = new InsService();
    }

    //获取单例
    public static InsService getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public InsNetwork getInsNetwork(){
        return insSeverice;
    }

}
