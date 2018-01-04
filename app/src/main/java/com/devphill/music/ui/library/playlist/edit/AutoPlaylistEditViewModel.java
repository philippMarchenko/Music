package com.devphill.music.ui.library.playlist.edit;

import android.databinding.Bindable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.marverenic.adapter.HeterogeneousAdapter;
import com.devphill.music.BR;
import com.devphill.music.model.AutoPlaylist;
import com.devphill.music.model.playlistrules.AutoPlaylistRule;
import com.devphill.music.ui.BaseViewModel;
import com.devphill.music.view.BackgroundDecoration;
import com.devphill.music.view.DividerDecoration;
import com.trello.rxlifecycle.components.support.RxFragment;

public class AutoPlaylistEditViewModel extends BaseViewModel {

    private AutoPlaylist mOriginalPlaylist;
    private AutoPlaylist.Builder mEditedPlaylist;
    private int mScrollPosition;

    private HeterogeneousAdapter mAdapter;

    public AutoPlaylistEditViewModel(RxFragment fragment, AutoPlaylist originalPlaylist,
                                     AutoPlaylist.Builder editedPlaylist) {
        super(fragment);
        mOriginalPlaylist = originalPlaylist;
        mEditedPlaylist = editedPlaylist;

        createAdapter();
    }

    private void createAdapter() {
        mAdapter = new HeterogeneousAdapter();

        mAdapter.addSection(new RuleHeaderSingleton(mOriginalPlaylist, mEditedPlaylist));
        mAdapter.addSection(new RuleSection(mEditedPlaylist.getRules()));
    }

    @Bindable
    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    @Bindable
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Bindable
    public RecyclerView.ItemDecoration[] getItemDecorations() {
        return new RecyclerView.ItemDecoration[] {
                new BackgroundDecoration(),
                new DividerDecoration(getContext())
        };
    }

    @Bindable
    public int getScrollPosition() {
        return mScrollPosition;
    }

    public void setScrollPosition(int scrollY) {
        if (scrollY != mScrollPosition) {
            mScrollPosition = scrollY;
            notifyPropertyChanged(BR.scrollPosition);
        }
    }

    public void addRule() {
        mEditedPlaylist.getRules().add(AutoPlaylistRule.emptyRule());
        mAdapter.notifyItemInserted(mEditedPlaylist.getRules().size());
    }

    public void focusPlaylistName() {
        setScrollPosition(0);
    }

}
