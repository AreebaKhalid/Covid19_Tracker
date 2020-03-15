package com.syedaareebakhalid.covid_19tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.syedaareebakhalid.covid_19tracker.Models.CountryViewTemplate;

import java.util.List;

public class ItemAdapter extends BaseAdapter{

    LayoutInflater inflater;

    List<CountryViewTemplate> countries;

    ItemAdapter(Context c, List<CountryViewTemplate> temp){
        countries = temp;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int position) {
        return countries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.activity_view_countryrecord,null);

        TextView countryTextView = (TextView) v.findViewById(R.id.countryTextView);
        TextView totalCasesTextView = (TextView) v.findViewById(R.id.totalCasesTextView);

        countryTextView.setText(countries.get(position).getCountry());
        totalCasesTextView.setText(countries.get(position).getCases().toString());

        return v;
    }
}
