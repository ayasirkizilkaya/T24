package com.ykizilkaya.t24.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.ykizilkaya.t24.R;
import com.ykizilkaya.t24.models.NewsPage;

/**
 * Created by YasirKizilkaya on 8.09.16.
 */

public class NewsFragment extends ParentFragment implements SwipeRefreshLayout.OnRefreshListener {

    private Gson gson = new Gson();
    private TextView pageContentTv, newsTitleTv;
    private ImageView newsPageIv;
    private String shareUrl = "";
    ActionBar supportActionBar;
    TextView txtTitle;

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_news;
    }

    @Override
    public int getToolbarTitle() {
        return R.string.news;
    }

    @Override
    public void loadResults(int resultCode, String getData) {
        if (resultCode == 0) {

            NewsPage mainNews = gson.fromJson(getData, NewsPage.class);

            if(mainNews != null && mainNews.getData() != null && mainNews.getData().getTitle() != null) {

                NewsPage.NewsPageData newsPageData = mainNews.getData();

                /** news content **/
                pageContentTv.setText(Html.fromHtml(newsPageData.getText()));

                /** news title **/
                newsTitleTv.setText(Html.fromHtml(newsPageData.getTitle()));
                //supportActionBar.setTitle(Html.fromHtml(newsPageData.getTitle()));
                txtTitle.setText(Html.fromHtml(newsPageData.getTitle()));


                /** place img **/
                Picasso.with(getActivityFromParent())
                        .load("http://" + newsPageData.getImages().get("page"))
                        .fit().centerCrop().into(newsPageIv);

                /** share url for page **/
                shareUrl = newsPageData.getUrls().get("web");

            } else {
                newsTitleTv.setText(R.string.datanotfound);
                pageContentTv.setText(R.string.datanot);
            }

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        /** home set as up **/
       // supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
       // if(supportActionBar != null) supportActionBar.setDisplayHomeAsUpEnabled(true);


        newsPageIv = (ImageView) view.findViewById(R.id.newsPageIv);
        pageContentTv = (TextView) view.findViewById(R.id.pageContentTv);
        newsTitleTv = (TextView) view.findViewById(R.id.newsTitleTv);

        ImageView imgBack = (ImageView) view.findViewById(R.id.imgBack);
        txtTitle = (TextView) view.findViewById(R.id.titleTv);
        ImageView imgShare = (ImageView) view.findViewById(R.id.imgShare);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!shareUrl.equals("")) shareTextUrl(shareUrl);
            }
        });



        /** get arg of news id **/
        Bundle bundleArgs = getArguments();
        if (bundleArgs  != null && bundleArgs.containsKey("newsId"))  {
            String newsId = bundleArgs.getString("newsId");
            serviceForSync(getStringFromResource(R.string.getnewspage) + newsId, 0);
        }

        /** set listener for refresh layout **/
        getRefreshLayout().setOnRefreshListener(this);

        setHasOptionsMenu(true);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }

    /** create options for fragment **/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /** share option is clicked **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                if(!shareUrl.equals("")) shareTextUrl(shareUrl);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** share news url **/
    private void shareTextUrl(String urlToShare) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.putExtra(Intent.EXTRA_SUBJECT, "Haber Linki");
        share.putExtra(Intent.EXTRA_TEXT, urlToShare);
        startActivity(Intent.createChooser(share, "Haberi Paylaş"));
    }

    /** when refreshed **/
    @Override
    public void onRefresh() {
        getRefreshLayout().setRefreshing(false);
    }

    public static NewsFragment newInstance(String newsId) {
        NewsFragment newsFragment = new NewsFragment();
        Bundle b = new Bundle();
        b.putString("newsId", newsId);
        newsFragment.setArguments(b);
        return newsFragment;
    }


}
