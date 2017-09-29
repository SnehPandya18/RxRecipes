package com.snehpandya.rxrecipes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.snehpandya.rxrecipes.model.Article;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private Disposable mDisposable;

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

            !!Key idea #4: Operators let you do anything to the stream of data.
        */

        /*
            **Schedulers**

            Everything that runs before the Subscriber runs,
            is on an I/O thread. In the end, View manipulation
            happens on the Main thread.

            subscribeOn -> Tells Observable code, which thread to run on
            observeOn -> Tells Subscriber, which thread to run on

            !!Tip: subscribeOn & observeOn can be attached to any Observable
            as they are just operators
         */

        mDisposable = Observable.just("Hello World")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Just: " + s), s -> Log.e(TAG, "onCreate: Just: Error!"));

        /*
            **Error handling**

            !!1. onError() is called if an Exception is thrown at any time

            !!2. The operators don't have to handle the Exception

            !!3. We know when the Subscriber has finished receiving items

            !!4. Error handling is skipped by Observables and operators,
            Subscribers handle errors.

            !!5. Unchecked Exceptions are automatically forwarded to onError().

            !!6. Even though RxJava has own system for handling errors,
            checked Exceptions must be handled by developer code. E.g. Try - Catch block.
        */

        Article article = new Article();    //¯\_(ツ)_/¯
        Observable<String> name = article.nameObservable();
        article.setName("Supercars");
        mDisposable = name.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(p -> Log.d(TAG, "onCreate: Article: " + p), p -> Log.e(TAG, "onCreate: Article: Error!"));

        /*
            **Observable.map() operator**

            Takes in one value and outputs another value

            Possible to chain as many map() calls as wanted together

            !!Interesting: map() does not have to emit items of the
            same type as the source Observable
        */

        mDisposable = Observable.just("This is map operator implementation")
                .map(String::hashCode)
                .map(i -> Integer.toString(i))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Map: " + s), s -> Log.e(TAG, "onCreate: Map: Error!"));

        /*
            **Observable.flatMap() operator**

            Takes items emitted by one Observable, transforms them into another Observable
            Gives newly transformed (resulted) Observable to Subscriber

            !!Tip: Observable.map() applies a function to
            each item emitted by original Observable whereas,
            Observable.flatMap() converts one Observable into another
            and gives resultant Observable as output ->
            Subscriber sees only resultant Observable
        */

        mDisposable = Observable.just(getIntegersList())
                .flatMap(i -> Observable.fromArray(i))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(i -> Log.d(TAG, "onCreate: FlatMap for List: " + i), i -> Log.e(TAG, "onCreate: FlatMap for List: Error!"));

        mDisposable = Observable.just(getIntegersArray())
                .flatMap(i -> Observable.fromArray(i))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(i -> Log.d(TAG, "onCreate: FlatMap for Array: " + i), i -> Log.e(TAG, "onCreate: FlatMap for Array: Error!"));

        /*
            Observable<String> description = article.descriptionObservable();

            We could explicitly call this method to get the description result,
            but flatMap lets you call method directly with "article" object!
            See above -> ¯\_(ツ)_/¯
        */

        /*
            **Observable.filter() operator**

            Emits only the items that pass a predicate test
        */

        /*
            **Observable.take() operator**

            Emits the number of items specified. If there are fewer than
            specified count, it will stop early.
        */

        /*
            **Observable.doOnNext() operator**

            Allows us to add extra behaviour each time an item is emitted
        */

        mDisposable = Observable.just(article)
                .flatMap(d -> article.descriptionObservable())
                .filter(d -> d != null)
                .take(5)
                .doOnNext(s -> Log.d(TAG, "onCreate: DoOnNext: " + s))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: FlatMap returning particular item: " + s),
                        s -> Log.e(TAG, "onCreate: Flatmap returning particular item: Error!"));

        /*
            **Observable.from() operator**

            Takes a collection of objects/items and emits each of them, one at a time

            !!Tip: Similar to Observable.just() operator
        */

        mDisposable = Observable.fromArray(new Integer[]{1, 2, 3, 4, 5})
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(i -> Log.d(TAG, "onCreate: From: " + i), i -> Log.e(TAG, "onCreate: From: Error!"));

        /*
            **Observable.zip() operator

            Allows to combine multiple calls together into a single call
        */

        mDisposable = Observable.zip(article.nameObservable(), article.descriptionObservable(),
                (names, desc) -> fi(names, desc))
                .subscribe(r -> Log.d(TAG, "onCreate: Zip: " + r), r -> Log.e(TAG, "onCreate: Zip: Error!"));


        /*
            **Observable.repeat() operator**

            Resubscribes when it receives onCompleted()
        */

        mDisposable = Observable.just("This is new data").repeat(5)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Repeat: Main data: " + s), s -> Log.e(TAG, "onCreate: Repeat: Error data: " + s));

        /*
            **Observable.retry() operator**

            Resubscribes when it receives onError()
        */

        mDisposable = Observable.just("This is error data").retry(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Retry: Main data: " + s), s -> Log.e(TAG, "onCreate: Retry: Error data: " + s));

        /*
            **Observable.fromCallable() operator**

            Used for async calls. Code for emitted value
            is not run until someone subscribes to the Observable
        */

        Observable<List<String>> observable = Observable.fromCallable(article::getArticles);
        mDisposable = observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: FromCallable: " + s), s -> Log.e(TAG, "onCreate: FromCallable: Error!"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }

    private List<Integer> getIntegersList() {
        List<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(20);
        integers.add(300);
        integers.add(4000);
        integers.add(50000);
        return integers;
    }

    private Integer[] getIntegersArray() {
        return new Integer[]{1, 20, 300, 4000, 50000};
    }

    private String fi(String names, String desc) {
        return String.valueOf(names + ", " + desc);
    }
}
