package net.dunrou.mobile.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import net.dunrou.mobile.R;
import net.dunrou.mobile.bean.BaseActivity;
import net.dunrou.mobile.network.HttpResult;
import net.dunrou.mobile.network.InsNetwork;
import net.dunrou.mobile.network.InsService;
import net.dunrou.mobile.network.RetrofitUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        TextView test = findViewById(R.id.test);
        test.setOnClickListener(this);
    }

    private void getMovie(){
        InsNetwork insNetwork = InsService.getInstance().getInsNetwork();

        Observer<HttpResult<String>> deal = new Observer<HttpResult<String>>() {

            @Override
            public void onError(Throwable e) {
                Log.d("result", "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Toast.makeText(MainActivity.this, "Get Top Movie Completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSubscribe(Disposable d) {
                Toast.makeText(MainActivity.this, "Get Top Movie begin", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(HttpResult<String> movieEntity) {
                Log.d("result", "onNext: " + movieEntity.getData());
            }
        };

        RetrofitUtil.bind(insNetwork.getTopMovie(0, 10), deal);
    }

    @Override
    public void onClick(View view) {
        getMovie();
    }
}
