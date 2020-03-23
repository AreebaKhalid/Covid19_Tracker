package com.syedaareebakhalid.covid_19tracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.syedaareebakhalid.covid_19tracker.Models.Template;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public static final String dateFirst = "dateFirst";

    private static Retrofit retrofit = null;
    SharedPreferences sharedPreferences;

    public Template template;

    private TextView recoveredTextView;
    private TextView casesTextView;
    private TextView deathTextView;
    private TextView dateTimeTextView;

    String totalCases="0";
    String totalDeaths="0";
    String totalRecovered="0";
    String date = "1\\1\\1001\\ 12:00:00";

    private Button btnNextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        deathTextView = (TextView) findViewById(R.id.deathTextView);
        casesTextView = (TextView) findViewById(R.id.casesTextView);
        recoveredTextView = (TextView) findViewById(R.id.recoveredTextView);
        dateTimeTextView = (TextView) findViewById(R.id.dateTimeTextView);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                    date = sharedPreferences.getString(dateFirst,"1\\1\\1001\\ 12:00:00");
                }
            }
            else {
                template = response.body();

                totalCases = template.getCases().toString();
                totalDeaths = template.getDeaths().toString();
                totalRecovered = template.getRecovered().toString();

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                date = dtf.format(now);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(casesCount, totalCases);
                editor.putString(deathCount, totalDeaths);
                editor.putString(recoveredCount, totalRecovered);
                editor.putString(dateFirst,date);
                editor.commit();
            }
        }
        deathTextView.setText(totalDeaths);
        casesTextView.setText(totalCases);
        recoveredTextView.setText(totalRecovered);
        dateTimeTextView.setText(date);
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
                date = sharedPreferences.getString(dateFirst,"1\\1\\1001\\ 12:00:00");
            }
        deathTextView.setText(totalDeaths);
        casesTextView.setText(totalCases);
        recoveredTextView.setText(totalRecovered);
        dateTimeTextView.setText(date);
        Log.e(TAG, t.toString());
    }

    @Override
    public void onClick(View v) {
        Intent nextView = new Intent(getApplicationContext(),ListByCountry.class);
        startActivity(nextView);
    }
}
