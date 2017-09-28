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

        /*
            Simple implementation of Observable and Subscriber

            !!Key Idea 1: Observable and Subscriber can do anything.

            !!Ket Idea 2: Observable and Subscriber are independent
            of the transformational steps between them.

            !!Key Idea 3: Hot vs Cold Observables ->
            Emits Items all the time even when no one is listening
            vs
            Only emits items when it has a subscriber
        */

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

        /*
            **Observable.from() operator**

            Takes a collection of objects/items and emits each of them, one at a time

            !!Tip: Similar to Observable.just() operator
        */

        Observable.fromArray(new Integer[]{1, 2, 3, 4, 5}).subscribe(i -> Log.d(TAG, "onCreate: From: " + i));
    }
}
