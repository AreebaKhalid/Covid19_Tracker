package com.syedaareebakhalid.covid_19tracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.syedaareebakhalid.covid_19tracker.Models.Template;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Callback<Template>, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String BASE_URL = "https://corona.lmao.ninja/";
    public static final String prefName = "pref1";
    public static final String recoveredCount = "recoveredCount";
    public static final String casesCount = "casesCount";
    public static final String deathCount = "deathCount";

    private static Retrofit retrofit = null;
    SharedPreferences sharedPreferences;

    public Template template;

    private TextView recoveredTextView;
    private TextView casesTextView;
    private TextView deathTextView;

    String totalCases="0";
    String totalDeaths="0";
    String totalRecovered="0";

    private Button btnNextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        deathTextView = (TextView) findViewById(R.id.deathTextView);
        casesTextView = (TextView) findViewById(R.id.casesTextView);
        recoveredTextView = (TextView) findViewById(R.id.recoveredTextView);
        btnNextView = (Button) findViewById(R.id.btnNextView);

        sharedPreferences = getSharedPreferences(prefName,MODE_PRIVATE);

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }

        ApiService apiService = retrofit.create(ApiService.class);
        Call<Template> call = apiService.getData("all");
        call.enqueue(this);

        btnNextView.setOnClickListener(this);

    }

    @Override
    public void onResponse(Call<Template> call, Response<Template> response) {
        if(response.isSuccessful()){

            if(response.body().equals(null)){
                if(sharedPreferences.getString(casesCount,null) == null){
                    Toast toast = Toast.makeText(getApplicationContext(),"There might be some problem, please check later", Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    totalCases = sharedPreferences.getString(casesCount,"0");
                    totalDeaths = sharedPreferences.getString(deathCount,"0");
                    totalRecovered = sharedPreferences.getString(recoveredCount,"0");
                }
            }
            else {
                template = response.body();

                totalCases = template.getCases().toString();
                totalDeaths = template.getDeaths().toString();
                totalRecovered = template.getRecovered().toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(casesCount, totalCases);
                editor.putString(deathCount, totalDeaths);
                editor.putString(recoveredCount, totalRecovered);
                editor.commit();
            }
        }
        deathTextView.setText(totalDeaths);
        casesTextView.setText(totalCases);
        recoveredTextView.setText(totalRecovered);
    }

    @Override
    public void onFailure(Call<Template> call, Throwable t) {
            if(sharedPreferences.getString(casesCount,null) == null){
                Toast toast = Toast.makeText(getApplicationContext(),"There might be some problem, please check later", Toast.LENGTH_LONG);
                toast.show();
            }
            else{
                totalCases = sharedPreferences.getString(casesCount,"0");
                totalDeaths = sharedPreferences.getString(deathCount,"0");
                totalRecovered = sharedPreferences.getString(recoveredCount,"0");
            }
        deathTextView.setText(totalDeaths);
        casesTextView.setText(totalCases);
        recoveredTextView.setText(totalRecovered);
        Log.e(TAG, t.toString());
    }

    @Override
    public void onClick(View v) {
        Intent nextView = new Intent(getApplicationContext(),ListByCountry.class);
        startActivity(nextView);
    }
}
