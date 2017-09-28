package com.snehpandya.rxrecipes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.snehpandya.rxrecipes.model.Article;

import io.reactivex.Observable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Article article = new Article();
        article.setName("Supercars");
        Observable<String> name = article.nameObservable();
        name.subscribe(p -> Log.d(TAG, "onCreate: " + p));
    }
}
