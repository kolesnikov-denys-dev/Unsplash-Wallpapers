package com.example.unsplashwallpapers.fragments.photos;

import com.example.unsplashwallpapers.data.model.Request;
import com.example.unsplashwallpapers.data.model.Result;
import com.example.unsplashwallpapers.data.retrofit.ApiFactory;
import com.example.unsplashwallpapers.data.retrofit.ApiService;
import com.example.unsplashwallpapers.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.unsplashwallpapers.util.Constants.CLIENT_ID;
import static com.example.unsplashwallpapers.util.Constants.PAGE;
import static com.example.unsplashwallpapers.util.Constants.QUERY;
import static com.example.unsplashwallpapers.util.Constants.UNSPLASH_API_KEY;

public class PhotosFragmentPresenter {

    private CompositeDisposable compositeDisposable;
    private PhotosListView viewAllPhotos;

    public PhotosFragmentPresenter(PhotosListView viewAllPhotos) {
        this.viewAllPhotos = viewAllPhotos;
        compositeDisposable = new CompositeDisposable();
    }

    public void loadSimpleData(int page) {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        Map<String, String> params = new HashMap<>();
        params.put(PAGE, Integer.toString(page));
        params.put(CLIENT_ID, UNSPLASH_API_KEY);
        Disposable disposableSimpleData = apiService.getPhotos(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Result>>() {
                    @Override
                    public void accept(List<Result> examples) throws Exception {
                        viewAllPhotos.showData(examples);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        viewAllPhotos.showError();
                    }
                });
        compositeDisposable.add(disposableSimpleData);
    }

    public void loadSearchDate(int page, String query) {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        Map<String, String> params = new HashMap<>();
        params.put(PAGE, Integer.toString(page));
        params.put(QUERY, query);
        params.put(CLIENT_ID, UNSPLASH_API_KEY);
        Disposable disposableSearchData = apiService.getSearchPhotos(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Request>() {
                    @Override
                    public void accept(Request request) throws Exception {
                        List<Result> list = request.getResults();
                        viewAllPhotos.showData(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        viewAllPhotos.showError();
                    }
                });
        compositeDisposable.add(disposableSearchData);
    }

    public void disposeDisposable() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

}
