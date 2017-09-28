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

        //Simple implementation of Observable and Subscriber
        Observable.just("Hello World").subscribe(s -> Log.d(TAG, "onCreate: Just: " + s));

        Article article = new Article();
        Observable<String> name = article.nameObservable();
        article.setName("Supercars");
        name.subscribe(p -> Log.d(TAG, "onCreate: Article: " + p));

        /*
            **Observable.map() operator**

            Takes in one value and outputs another value

            Possible to chain as many map() calls as wanted together

            !!Interesting: map() does not have to emit items of the
            same type as the source Observable
        */

        Observable.just("This is map operator implementation")
                .map(String::hashCode)
                .map(i -> Integer.toString(i))
                .subscribe(s -> Log.d(TAG, "onCreate: Map: " + s));
    }
}
