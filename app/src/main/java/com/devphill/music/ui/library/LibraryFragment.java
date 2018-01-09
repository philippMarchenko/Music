package com.devphill.music.ui.library;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.devphill.music.data.store.PreferenceStore;
import com.devphill.music.data.store.ThemeStore;
import com.devphill.music.databinding.FragmentLibraryBinding;
import com.devphill.music.ui.BaseFragment;
import com.devphill.music.ui.SampleSuggestionsBuilder;
import com.devphill.music.ui.SimpleAnimationListener;
import com.devphill.music.ui.about.AboutActivity;
import com.devphill.music.ui.library.my_downloads.MyDownloadsFragment;
import com.devphill.music.ui.library.net_songs.NetSongsFragment;
import com.devphill.music.ui.settings.SettingsActivity;
import com.devphill.music.utils.ObjectSerializer;
import com.mancj.materialsearchbar.MaterialSearchBar;


import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.SearchItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LibraryFragment extends BaseFragment{

    @Inject MusicStore mMusicStore;
    @Inject PlaylistStore mPlaylistStore;
    @Inject ThemeStore mThemeStore;
    @Inject PreferenceStore mPrefStore;

    private FragmentLibraryBinding mBinding;
    private LibraryViewModel mViewModel;

    private int currentPage = 1;

    MaterialSearchBar materialSearchBar;

    private ArrayList<String> suggestionList = new ArrayList<String>();

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JockeyApplication.getComponent(this).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = FragmentLibraryBinding.inflate(inflater, container, false);
        mViewModel = new LibraryViewModel(this, mPrefStore, mThemeStore, mMusicStore, mPlaylistStore);
        mBinding.setViewModel(mViewModel);

        mMusicStore.loadAll();
        mPlaylistStore.loadPlaylists();

        ViewPager pager = mBinding.libraryPager;
        AppBarLayout appBarLayout = mBinding.libraryAppBarLayout;

        appBarLayout.addOnOffsetChangedListener((layout, verticalOffset) ->
                pager.setPadding(pager.getPaddingLeft(),
                        pager.getPaddingTop(),
                        pager.getPaddingRight(),
                        layout.getTotalScrollRange() + verticalOffset));

        mBinding.libraryTabs.setupWithViewPager(mBinding.libraryPager);

        setupToolbar(mBinding.toolbar);
        setHasOptionsMenu(true);

        suggestionList.add("ewgwqeg");
        suggestionList.add("wgweg");
        suggestionList.add("ewgwegwQWwqeg");

        materialSearchBar = mBinding.searchBar;





     //   materialSearchBar = mBinding.searchBar;
     /*   materialSearchBar.setHint("Ведите название...");
        materialSearchBar.setSpeechMode(true);
        materialSearchBar.setElevation(0);*/


      /*  materialSearchView = mBinding.searchView;
        materialSearchView.setBackgroundColor(getResources().getColor(R.color.primary));

        materialSearchView.setHint("Ведите название...");



        String[]  suggestionArr = new String[suggestionList.size()];
        suggestionArr = suggestionList.toArray(suggestionArr);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.suggestion,R.id.textView,
                suggestionArr);

        materialSearchView.setSuggestions(suggestionList.toArray(new String[0]));
        materialSearchView.setAdapter(adapter);
        materialSearchView.setOnItemClickListener((parent, view, position, id) -> {
            String query = (String) parent.getItemAtPosition(position);
            materialSearchView.closeSearch();
        });

        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sendDataToFragment(query);
                suggestionList.add(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                sendDataToFragment(newText);
                return false;
            }
        });

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                materialSearchView.showSuggestions();
            }

            @Override
            public void onSearchViewClosed() {
                materialSearchView.dismissSuggestions();
            }
        });*/




        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("LibraryFragment", "onPageScrolled " + position);

                currentPage = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return mBinding.getRoot();
    }

    private void sendDataToFragment(String text){
        Intent intent = null;
        switch(currentPage){
            case 0:
                intent = new Intent(NetSongsFragment.SEARCH_NET_SONGS);
                break;
            case 1:
                intent = new Intent(SongFragment.SEARCH_ALL_SONGS);
                break;
            case 2:
                intent = new Intent(MyDownloadsFragment.SEARCH_MY_DOWNLOADED);
                break;
            case 3:
                break;
            default:
                break;

        }
        intent.putExtra("search", text);
        getContext().sendBroadcast(intent);
    }

    private void setupToolbar(Toolbar toolbar) {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_library, menu);

      //  View  menuItem = (View) menu.findItem(R.id.menu_library_search);
      //  View menuItemView = findViewById(R.id.action_search);

      //  mSearchView.openSearch(menuItemView);

       /* FakeSearchView fakeSearchView = (FakeSearchView) MenuItemCompat.getActionView(menuItem);
        fakeSearchView.setOnSearchListener(this);*/

      //  MenuItem item = menu.findItem(R.id.action_search);
      //  materialSearchView.setMenuItem(item);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_library_settings:
                startActivity(SettingsActivity.newIntent(getContext()));
                return true;
            case R.id.action_search:
             //   startActivity(SearchActivity.newIntent(getContext()));
                mSearchView.openSearch();
                return true;
            case R.id.menu_library_about:
                startActivity(AboutActivity.newIntent(getContext()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void restoreSuggestionList () {

        // load list from preference
        SharedPreferences prefs = getContext().getSharedPreferences("suggestionList", Context.MODE_PRIVATE);
        ArrayList<String> strings = new ArrayList<String>();
        try {
            suggestionList = (ArrayList<String>) ObjectSerializer.deserialize(prefs.getString("LIST", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        SharedPreferences prefs = getContext().getSharedPreferences("suggestionList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString("LIST",ObjectSerializer.serialize(suggestionList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();

    }
/*    @Override
    public void onSearch(FakeSearchView fakeSearchView, CharSequence constraint) {
        Log.d("LibraryFragment", "onSearch " + constraint.toString());

        Intent intent = null;
        switch(currentPage){
            case 0:
                intent = new Intent(NetSongsFragment.SEARCH_NET_SONGS);
                break;
            case 1:
                intent = new Intent(SongFragment.SEARCH_ALL_SONGS);
                break;
            case 2:
                intent = new Intent(MyDownloadsFragment.SEARCH_MY_DOWNLOADED);
                break;
            case 3:
                break;
            default:
                break;

        }
        intent.putExtra("search", constraint);
        getContext().sendBroadcast(intent);
    }
    @Override
    public void onSearchHint(FakeSearchView fakeSearchView, CharSequence constraint) {
        Log.d("LibraryFragment", "onSearchHint " + constraint.toString());

        Intent intent = null;
        switch(currentPage){
            case 0:
                intent = new Intent(NetSongsFragment.SEARCH_NET_SONGS);
                break;
            case 1:
                intent = new Intent(SongFragment.SEARCH_ALL_SONGS);
                break;
            case 2:
                intent = new Intent(MyDownloadsFragment.SEARCH_MY_DOWNLOADED);
                break;
            case 3:
                break;
            default:
                break;

        }

        intent.putExtra("search", constraint);
        getContext().sendBroadcast(intent);
        fakeSearchView.clearFocus();
    }*/

}
