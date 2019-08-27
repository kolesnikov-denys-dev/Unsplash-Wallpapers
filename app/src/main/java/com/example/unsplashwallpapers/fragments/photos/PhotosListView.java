package com.example.unsplashwallpapers.fragments.photos;

import com.example.unsplashwallpapers.data.model.Result;

import java.util.List;

public interface PhotosListView {

    void showData(List<Result> results);

    void showError();

}
