package com.example.unsplashwallpapers.fragments.photos;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.unsplashwallpapers.R;
import com.example.unsplashwallpapers.adapter.PhotoAdapder;
import com.example.unsplashwallpapers.data.PhotosViewModel;
import com.example.unsplashwallpapers.data.model.Result;
import com.example.unsplashwallpapers.fragments.photo.PhotoViewFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

import static com.example.unsplashwallpapers.fragments.photo.PhotoViewFragment.PHOTO_URL;
import static com.example.unsplashwallpapers.util.Constants.IMAGE_DENSITY;
import static com.example.unsplashwallpapers.util.Constants.IMAGE_DENSITY_DIVIDER;

public class PhotosFragment extends Fragment implements PhotosListView {

    @BindView(R.id.image_view_search)
    ImageView imageViewSearch;
    @BindView(R.id.edit_text_search)
    EditText editTextSearch;
    @BindView(R.id.recycler_view_photo)
    RecyclerView recyclerView;

    public static final String TAG = "PhotosFragment";
    private Unbinder unbinder;
    private PhotoAdapder adapder;
    private int pageCounter = 1;
    private boolean isLoading = false;
    private boolean isSearch = false;
    private PhotoViewFragment photoViewFragment;
    private PhotosFragmentPresenter photosFragmentPresenter;
    private PhotosViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.photos_fragment, container, false);
        unbinder = ButterKnife.bind(this, v);
        setRetainInstance(true);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pageCounter = 1;
        editTextSearch.requestFocus();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getColumnCount()));
        adapder = new PhotoAdapder();
        recyclerView.setAdapter(adapder);
        adapder.setPhotos(new ArrayList<Result>());
        photosFragmentPresenter = new PhotosFragmentPresenter(this);
        viewModel = ViewModelProviders.of(this).get(PhotosViewModel.class);
        viewModel.getPhotos().observe(this, new Observer<List<Result>>() {
            @Override
            public void onChanged(@Nullable List<Result> results) {
                adapder.setPhotos(results);
            }
        });
        if (!isLoading && !isSearch) {
            photosFragmentPresenter.loadSimpleData(pageCounter);
        }

        adapder.setOnPhotoClickListener(new PhotoAdapder.OnPhotoClickListener() {
            @Override
            public void onDayClick(int position) {
                photoViewFragment = new PhotoViewFragment();
                //send link
                Bundle bundle = new Bundle();
                bundle.putString(PHOTO_URL, adapder.getPhotos().get(position).getUrls().getRegular());
                photoViewFragment.setArguments(bundle);
                //open big photo
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, photoViewFragment, PhotoViewFragment.TAG)
                        .addToBackStack(PhotoViewFragment.TAG)
                        .commit();
            }
        });

        adapder.setOnReachEndListener(new PhotoAdapder.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if (!isLoading && isSearch) {
                    photosFragmentPresenter.loadSearchDate(pageCounter, editTextSearch.getText().toString());
                }
                if (!isLoading && !isSearch) {
                    photosFragmentPresenter.loadSimpleData(pageCounter);
                }
            }
        });
    }

    @OnTextChanged(R.id.edit_text_search)
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        viewModel.deleteAllPhotos();
        adapder.clear();
        if (s.length() >= 1) {
            imageViewSearch.setImageResource(R.drawable.ic_close_white_24dp);
            isSearch = true;
        }
        if (s.length() == 0) {
            imageViewSearch.setImageResource(R.drawable.ic_search_white_24dp);
            isSearch = false;
        }
        if (isSearch && !isLoading) {
            pageCounter = 1;
            photosFragmentPresenter.loadSearchDate(pageCounter, editTextSearch.getText().toString());
        }
    }

    @OnClick(R.id.image_view_search)
    void onSearchClick() {
        viewModel.deleteAllPhotos();
        isSearch = false;
        adapder.clear();
        imageViewSearch.setImageResource(R.drawable.ic_search_white_24dp);
        editTextSearch.setText("");
        if (!isLoading && !isSearch) {
            pageCounter = 1;
            photosFragmentPresenter.loadSimpleData(pageCounter);
        }
    }

    public int getColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / IMAGE_DENSITY > IMAGE_DENSITY_DIVIDER ? width / IMAGE_DENSITY : IMAGE_DENSITY_DIVIDER;
    }

    @Override
    public void showData(List<Result> results) {
        viewModel.insertPhotos(results);
        adapder.setPhotos(results);
        pageCounter++;
    }

    @Override
    public void showError() {
        Toast.makeText(getContext(), "Internet problem", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        photosFragmentPresenter.disposeDisposable();
        super.onDestroy();
    }
}

