package com.devphill.music.ui.library.folders;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.devphill.music.R;
import com.devphill.music.databinding.InstanceFolderBinding;
import com.devphill.music.ui.BaseActivity;
import com.devphill.music.ui.BaseFragment;
import com.marverenic.adapter.EnhancedViewHolder;
import com.marverenic.adapter.HeterogeneousAdapter;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;
import java.util.List;

public class FoldersSection extends HeterogeneousAdapter.ListSection<File>
        implements FastScrollRecyclerView.MeasurableAdapter {

    private BaseActivity mActivity;
    private BaseFragment mFragment;
    private Context context;
    FolderViewModel folderViewModel;
    FolderViewModel.FolderAdapterListener mFolderAdapterListener;

    public static final String LOG_TAG = "FoldersSection";


/*    public FoldersSection(BaseFragment fragment,Context context, @NonNull List<File> data) {
        super(data);
        mFragment = fragment;
        this.context = context;

    }*/
    public FoldersSection(@NonNull List<File> data,FolderViewModel.FolderAdapterListener folderAdapterListener,Context context) {
        super(data);
        this.context = context;
        mFolderAdapterListener = folderAdapterListener;
    }


    @Override
    public int getViewTypeHeight(RecyclerView recyclerView, int viewType) {
        return recyclerView.getResources().getDimensionPixelSize(R.dimen.list_height)
                + recyclerView.getResources().getDimensionPixelSize(R.dimen.divider_height);
    }

    @Override
    public EnhancedViewHolder<File> createViewHolder(HeterogeneousAdapter adapter, ViewGroup parent) {
        InstanceFolderBinding binding = InstanceFolderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding,getData());    }

    private class ViewHolder extends EnhancedViewHolder<File> {

        private InstanceFolderBinding mBinding;

        public ViewHolder(InstanceFolderBinding binding,List<File> data) {
            super(binding.getRoot());
            mBinding = binding;

            if (mBinding != null) {
                binding.setViewModel(new FolderViewModel(data,context,mFolderAdapterListener));
            } else {
                throw new RuntimeException("Unable to create view model. This SongSection has not "
                        + "been created with a valid activity or fragment");
            }
        }

        @Override
        public void onUpdate(File item, int position) {
            mBinding.getViewModel().setFolder(getData(), position);
            mBinding.executePendingBindings();
            Log.d(LOG_TAG, "onUpdate " + getData().get(position).getName() );

        }
    }
}
