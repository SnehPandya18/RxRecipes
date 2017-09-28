package com.snehpandya.rxrecipes.model;

import io.reactivex.Observable;

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
        return Observable.defer(() -> Observable.just(name));
    }

    public void setName(String name) {
        this.name = name;
    }

    public Observable<String> descriptionObservable() {
        return Observable.just("This is article description");
    }
}
