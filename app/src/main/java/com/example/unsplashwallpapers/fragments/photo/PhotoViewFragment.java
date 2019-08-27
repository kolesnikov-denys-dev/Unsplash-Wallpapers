package com.example.unsplashwallpapers.fragments.photo;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unsplashwallpapers.R;
import com.example.unsplashwallpapers.util.Utils;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.unsplashwallpapers.util.Constants.DEFAULT_WALLPAPER;
import static com.example.unsplashwallpapers.util.Constants.VIBRATE_TIME;

public class PhotoViewFragment extends Fragment implements com.example.unsplashwallpapers.fragments.photo.PhotoView {

    @BindView(R.id.text_view_download)
    TextView textViewDownload;
    @BindView(R.id.text_view_set_wallpaper)
    TextView textViewSetWallpaper;
    @BindView(R.id.image_view_download)
    ImageView imageViewDownload;
    @BindView(R.id.image_view_set_wallpaper)
    ImageView imageViewSetAsWallpaper;
    @BindView(R.id.photo_view)
    PhotoView photoView;

    public static final String TAG = "PhotoViewFragment";
    public static final String PHOTO_URL = "photo_url";
    private Unbinder unbinder;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private Vibrator vibe;
    private String photoUrl;
    private PhotoViewFragmentPresenter photoViewFragmentPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.photo_view_fragment, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        photoViewFragmentPresenter = new PhotoViewFragmentPresenter(this);
        photoUrl = getArguments().getString(PHOTO_URL, DEFAULT_WALLPAPER);
        Picasso.get().load(photoUrl).into(photoView);
    }

    public void vibe() {
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(VIBRATE_TIME);
    }

    public void setWallpaper(String url) {
        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
                try {
                    wallpaperManager.setBitmap(bitmap);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Toast.makeText(getActivity(), "Wallpaper was installed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Toast.makeText(getActivity(), "Loading image failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                Toast.makeText(getActivity(), "Downloading image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick({R.id.text_view_download, R.id.image_view_download, R.id.text_view_set_wallpaper, R.id.image_view_set_wallpaper})
    void onSaveClick(View view) {
        switch (view.getId()) {
            //Set As Wallpaper
            case R.id.text_view_download:
            case R.id.image_view_download:
                vibe();
                Utils.verifyStoragePermissions(getActivity());
                photoViewFragmentPresenter.downloadImage(photoUrl);
                break;
            //Download image
            case R.id.text_view_set_wallpaper:
            case R.id.image_view_set_wallpaper:
                vibe();
                setWallpaper(photoUrl);
                break;
        }
    }

    @Override
    public void showError() {
        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCompleted() {
        Toast.makeText(getActivity(), "Image saved to gallery", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        Toast.makeText(getActivity(), "Loading", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}