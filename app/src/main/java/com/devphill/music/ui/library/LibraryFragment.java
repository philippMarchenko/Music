package com.devphill.music.ui.library;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
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
import android.view.inputmethod.InputMethodManager;

import com.devphill.music.JockeyApplication;
import com.devphill.music.R;
import com.devphill.music.data.store.MusicStore;
import com.devphill.music.data.store.PlaylistStore;
import com.devphill.music.data.store.PreferenceStore;
import com.devphill.music.data.store.ThemeStore;
import com.devphill.music.databinding.FragmentLibraryBinding;
import com.devphill.music.ui.BaseFragment;
import com.devphill.music.ui.about.AboutActivity;
import com.devphill.music.ui.library.my_downloads.MyDownloadsFragment;
import com.devphill.music.ui.library.net_songs.NetSongsFragment;
import com.devphill.music.ui.settings.SettingsActivity;
import com.devphill.music.utils.ObjectSerializer;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LibraryFragment extends BaseFragment implements MaterialSearchBar.OnSearchActionListener,
        SuggestionsAdapter.OnItemViewClickListener{

    @Inject MusicStore mMusicStore;
    @Inject PlaylistStore mPlaylistStore;
    @Inject ThemeStore mThemeStore;
    @Inject PreferenceStore mPrefStore;

    private FragmentLibraryBinding mBinding;
    private LibraryViewModel mViewModel;

    private int currentPage = 1;

    private MaterialSearchBar materialSearchBar;

    private ArrayList<String> suggestionList = new ArrayList<>();
    private LibraryPagerAdapter libraryPagerAdapter;

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
       // AppBarLayout appBarLayout = mBinding.libraryAppBarLayout;

     /*   appBarLayout.addOnOffsetChangedListener((layout, verticalOffset) ->
                pager.setPadding(pager.getPaddingLeft(),
                        pager.getPaddingTop(),
                        pager.getPaddingRight(),
                        layout.getTotalScrollRange() + verticalOffset));*/

        mBinding.libraryTabs.setupWithViewPager(mBinding.libraryPager);


     //   FragmentManager fragmentManager = mBinding.get

        libraryPagerAdapter = (LibraryPagerAdapter) mViewModel.getmPagerAdapter();

        NetSongsFragment  netSongsFragment = (NetSongsFragment) libraryPagerAdapter.getItem(0);
        netSongsFragment.setOnChangeStateFragment(new NetSongsFragment.OnChangeStateFragment() {
            @Override
            public void changeFragment(String songUrl) {
                libraryPagerAdapter.update(1,songUrl);
                libraryPagerAdapter.notifyDataSetChanged();
                Log.d("LibraryFragment", "changeFragment " );

            }
        });
       // setupToolbar(mBinding.toolbar);
        setHasOptionsMenu(true);

        materialSearchBar = mBinding.searchBar;
        materialSearchBar.setCardViewElevation(10);

        materialSearchBar.setLastSuggestions(suggestionList);

        materialSearchBar.setOnSearchActionListener(this);
        materialSearchBar.setSuggstionsClickListener(this);

        restoreSuggestionList();

        materialSearchBar.setLastSuggestions(suggestionList);

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

    public void updateNetSongsFragment(){
        libraryPagerAdapter.update(0,"");
        libraryPagerAdapter.notifyDataSetChanged();
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

  /*  private void setupToolbar(Toolbar toolbar) {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_library, menu);

        MenuItem item = menu.findItem(R.id.action_search);
    }


   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_library_settings:
                startActivity(SettingsActivity.newIntent(getContext()));
                return true;
            case R.id.menu_library_search:
             //   startActivity(SearchActivity.newIntent(getContext()));
                return true;
            case R.id.menu_library_about:
                startActivity(AboutActivity.newIntent(getContext()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public void OnItemClickListener(int position, View v) {

        sendDataToFragment(suggestionList.get(position));              //запрос на сервер

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);      //закрываем клаву
        }
        materialSearchBar.disableSearch();

    }

    @Override
    public void OnItemDeleteListener(int position, View v) {

        suggestionList.remove(position);
        materialSearchBar.updateLastSuggestions(suggestionList);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

        if(enabled && currentPage == 0){
            Log.d("LibraryFragment", "showSuggestionsList " );

            materialSearchBar.showSuggestionsList();//покажем подсказки поиска
        }
        else {
            Log.d("LibraryFragment", "hideSuggestionsList " );

           // materialSearchBar.hideSuggestionsList();//иначе закроем
            materialSearchBar.clearSuggestions();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

        sendDataToFragment(text.toString());

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);      //закрываем клаву
        }
        materialSearchBar.disableSearch();

        if(currentPage == 0){
            suggestionList.add(text.toString());
        }

    }

    @Override
    public void onTextChanged(CharSequence text) {

        if (currentPage != 0) {
            sendDataToFragment(text.toString());
        }

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        saveSuggestionList();
    }

    private void restoreSuggestionList() {

        SharedPreferences prefs = getContext().getSharedPreferences("suggestionList", Context.MODE_PRIVATE);
        ArrayList<String> strings = new ArrayList<String>();
        try {
            suggestionList = (ArrayList<String>) ObjectSerializer.deserialize(prefs.getString("LIST", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveSuggestionList(){

        SharedPreferences prefs = getContext().getSharedPreferences("suggestionList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString("LIST", ObjectSerializer.serialize(suggestionList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

}
