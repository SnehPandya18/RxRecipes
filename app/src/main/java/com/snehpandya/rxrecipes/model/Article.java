package com.snehpandya.rxrecipes.model;

import io.reactivex.Observable;

/**
 * Created by sneh.pandya on 28/09/17.
 */

public class Article {

    private String name = "article";

    public io.reactivex.Observable<String> nameObservable() {
        return Observable.just(name);
    }

    public void setName(String name) {
        this.name = name;
    }
}
