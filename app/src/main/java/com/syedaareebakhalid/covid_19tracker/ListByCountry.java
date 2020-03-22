package com.syedaareebakhalid.covid_19tracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.syedaareebakhalid.covid_19tracker.Models.CountryViewTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListByCountry extends AppCompatActivity implements Callback<List<CountryViewTemplate>>, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = ListByCountry.class.getSimpleName();
    public static final String prefName = "pref1";
    public static final String countriesKey = "countries";
    public static final String addedCountriesKey = "addedCountriesData";

    private static List<CountryViewTemplate> listCountries =  new ArrayList<CountryViewTemplate>();
    private static List<CountryViewTemplate> addedCountries =  new ArrayList<CountryViewTemplate>();

    ItemAdapter adapter ;
    SharedPreferences sharedPreferences;

    private static Retrofit retrofit = null;

    private PopupMenu popup;
    private ListView countryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        countryListView = (ListView) findViewById(R.id.countryListView);
        countryListView.setEmptyView(findViewById(R.id.empty));

        sharedPreferences = getApplicationContext().getSharedPreferences(prefName,MODE_PRIVATE);
        connectToApi();

         countryListView.setOnItemClickListener(this);
         countryListView.setOnItemLongClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.add_country){

            popup = new PopupMenu(getApplicationContext(),findViewById(R.id.add_country));
            Set<String> countries;
           // sharedPreferences = getApplicationContext().getSharedPreferences(prefName,MODE_PRIVATE);
            countries = sharedPreferences.getStringSet(countriesKey,null);

            if(countries == null || countries.isEmpty()){
                Toast toast = Toast.makeText(getApplicationContext(),"There might be some problem, please check later", Toast.LENGTH_LONG);
                toast.show();
            }
            else{
                countries.forEach(c -> popup.getMenu().add(c));
                popup.show();
            }
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
        if(sharedPreferences.getStringSet(countriesKey,null) == null){
            Set<String> countries = new HashSet<String>();

            if(listCountries.isEmpty()){
                Toast toast = Toast.makeText(getApplicationContext(),"There might be some problem, please check later", Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                listCountries.forEach(country -> countries.add(country.getCountry()));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet(countriesKey,countries);
                editor.commit();
            }
        }
        //updated allready added countries shared preferences
        String addedCountriesJson = sharedPreferences.getString(addedCountriesKey,null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CountryViewTemplate>>(){}.getType();
        ArrayList<CountryViewTemplate> addedCountriesFromPref = new ArrayList<CountryViewTemplate>();
        if(addedCountriesJson != null){
            addedCountriesFromPref = gson.fromJson(addedCountriesJson,type);
            addedCountriesFromPref.forEach(dataitem -> {
                listCountries.forEach(dataitem2 ->{
                    //dont know why this IF statement is getting TRUE everytime
                    String c1 = dataitem.getCountry();
                    String c2 = dataitem2.getCountry();
                    if(c1.equalsIgnoreCase(c2));
                    dataitem.setCases(dataitem2.getCases());
                    dataitem.setCritical(dataitem2.getCritical());
                    dataitem.setDeaths(dataitem2.getDeaths());
                    dataitem.setRecovered(dataitem2.getRecovered());
                    dataitem.setTodayCases(dataitem2.getTodayCases());
                    dataitem.setTodayDeaths(dataitem2.getTodayDeaths());
                });
            });


            SharedPreferences.Editor editor = sharedPreferences.edit();
            String json = gson.toJson(addedCountriesFromPref);
            editor.putString(addedCountriesKey,json);
            editor.commit();

        }

        //after updating set the listView
        adapter = new ItemAdapter(this, addedCountriesFromPref);
        adapter.notifyDataSetChanged();
        countryListView.setAdapter(adapter);


    }

    @Override
    public void onFailure(Call<List<CountryViewTemplate>> call, Throwable t) {
        String addedCountriesJson = sharedPreferences.getString(addedCountriesKey,null);
        ArrayList<CountryViewTemplate> addedCountriesFromPref = new ArrayList<CountryViewTemplate>();
        if(addedCountriesJson != null){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<CountryViewTemplate>>(){}.getType();
            addedCountriesFromPref = gson.fromJson(addedCountriesJson,type);

        }
        adapter = new ItemAdapter(this, addedCountriesFromPref);
        adapter.notifyDataSetChanged();
        countryListView.setAdapter(adapter);

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
    public void addCountryToList(String countryName){
        listCountries.forEach(item ->{
            //add item to shared preferences
            //we have to append here --> need to add code to get the sharedPreferences string then append it with newly added countries
            if(item.getCountry().toLowerCase().equals(countryName.toLowerCase())){
                addedCountries.add(item);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(addedCountries);
                editor.putString(addedCountriesKey,json);
                editor.commit();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent nextIntent = new Intent(getApplicationContext(),View_detail.class);
        nextIntent.putExtra("com.syedaareebakhalid.covid_19tracker.TEMPLATE",addedCountries.get(position));
        startActivity(nextIntent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        addedCountries.remove(addedCountries.get(position));
        adapter.notifyDataSetChanged();
        return true;
    }
}
