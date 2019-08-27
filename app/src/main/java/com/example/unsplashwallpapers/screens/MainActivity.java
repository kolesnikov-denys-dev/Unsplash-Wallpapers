package com.example.unsplashwallpapers.screens;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.unsplashwallpapers.fragments.photos.PhotosFragment;
import com.example.unsplashwallpapers.util.Utils;
import com.example.unsplashwallpapers.R;

public class MainActivity extends FragmentActivity {

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private PhotosFragment photosFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.verifyStoragePermissions(this);
        photosFragment = new PhotosFragment();
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        if (manager.findFragmentByTag(PhotosFragment.TAG) == null) {
            transaction.add(R.id.container, photosFragment, PhotosFragment.TAG);
        }
        transaction.commit();
    }
}
