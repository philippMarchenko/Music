package com.devphill.music.ui.library.net_songs;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import static android.support.v7.widget.RecyclerView.*;

public abstract class EndlessRecyclerViewScrollListener extends OnScrollListener {
    // Минимальное количество оставшихся элементов в адаптере, после достижение
    // первого элемента из которых начнется загрузка данных (<a href="#threshold">threshold</a>)
    private int visibleThreshold = 2;
    // Текущий индекс в коллекции загруженных данных
    private int currentPage = 0;
    // Общее количество элементов после предыдущей подгрузки новых данных
    private int previousTotalItemCount = 0;
    // True - если мы в ожидании загрузки свежей порции данных
    private boolean loading = true;
    // Индекс стартовой страницы, с которой начинается загрузка данных
    private int startingPageIndex = 0;

    // это поле также используется когда EndlessRecyclerViewScrollListener используется для GridLayoutManager
    private LinearLayoutManager linearLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.linearLayoutManager = layoutManager;
    }

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        // GridLayoutManager наследуется от LinearLayoutManager и не переопределяет
        // #findLastVisibleItemPosition() и#findFirstVisibleItemPosition методы
        this.linearLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
        this.staggeredGridLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    // Этот метод вызывается каждый раз когда пользователь скроллит список.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        int totalItemCount = 0;

        // Получаем актуальное количество элементов в списке и позицию последнего видимого элемента
        // в зависимости от используемого LayoutManager
        if (linearLayoutManager != null) {
            totalItemCount = linearLayoutManager.getItemCount();
            lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
        } else if (staggeredGridLayoutManager != null) {
            totalItemCount = staggeredGridLayoutManager.getItemCount();
            int[] lastVisibleItemPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
            // get maximum element within the list
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
        }

        // Если до сих пор состоянии загрузки, значит следует проверить изменилось ли количество элементов
        // в списке, и если изменилось - значит загрузка закончилась и следует обновить номер текущей страницы
        // и общее количество элементов
        if (totalItemCount < previousTotalItemCount) {
            currentPage = this.startingPageIndex;
            previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                loading = true;
            }
        }

        // Если до сих пор состояние загрузки, значит следует проверить - изменилось ли количество элементов
        // в списке, и если изменилось - значит загрузка закончилась и следует обновить номер текущей страницы
        // и общее количество элементов
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!loading && (lastVisibleItemPosition + visibleThreshold > totalItemCount)) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount);
            loading = true;
        }
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page, int totalItemsCount);

}