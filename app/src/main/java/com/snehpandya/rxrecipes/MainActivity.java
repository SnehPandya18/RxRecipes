package com.snehpandya.rxrecipes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.snehpandya.rxrecipes.model.Article;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

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

            !!Tip 1: Observable.map() applies a function to
            each item emitted by original Observable whereas,
            Observable.flatMap() converts one Observable into another
            and gives resultant Observable as output ->
            Subscriber sees only resultant Observable

            !!Tip 2: "map" transforms items emitted by an Observable
            by applying a function to each item whereas "flatmap":

            1. Applies a specified function to each emitted item
            & this function in turn returns an Observable for each item.
            2. flatMap then merges all these sequences to make a new sequence.
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

            Allows to combine multiple async calls together into a single call

            When you need a signal that sends a value each time
            any of its inputs change, use combineLatest.
            When you need a signal that sends a value only when
            all of its inputs change, use zip
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
            is not run until someone subscribes to the Observable.

            !!Tip: fromCallable can handle checked exceptions.
        */

        Observable<List<String>> observable = Observable.fromCallable(article::getArticles);
        mDisposable = observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: FromCallable: " + s), s -> Log.e(TAG, "onCreate: FromCallable: Error!"));

        /*
            **Observable.skip() operator**

            Skips the first 'n' items emitted by Observable
            and emits data after 'n' items
        */

        mDisposable = Observable.just(getIntegersArray())
                .flatMap(i -> Observable.fromArray(i))
                .skip(2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Skip: " + s), s -> Log.e(TAG, "onCreate: Skip: Error!"));

        /*
            **Observable.skipLast() operator**

            Skips 'n' number of elements starting from last position
        */

        mDisposable = Observable.just(getIntegersArray())
                .flatMap(i -> Observable.fromArray(i))
                .skipLast(2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: SkipLast: " + s), s -> Log.e(TAG, "onCreate: SkipLast: Error!"));

        /*
            **Observable.take() operator**

            Emits first 'n' number of elements
        */

        mDisposable = Observable.just(getIntegersArray())
                .flatMap(i -> Observable.fromArray(i))
                .take(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Take: " + s), s -> Log.e(TAG, "onCreate: Take: Error!"));

        /*
            **Observable.takeLast() operator**

            Emits 'n' number of elements starting from last position
        */

        mDisposable = Observable.just(getIntegersArray())
                .flatMap(i -> Observable.fromArray(i))
                .takeLast(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: TakeLast: " + s), s -> Log.e(TAG, "onCreate: TakeLast: Error!"));

        /*
            **Observable.concat() operator**

            Concats multiple Observables and emits data stream
            from Observables, one after another

            !!Tip: Next Observables will start emitting only after
            the previous Observable has finished emitting data
        */

        mDisposable = Observable.concat(article.nameObservable(), article.descriptionObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Concat: " + s), s -> Log.e(TAG, "onCreate: Concat: Error!"));

        /*
            **Observable.merge() operator**

            Merge multiple Observables and emits data stream
            from Observables, interleaving the outputs

            !!Tip: Merge does not wait for any Observable to
            finish emitting data, it emits data from all the
            specified Observables simultaneously as soon as
            the data becomes available to emit.
        */

        mDisposable = Observable.merge(article.descriptionObservable(), article.dateObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Merge: " + s), s -> Log.e(TAG, "onCreate: Merge: Error!"));

        /*
            **Observable.debounce() operator**

            Emits items from an Observable if a particular
            time span has passed without it emitting any
            other item.
        */

        mDisposable = Observable.just(article.getArticles())
                .debounce(4, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Debounce: " + s), s -> Log.e(TAG, "onCreate: Debounce: Error!"));

        /*
            **Observable.share() operator**

            Allows multiple Subscribers to subscribe
            to a single Observable. It avoids duplication
            of expensive operation calls & promises
            better, cheaper and faster resource utilization.
        */

        Observable<String> shareObservable = article.descriptionObservable().share();

        shareObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Share: " + s), s -> Log.e(TAG, "onCreate: Share: Error!"));

        shareObservable.take(5)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Share: " + s), s -> Log.e(TAG, "onCreate: Share: Error!"));

        /*
            **Observable.buffer() operator**

            Periodically gather items emitted by an Observable
            into bundles and emit these bundles rather than
            emitting them at once.
        */

        mDisposable = Observable.just(getIntegersList(), getIntegersList(), getIntegersList())
                .buffer(2, 2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Buffer: " + s), s -> Log.d(TAG, "onCreate: Buffer: Error!"));

        /*
            **Maybe Observable**

            Maybe Observable succeeds with an item,
            or no item, or errors.

            !!Tip: Maybe emits at most one item.
        */

        Maybe<List<Integer>> listMaybe = Maybe.create(e -> {
            List<Integer> list = getIntegersList();
            if (list != null && !list.isEmpty()) {
                e.onSuccess(list);
            } else {
                e.onComplete();
            }
        });

        mDisposable = listMaybe.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Maybe: " + s), s -> Log.e(TAG, "onCreate: Maybe: Error!"));

        /*
            **Single Observable**

            Instead of  onCompleted(),
                        onNext(),
                        onError(),
            it only gives
                        onSuccess()
                      & onError()
        */

        Single<List<Integer>> listSingle = Single.fromCallable(this::getIntegersList);

        mDisposable = listSingle.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d(TAG, "onCreate: Single: " + s), s -> Log.e(TAG, "onCreate: Single: Error!"));

        /*
            **Publish Subject**

            PublishSubject emits to a Subscriber
            only those item which are emitted by
            the source Observables subsequent to
            the time of the subscription.

            !!Tip: Subscriber gets only the data
            from the moment it subscribes.

            *Example: Student enters late into the
            classroom and listens from the point of
            time he entered.
        */

        //Create new PublishSubject
        PublishSubject<Integer> publishSubject = PublishSubject.create();

        //Subscriber 1 subscribes to PublishSubject
        publishSubject.subscribe(s -> Log.d(TAG, "onCreate: PublishSubject: Subscriber 1: " + s), s -> Log.e(TAG, "onCreate: PublishSubject: Subscriber 1: Error!"));

        //PublishSubject starts emitting data stream
        publishSubject.onNext(1);
        publishSubject.onNext(2);
        publishSubject.onNext(3);

        //Subscriber 2 subscribes to PublishSubject
        publishSubject.subscribe(d -> Log.d(TAG, "onCreate: PublishSubject: Subscriber 2: " + d), d -> Log.e(TAG, "onCreate: PublishSubject: Subscriber 2: Error!"));

        //PublishSubject is still emitting data stream
        publishSubject.onNext(4);
        publishSubject.onNext(5);

        //PublishSubject completes emitting data stream
        publishSubject.onComplete();

        /*
            **Replay Subject**

            ReplaySubject emits all the items of
            the source Observable, regardless of
            when the Subscriber subscribes

            !!Tip: Subscriber gets all the data
            regardless of the time it subscribes

            *Example: Student enters late into the
            classroom and listens from the beginning.
        */

        //Create new ReplaySubject
        ReplaySubject<Integer> replaySubject = ReplaySubject.create();

        //Subscriber 1 subscribes to ReplaySubject
        replaySubject.subscribe(s -> Log.d(TAG, "onCreate: ReplaySubject: Subscriber 1: " + s), s -> Log.e(TAG, "onCreate: ReplaySubject: Subscriber 1: Error!"));

        //ReplaySubject starts emitting data stream
        replaySubject.onNext(1);
        replaySubject.onNext(2);
        replaySubject.onNext(3);

        //Subscriber 2 subscribes to ReplaySubject
        replaySubject.subscribe(d -> Log.d(TAG, "onCreate: ReplaySubject: Subscriber 2: " + d), d -> Log.e(TAG, "onCreate: ReplaySubject: Subscriber 2: Error!"));

        //ReplaySubject is still emitting data stream
        replaySubject.onNext(4);
        replaySubject.onNext(5);

        //ReplaySubject completes emitting data stream
        replaySubject.onComplete();

        //Subscriber 3 subscribes to ReplaySubject
        replaySubject.subscribe(a -> Log.d(TAG, "onCreate: ReplaySubject: Subscriber 3: " + a), a -> Log.e(TAG, "onCreate: ReplaySubject: Subscriber 3: Error!"));
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
        return new Integer[]{100, 200, 300, 400, 500};
    }

    private String fi(String names, String desc) {
        return String.valueOf(names + ", " + desc);
    }
}
