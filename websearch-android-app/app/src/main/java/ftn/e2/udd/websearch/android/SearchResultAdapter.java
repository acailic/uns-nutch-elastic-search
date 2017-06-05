package ftn.e2.udd.websearch.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ftn.e2.udd.websearch.android.dto.SearchResult;

public class SearchResultAdapter extends BaseAdapter {

    SearchResult searchResult;

    private LayoutInflater layoutInflater;

    public SearchResultAdapter(Context context, SearchResult searchResult) {
        this.searchResult = searchResult;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return searchResult.getEntries().size();
    }

    @Override
    public Object getItem(int position) {
        return searchResult.getEntries().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSearchResult(SearchResult searchResult) {
        this.searchResult = searchResult;
        notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.results_layout, null);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.title);
            holder.descriptionView = (TextView) convertView.findViewById(R.id.description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleView.setText(searchResult.getEntries().get(position).getTitle());
        holder.descriptionView.setText(Html.fromHtml(searchResult.getEntries().get(position).getDescription()));
        holder.link = searchResult.getEntries().get(position).getUrl();
        return convertView;
    }

    public static class ViewHolder {
        private TextView titleView;
        private TextView descriptionView;
        private String link;

        @SuppressWarnings("unused")
        public TextView getTitleView() {
            return titleView;
        }

        @SuppressWarnings("unused")
        public TextView getDescriptionView() {
            return descriptionView;
        }

        public String getLink() {
            return link;
        }

    }
}
