package com.ykizilkaya.t24.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ykizilkaya.t24.NewsActivity;
import com.ykizilkaya.t24.adapter.CustomSpinnerAdapter;
import com.ykizilkaya.t24.infiniteindicator.InfiniteIndicatorLayout;
import com.ykizilkaya.t24.infiniteindicator.slideview.BaseSliderView;
import com.ykizilkaya.t24.utils.EndlessScrollListener;
import com.ykizilkaya.t24.R;
import com.ykizilkaya.t24.adapter.NewsAdapter;
import com.ykizilkaya.t24.infiniteindicator.slideview.DefaultSliderView;
import com.ykizilkaya.t24.models.MainNews;
import com.ykizilkaya.t24.models.NewsCats;
import com.ykizilkaya.t24.utils.Navigator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by YasirKizilkaya on 8.09.16.
 */

public class MainNewsFragment extends ParentFragment implements BaseSliderView.OnSliderClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private Gson gson = new Gson();
    private View fragmentView = null;
    private RecyclerView newsRecyclerView = null;
    private EndlessScrollListener endlessScrollListener = null;
    private boolean fromCats = false;
    private String catId = "";
    private int position = -1;
    private TextView titleNewsTv;
    private Handler handler = null;

    private Toolbar toolbar;

    private Spinner spinner_nav;

    public ArrayList<NewsCats.NewsCat> mNewsCats;
    private boolean mSpinnerInitialized;
    int spinnerPosition = 0;

    /** layout resource **/
    @Override
    public int getLayoutResource() {
        return R.layout.fragment_mainnews;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentView = view;

        toolbar = (Toolbar) fragmentView.findViewById(R.id.toolbar);
        spinner_nav = (Spinner) fragmentView.findViewById(R.id.spinner_nav);
        ImageView imgRefresh = (ImageView) fragmentView.findViewById(R.id.imgRefresh);

        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //openNewsPage(mNewsCats.get(position).getId(),position,mNewsCats.get(position).getName());
                  //  catId = mNewsCats.get(spinnerPosition).getId();
                  // serviceForSync(getStringFromResource(R.string.getbykats) + catId + "&paging=1", 0);
                    serviceForSync(getStringFromResource(R.string.getkats), 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        /** set recler view **/
        titleNewsTv = (TextView) fragmentView.findViewById(R.id.titleNewsTv);
        newsRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivityFromParent());
        newsRecyclerView.setLayoutManager(linearLayoutManager);
        NewsAdapter newsRecyclerAdapter = new NewsAdapter(new ArrayList<MainNews.News>(), getActivityFromParent(), this);
        newsRecyclerView.setAdapter(newsRecyclerAdapter);

        /** endless scroll to load data when scrolling reaches to end **/
        endlessScrollListener = new EndlessScrollListener(linearLayoutManager, newsRecyclerAdapter) {
            @Override
            public void onLoadMore(int currentPage) {
                triggerAppendData(currentPage);
            }
        };
        newsRecyclerView.addOnScrollListener(endlessScrollListener);

        /** if cat is selected from navigation drawer **/
        Bundle bundleArgs = getArguments();
        if (bundleArgs  != null && bundleArgs.containsKey("position") && bundleArgs.containsKey("catId"))  {
            position = bundleArgs.getInt("position");
            spinnerPosition = position;
            catId = bundleArgs.getString("catId");
            serviceForSync(getStringFromResource(R.string.getbykats) + catId + "&paging=1", 0);
            fromCats = true;
        } else {
            /** get news without cat id **/
            serviceForSync(getStringFromResource(R.string.getnews) + endlessScrollListener.getCurrentPage(), 0);
            fromCats = false;
        }

        serviceForSync(getStringFromResource(R.string.getkats), 2);

        /** set listener for refresh layout **/
        getRefreshLayout().setOnRefreshListener(this);

        setHasOptionsMenu(false);

    }


    @Override
    public int getToolbarTitle() {
        return R.string.news;
    }

    /** load results from services **/
    @Override
    public void loadResults(int resultCode, String getData) {
        if (resultCode == 0) loadNews(getData);
        else if(resultCode == 1) appendNews(getData);
        else if(resultCode == 2) placeDrawersData(getData);
        else if(resultCode == 3) checkForUpdate(getData, true);
        else if(resultCode == 4) checkForUpdate(getData, false);
    }

    /** data to update news **/
    private void checkForUpdate(String getData, boolean smoothScrollToFirstPosition) {
        MainNews mainNews = gson.fromJson(getData, MainNews.class);
        if (mainNews != null) {

            /** get loaded news from recylerview's adapter **/
            NewsAdapter newsRecyclerAdapter = (NewsAdapter) newsRecyclerView.getAdapter();
            ArrayList<MainNews.News> newsDataOfAdapter = newsRecyclerAdapter.getNews();

            /** updated data list **/
            ArrayList<MainNews.News> updatedDataList = new ArrayList<>();

            /**
             * updated data just arrived, put ids of it to a hashmap
             * and control if there is a new data, control by their id
             **/
            ArrayList<MainNews.News> updatedDatas = mainNews.getData();

            /** new data size **/
            int updatedDatasSize = updatedDatas.size();

            /** old data id hash **/
            HashMap<String, Integer> oldDataId = new HashMap<>();

            for (int i = 0; i < updatedDatasSize; i++) {

                /** ids of old datas from adapter **/
                if(!newsDataOfAdapter.get(i).isFooter()) oldDataId.put(newsDataOfAdapter.get(i).getId(), 1);

                /** if old and new value equals, than there is no new data available **/
                if(oldDataId.containsKey(updatedDatas.get(i).getId())) break;

                /** new list with updated news **/
                else updatedDataList.add(updatedDatas.get(i));
            }

            /** updated data list size **/
            int updatedDataListSize = updatedDataList.size();

            if(updatedDataListSize > 0) {

                /** fill arraylist **/
                for (int g = 0; g < updatedDataListSize; g++) newsDataOfAdapter.add(g, updatedDataList.get(g));

                /** notify item inserted **/
                newsRecyclerAdapter.notifyItemRangeInserted(0, updatedDataListSize);

                /** smooth scroll to first position **/
                if(smoothScrollToFirstPosition) newsRecyclerView.smoothScrollToPosition(0);

                /** warn user that new data has been arrived **/
                else showToast(updatedDataListSize + " " + getStringFromResource(R.string.shownewdataarrived));

            }

        }
    }

    /** place navigation drawers data **/
    private void placeDrawersData(String getData) {
        /** get category data from gson **/
        NewsCats newsCatsResult = gson.fromJson(getData, NewsCats.class);
        if (newsCatsResult != null) {
            // set category spinner
            addItemsToSpinner(newsCatsResult.getData());
        }

    }

    /** append news with scroll **/
    private void appendNews(String getData) {
        MainNews appendMainNews = gson.fromJson(getData, MainNews.class);
        if (appendMainNews != null) {

            /** get data to append **/
            ArrayList<MainNews.News> appendListData = appendMainNews.getData();
            int appendMainNewsSize = appendListData.size();
            NewsAdapter newsRecyclerAdapter = (NewsAdapter) newsRecyclerView.getAdapter();

            /** stop when we get all data **/
            if(appendMainNewsSize == 0 && newsRecyclerAdapter.isLastFooter()) { newsRecyclerAdapter.removeLast(); return; }
            else if(appendMainNewsSize == 0) return;

            /** set if downloading or increase current page **/
            if(appendMainNewsSize != 10) endlessScrollListener.setLoading(false);
            else endlessScrollListener.increaseCurrentPage();

            /** remove footer **/
            newsRecyclerAdapter.removeLast();

            /** start appending **/
            for (MainNews.News appendData : appendListData) newsRecyclerAdapter.addItem(appendData);

            /** add footer **/
            newsRecyclerAdapter.addItem(new MainNews.News(true));

            /** insert item range **/
            newsRecyclerAdapter.itemRangeInserted(appendListData.size());

        }
    }

    /** load news for the first time **/
    private void loadNews(String getData) {
        MainNews mainNews = gson.fromJson(getData, MainNews.class);
        if (mainNews != null) {
            loadData(mainNews.getData());
            final ArrayList<MainNews.News> newsDatas = mainNews.getData();
            /** first title **/
            titleNewsTv.setText(Html.fromHtml(newsDatas.get(0).getTitle()));

            /** set slider for use **/
            final InfiniteIndicatorLayout mDefaultIndicator = (InfiniteIndicatorLayout) fragmentView.findViewById(R.id.indicator_default_circle);
            for(MainNews.News newDatas : newsDatas){
                if(newDatas.getImages() != null && newDatas.getImages().get("page") != null) {
                    DefaultSliderView textSliderView = new DefaultSliderView(getActivityFromParent());

                    /** add some image to display **/
                    textSliderView
                        .image("http://" + newDatas.getImages().get("page"))
                        .setScaleType(BaseSliderView.ScaleType.Fit).setOnSliderClickListener(this);

                    /** add id to go to news page by id **/
                    textSliderView.getBundle()
                            .putString("extra", newDatas.getId());
                    textSliderView.getBundle()
                            .putString("catId", newDatas.getCategory().get("id"));
                    mDefaultIndicator.addSlider(textSliderView);
                }
            }

            /** position circle indicators **/
            mDefaultIndicator.setIndicatorPosition(InfiniteIndicatorLayout.IndicatorPosition.Center_Bottom);

            /** start scrolling automatically **/
            mDefaultIndicator.startAutoScroll();

            /** listener for page changes, so the title can change **/
            mDefaultIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {
                    /** change title accoring to slides **/
                    titleNewsTv.setText(Html.fromHtml(newsDatas.get(mDefaultIndicator.getPosition(position)).getTitle()));
                }

                @Override
                public void onPageScrollStateChanged(int state) {}

            });

        }
    }


    private int lastPage = -1;
    public void triggerAppendData(int currentPage) {
        if (currentPage != lastPage) {
            /** if its not from cats **/
            if(!fromCats) serviceForSync(getStringFromResource(R.string.getnews) + currentPage, 1);

            /** if its from cats **/
            else if(!catId.equals("")) serviceForSync(getStringFromResource(R.string.getbykats) + catId + "&paging=" + currentPage, 1);

        }
        lastPage = currentPage;
    }

    // add items into spinner dynamically
    public void addItemsToSpinner(ArrayList<NewsCats.NewsCat> newsCats) {

        mNewsCats = newsCats;

        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(getActivity().getApplicationContext(), newsCats);
        spinner_nav.setAdapter(spinAdapter);
        spinner_nav.setSelection(spinnerPosition);


        spinner_nav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                if (!mSpinnerInitialized) {
                    mSpinnerInitialized = true;
                    return;
                }
                spinnerPosition = position;

                openNewsPage(mNewsCats.get(position).getId(),position,mNewsCats.get(position).getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });


    }

    /** load data to recyler view first time **/
    public void loadData(ArrayList<MainNews.News> news) {
        endlessScrollListener.setLoading(true);
        endlessScrollListener.setCurrentPage(2);
        NewsAdapter newsRecyclerAdapter = (NewsAdapter) newsRecyclerView.getAdapter();
        int newsRecyclerAdapterItemCount = newsRecyclerAdapter.getItemCount();
        if (newsRecyclerAdapterItemCount > 0) {
            newsRecyclerAdapter.clearListData();
            newsRecyclerAdapter.notifyItemRangeRemoved(0, newsRecyclerAdapterItemCount);
        }

        /** add footer **/
        news.add(new MainNews.News(true));
        newsRecyclerAdapter.setItem(news);
        newsRecyclerAdapter.notiftyChange();
    }

    /** open news page by their id **/
    public void openNewsPage(String newsId) {
//        Fragment newsFragment = new NewsFragment();
        NewsAdapter newsRecyclerAdapter = (NewsAdapter) newsRecyclerView.getAdapter();

        /** limit and news id, scroll to id **/
        int limitForAdapter = newsRecyclerAdapter.getLimit();
        int newsIdInt = Integer.parseInt(newsId);
        if(newsIdInt >= limitForAdapter) limitForAdapter = newsIdInt + 1;

        Fragment newsFragment = new NewsPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("newsId", newsId);
        bundle.putInt("limit", limitForAdapter);
        newsFragment.setArguments(bundle);
        ((NewsActivity) getActivityFromParent()).startTransactionFromActivity(newsFragment, true, false);
    }

    /** when slider is clicked go to news page **/
    @Override
    public void onSliderClick(BaseSliderView slider) {
        if(slider.getBundle() != null && slider.getBundle().containsKey("extra"))
            openNewsPage(slider.getBundle().getString("extra"));
    }

    /** when refresh happens, to update data **/
    @Override
    public void onRefresh() {
        serviceForSync(getStringFromResource(R.string.getnews) + 1 + "&limit=10", 3);
    }

    /** runnable for sync **/
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            serviceForSync(getStringFromResource(R.string.getnews) + 1 + "&limit=10", 4);
            handler = new Handler();
            handler.postDelayed(this, 120000);
        }
    };

    /** start sycning data **/
    private void startSycn() {
        handler = new Handler();
        handler.postDelayed(runnable, 120000);
    }

    /** start sycning when resumed **/
    @Override
    public void onResume() {
        super.onResume();
        startSycn();
    }

    /** stop syncing when paused **/
    @Override
    public void onPause() {
        super.onPause();
        if(handler != null) handler.removeCallbacks(runnable);
        handler = null;
    }

    /** open news page **/
    private void openNewsPage(String catId, int position, String name) {
        Fragment newsFragment = new MainNewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("catId", catId);
        bundle.putInt("position", position);
        bundle.putString("name", name);
        newsFragment.setArguments(bundle);


        Navigator navigator = new Navigator(getActivity().getSupportFragmentManager(), (AppCompatActivity) getActivity());
        navigator.startFragmentTransaction(newsFragment, false, true);
    }


}
