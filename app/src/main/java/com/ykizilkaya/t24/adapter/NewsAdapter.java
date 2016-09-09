package com.ykizilkaya.t24.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.ykizilkaya.t24.fragments.MainNewsFragment;
import com.ykizilkaya.t24.R;
import com.ykizilkaya.t24.models.MainNews;

import java.util.ArrayList;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private ArrayList<MainNews.News> mNews;
    private Activity mActivity;
    private MainNewsFragment mMainNewsFragment = null;

    public NewsAdapter(ArrayList<MainNews.News> news, Activity activity, MainNewsFragment mainNewsFragment) {
        mNews = news;
        mActivity = activity;
        mMainNewsFragment = mainNewsFragment;
    }

    /** clear all from array list **/
    public void clearListData() {
        mNews.clear();
    }

    /** get view type footer or item ? **/
    @Override
    public int getItemViewType(int position) {
        if(mNews.get(position).isFooter()) return -2;
        else return -1;
    }

    /** create view **/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == -2) view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_append, parent, false);
        else view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        /** when clicked, go to news page by id **/
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    mMainNewsFragment.openNewsPage(mNews.get(position).getId());

                }
            }
        });

        return viewHolder;
    }

    /** bind views **/
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MainNews.News news = mNews.get(position);
        if(news.isFooter()) return;

        Picasso.with(mActivity)
                .load("http://" + news.getImages().get("grid"))
                .fit().centerCrop().into(holder.newGridIm);

        holder.newsTitleTv.setText(Html.fromHtml(news.getTitle()));

        holder.publishingDateTv.setText(news.getPublishingDate());


    }

    /** how many items in the list **/
    @Override
    public int getItemCount() {
        return mNews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView newGridIm;
        public final TextView newsTitleTv, publishingDateTv;

        public ViewHolder(View itemView) {
            super(itemView);
            newGridIm = (ImageView) itemView.findViewById(R.id.newGridIm);
            newsTitleTv = (TextView) itemView.findViewById(R.id.newsTitleTv);
            publishingDateTv = (TextView) itemView.findViewById(R.id.publishingDateTv);
        }

    }

    /** get limit **/
    public int getLimit() { return Integer.parseInt(mNews.get(0).getId()) + 1; }

    /** add item **/
    public void addItem(MainNews.News _new) { mNews.add(_new); }

    /** set new items to list **/
    public void setItem(ArrayList<MainNews.News> news) { mNews = news; }

    /** notify if item is inserted **/
    public void notiftyChange() { notifyItemInserted(getItemCount() - 1); }

    /** when item range is inserted **/
    public void itemRangeInserted(int listSize) {
        notifyItemRangeInserted(getItemCount() - listSize, listSize);
    }

    /** remove last item from list **/
    public void removeLast() {
        int sizeOfList = getItemCount();
        mNews.remove(sizeOfList - 1);
        notifyItemRemoved(sizeOfList - 1);
    }

    /** is last one a footer ? **/
    public boolean isLastFooter() {
        return mNews.get(getItemCount() - 1).isFooter();
    }

    public ArrayList<MainNews.News> getNews() { return mNews; }

}
