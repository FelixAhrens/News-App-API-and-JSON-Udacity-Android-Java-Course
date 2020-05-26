package com.android.example.cryptonewsbytheguardian;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<NewsClass> {

    public NewsAdapter(Activity context, ArrayList<NewsClass> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View newsView = convertView;
        if (newsView == null) {
            newsView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        NewsClass currentNews = getItem(position);

        TextView titleView = (TextView) newsView.findViewById(R.id.news_title);
        titleView.setText(currentNews.getTitle());

        String datetime = currentNews.getDate();
        String date = datetime.substring(0, datetime.indexOf("T"));

        String author = currentNews.getAuthor();
        if (author.contains(" in ")) {
            author = author.substring(0, author.indexOf(" in "));
        } else if (author.contains("correspondent")) {
            author = author.substring(0, author.indexOf(" ", author.indexOf(" ") + 1));
        }

        TextView authorView = (TextView) newsView.findViewById(R.id.sub_text);
        authorView.setText(author + " | " + currentNews.getSection() + " | " + date);

        return newsView;
    }

}
