package com.syedaareebakhalid.covid_19tracker;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.syedaareebakhalid.covid_19tracker.Models.CountryViewTemplate;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListByCountry extends AppCompatActivity implements Callback<List<CountryViewTemplate>> {

    private static final String TAG = ListByCountry.class.getSimpleName();

    List<String> countries = new ArrayList<String>();
    private static List<CountryViewTemplate> listCountries =  new ArrayList<CountryViewTemplate>();
    private static List<CountryViewTemplate> addedCountries =  new ArrayList<CountryViewTemplate>();

    ItemAdapter adapter ;

    private static Retrofit retrofit = null;

    private PopupMenu popup;
    private ListView countryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        countryListView = (ListView) findViewById(R.id.countryListView);
        countryListView.setEmptyView(findViewById(R.id.empty));
        connectToApi();

         adapter = new ItemAdapter(this, addedCountries);
         adapter.notifyDataSetChanged();
         countryListView.setAdapter(adapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.add_country){

            popup = new PopupMenu(getApplicationContext(),findViewById(R.id.add_country));
            countries.forEach(c -> popup.getMenu().add(c));
            popup.show();
            popup.setOnMenuItemClickListener(menuItem -> {
                String country = menuItem.getTitle().toString();
                addCountryToList(country);
                adapter.notifyDataSetChanged();
                return false;
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResponse(Call<List<CountryViewTemplate>> call, Response<List<CountryViewTemplate>> response) {

        listCountries = response.body();
        listCountries.forEach(country -> countries.add(country.getCountry()));

    }

    @Override
    public void onFailure(Call<List<CountryViewTemplate>> call, Throwable t) {
        Log.e(TAG, t.toString());
    }

    public void connectToApi(){

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(MainActivity.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<CountryViewTemplate>> call = apiService.getCountriesData("countries");
        call.enqueue(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addCountryToList(String country){


        listCountries.forEach(item ->{
            if(item.getCountry() == country){
                addedCountries.add(item);
            }
        });
    }
}
