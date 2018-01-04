package com.devphill.music.ui.library;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.marverenic.adapter.EnhancedViewHolder;
import com.marverenic.adapter.HeterogeneousAdapter;
import com.devphill.music.R;
import com.devphill.music.databinding.InstanceGenreBinding;
import com.devphill.music.model.Genre;
import com.devphill.music.model.ModelUtil;
import com.devphill.music.ui.library.GenreViewModel;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView.MeasurableAdapter;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView.SectionedAdapter;

import java.util.List;

public class GenreSection extends HeterogeneousAdapter.ListSection<Genre>
        implements SectionedAdapter, MeasurableAdapter {

    private FragmentManager mFragmentManager;

    public GenreSection(AppCompatActivity activity, @NonNull List<Genre> data) {
        this(activity.getSupportFragmentManager(), data);
    }

    public GenreSection(Fragment fragment, @NonNull List<Genre> data) {
        this(fragment.getFragmentManager(), data);
    }

    public GenreSection(FragmentManager fragmentManager, @NonNull List<Genre> data) {
        super(data);
        mFragmentManager = fragmentManager;
    }

    @Override
    public int getId(int position) {
        return (int) get(position).getGenreId();
    }

    @Override
    public EnhancedViewHolder<Genre> createViewHolder(HeterogeneousAdapter adapter,
                                                      ViewGroup parent) {
        InstanceGenreBinding binding = InstanceGenreBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        char firstChar = ModelUtil.sortableTitle(get(position).getGenreName()).charAt(0);
        return Character.toString(firstChar).toUpperCase();
    }

    @Override
    public int getViewTypeHeight(RecyclerView recyclerView, int viewType) {
        return recyclerView.getResources().getDimensionPixelSize(R.dimen.list_height)
                + recyclerView.getResources().getDimensionPixelSize(R.dimen.divider_height);
    }

    private class ViewHolder extends EnhancedViewHolder<Genre> {

        private InstanceGenreBinding mBinding;

        public ViewHolder(InstanceGenreBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewModel(new GenreViewModel(itemView.getContext(), mFragmentManager));
        }

        @Override
        public void onUpdate(Genre item, int sectionPosition) {
            mBinding.getViewModel().setGenre(item);
            mBinding.executePendingBindings();
        }
    }
}
