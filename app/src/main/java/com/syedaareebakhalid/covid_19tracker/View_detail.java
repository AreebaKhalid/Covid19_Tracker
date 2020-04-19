package com.syedaareebakhalid.covid_19tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.syedaareebakhalid.covid_19tracker.Models.CountryViewTemplate;

public class View_detail extends AppCompatActivity {

    Intent getIntent;
    private CountryViewTemplate countryViewTemplate;

    private TextView countryHeadTextView;
    private TextView casesCountTextView;
    private TextView todayCasesCountTextView;
    private TextView deathCountTextView;
    private TextView todayDeathCountTextView;
    private TextView recoveredCountTextView;
    private ImageView flagImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);

        getIntent = getIntent();
        countryViewTemplate = (CountryViewTemplate) getIntent.getSerializableExtra("com.syedaareebakhalid.covid_19tracker.TEMPLATE");

        countryHeadTextView = (TextView) findViewById(R.id.countryHeadTextView);
        casesCountTextView = (TextView) findViewById(R.id.casesCountTextView);
        todayCasesCountTextView = (TextView) findViewById(R.id.todayCasesCountTextView);
        deathCountTextView = (TextView) findViewById(R.id.deathCountTextView);
        todayDeathCountTextView = (TextView) findViewById(R.id.todayDeathCountTextView);
        recoveredCountTextView = (TextView) findViewById(R.id.recoveredCountTextView);
        flagImageView = (ImageView) findViewById(R.id.flagImageView);

        countryHeadTextView.setText(countryViewTemplate.getCountry());
        casesCountTextView.setText(countryViewTemplate.getCases().toString());
        todayCasesCountTextView.setText(countryViewTemplate.getTodayCases().toString());
        deathCountTextView.setText(countryViewTemplate.getDeaths().toString());
        todayDeathCountTextView.setText(countryViewTemplate.getTodayDeaths().toString());
        recoveredCountTextView.setText(countryViewTemplate.getRecovered().toString());
        //setting image
        new DownloadImageTask(flagImageView).execute(countryViewTemplate.getCountryInfo().getFlag());
    }
    /*private Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            stream = getHttpConnection(url);
            bitmap = BitmapFactory.
                    decodeStream(stream, null, bmOptions);
            stream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return bitmap;
    }
    // Makes HttpURLConnection and returns InputStream
    private InputStream getHttpConnection(String urlString)
            throws IOException {
        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }*/
}

