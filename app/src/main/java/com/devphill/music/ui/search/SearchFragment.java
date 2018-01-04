package com.devphill.music.ui.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.devphill.music.JockeyApplication;
import com.devphill.music.R;
import com.devphill.music.data.store.MusicStore;
import com.devphill.music.data.store.PlaylistStore;
import com.devphill.music.databinding.FragmentSearchBinding;
import com.devphill.music.ui.BaseToolbarFragment;
import com.devphill.music.utils.StringUtils;

import javax.inject.Inject;

public class SearchFragment extends BaseToolbarFragment {

    private static final String KEY_SAVED_QUERY = "SearchActivity.LAST_QUERY";

    @Inject MusicStore mMusicStore;
    @Inject PlaylistStore mPlaylistStore;

    private FragmentSearchBinding mBinding;
    private SearchViewModel mViewModel;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    protected String getFragmentTitle() {
        return getString(R.string.header_search);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JockeyApplication.getComponent(this).inject(this);
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {

        mBinding = FragmentSearchBinding.inflate(inflater, container, false);
        mViewModel = new SearchViewModel(this, mMusicStore, mPlaylistStore);

        if (savedInstanceState != null) {
            String lastQuery = savedInstanceState.getString(KEY_SAVED_QUERY, "");
            mViewModel.setSearchQuery(lastQuery);
        }

        mBinding.setViewModel(mViewModel);
        setHasOptionsMenu(true);

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_search, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_library_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mViewModel.setSearchQuery(newText);
                return true;
            }
        });

        searchView.setIconified(false);
        if (!StringUtils.isEmpty(mViewModel.getSearchQuery())) {
            searchView.setQuery(mViewModel.getSearchQuery(), true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SAVED_QUERY, mViewModel.getSearchQuery());
    }

    @Override
    protected boolean canNavigateUp() {
        return true;
    }

    public void setSearchQuery(String query) {
        mViewModel.setSearchQuery(query);
    }
}
