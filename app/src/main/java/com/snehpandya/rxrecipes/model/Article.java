package com.snehpandya.rxrecipes.model;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sneh.pandya on 28/09/17.
 */

public class Article {

    private String name;

    /*
        **Wrapped Observable.just() with defer()**

        None of the code inside defer() is executed until
        Subscriber subscribes to this Observable

        !!Drawback: defer() creates new Observable each time
        a new Subscriber subscribes
    */

    public Observable<String> nameObservable() {
        return Observable.defer(() -> Observable.just(name))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void setName(String name) {
        this.name = name;
    }

    public Observable<String> descriptionObservable() {
        return Observable.just("This is article description", "Description is here", "New item",
                "Great ideas", "Latest technology", "Tagged places", "Celebrities")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public List<String> getArticles() {
        SystemClock.sleep(3000);
        return createArticles();
    }

    private List<String> createArticles() {
        List<String> articles = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            articles.add("Article " + i);
        }
        return articles;
    }
}
