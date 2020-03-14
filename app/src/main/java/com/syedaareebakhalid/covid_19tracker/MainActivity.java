package com.syedaareebakhalid.covid_19tracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Callback<Template> {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String BASE_URL = "https://corona.lmao.ninja/";

    private static Retrofit retrofit = null;

    public Template template;

    private TextView recoveredTextView;
    private TextView casesTextView;
    private TextView deathTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }

        ApiService apiService = retrofit.create(ApiService.class);
        Call<Template> call = apiService.getData("all");
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Template> call, Response<Template> response) {
        if(response.isSuccessful()){

            template = response.body();

            deathTextView = (TextView) findViewById(R.id.deathTextView);
            casesTextView = (TextView) findViewById(R.id.casesTextView);
            recoveredTextView = (TextView) findViewById(R.id.recoveredTextView);

            String totalCases = template.getCases().toString();
            String totalDeaths = template.getDeaths().toString();
            String totalRecovered = template.getRecovered().toString();

            deathTextView.setText(totalDeaths);
            casesTextView.setText(totalCases);
            recoveredTextView.setText(totalRecovered);
        }
    }

    @Override
    public void onFailure(Call<Template> call, Throwable t) {
        Log.e(TAG, t.toString());
    }
}
