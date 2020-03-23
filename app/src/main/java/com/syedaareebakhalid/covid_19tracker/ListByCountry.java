package com.syedaareebakhalid.covid_19tracker;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.syedaareebakhalid.covid_19tracker.Models.CountryViewTemplate;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static final String dateSecond = "dateSecond";

    private static List<CountryViewTemplate> listCountries =  new ArrayList<CountryViewTemplate>();
    private static List<CountryViewTemplate> addedCountries =  new ArrayList<CountryViewTemplate>();

    ItemAdapter adapter ;
    SharedPreferences sharedPreferences;
    String date = "1\\1\\1001\\ 12:00:00";

    private static Retrofit retrofit = null;

    private PopupMenu popup;
    private ListView countryListView;
    private TextView dateTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        countryListView = (ListView) findViewById(R.id.countryListView);
        dateTimeTextView = (TextView) findViewById(R.id.dateTimeTextView);
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
                TreeSet<String> sortedCountries = new TreeSet<>();
                sortedCountries.addAll(countries);
                sortedCountries.forEach(c -> popup.getMenu().add(c));
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

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResponse(Call<List<CountryViewTemplate>> call, Response<List<CountryViewTemplate>> response) {
        listCountries = response.body();
        @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        @SuppressLint({"NewApi", "LocalSuppress"}) LocalDateTime now = LocalDateTime.now();
        date = dtf.format(now);
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
        updateSharedPrefCountryData();
        setListView();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(dateSecond,date);
        editor.commit();
        dateTimeTextView.setText(date);

    }

    @Override
    public void onFailure(Call<List<CountryViewTemplate>> call, Throwable t) {
        setListView();
        String d = sharedPreferences.getString(dateSecond,null);
        if(d == null || d==""){
            dateTimeTextView.setText(date);
        }
        else{
            dateTimeTextView.setText(d);
        }
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
        if(listCountries ==  null || listCountries.isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(),"There might be some problem, please check later", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            listCountries.forEach(item ->{
                if(item.getCountry().toLowerCase().equals(countryName.toLowerCase())){
                    addedCountries.add(item);
                    addSharedPrefCountryData(item);
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent nextIntent = new Intent(getApplicationContext(),View_detail.class);
        nextIntent.putExtra("com.syedaareebakhalid.covid_19tracker.TEMPLATE",addedCountries.get(position));
        startActivity(nextIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //addedCountries.remove(addedCountries.get(position));
        removeFromSharedPreference(addedCountries.get(position));
        setListView();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeFromSharedPreference(CountryViewTemplate cview){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ArrayList<CountryViewTemplate> addedCountriesFromPref = getSharedPrefCountryData();
        editor.remove(addedCountriesKey);
        editor.commit();
        String country = cview.getCountry();
        addedCountriesFromPref.remove((addedCountriesFromPref.stream().filter(c ->
                country.equalsIgnoreCase(c.getCountry())).findFirst().orElse(null)));
        addedCountriesFromPref.forEach(c -> addSharedPrefCountryData(c));

    }
    private void addSharedPrefCountryData(CountryViewTemplate obj){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json;
        ArrayList<CountryViewTemplate> addedCountriesFromPref = getSharedPrefCountryData();
        if(addedCountriesFromPref == null || addedCountriesFromPref.isEmpty()){
            json = gson.toJson(obj);
        }else{
            addedCountriesFromPref.add(obj);
            json = gson.toJson(addedCountriesFromPref);
        }
        editor.putString(addedCountriesKey,json);
        editor.commit();
    }

    private ArrayList<CountryViewTemplate> getSharedPrefCountryData(){
        String addedCountriesJson = sharedPreferences.getString(addedCountriesKey,null);
        ArrayList<CountryViewTemplate> addedCountriesFromPref = new ArrayList<CountryViewTemplate>();
        if(addedCountriesJson != null){
            Gson gson = new Gson();
            int i=0;
            Pattern p = Pattern.compile("country");
            Matcher m = p.matcher(addedCountriesJson);
            while (m.find()) {
                i++;
            }

            if(i==1){

                Type type = new TypeToken<CountryViewTemplate>(){}.getType();
                addedCountriesFromPref.add(gson.fromJson(addedCountriesJson,type));
            }else{

                Type type = new TypeToken<ArrayList<CountryViewTemplate>>(){}.getType();
                addedCountriesFromPref = gson.fromJson(addedCountriesJson,type);
            }
        }
        return  addedCountriesFromPref;
    }

    private void setListView(){
        if(getSharedPrefCountryData() != null){
            addedCountries = getSharedPrefCountryData();
        }
      Collections.sort(addedCountries, new Comparator<CountryViewTemplate>() {
            @Override
            public int compare(CountryViewTemplate o1, CountryViewTemplate o2) {
                return o1.getCountry().compareTo(o2.getCountry());
            }
        });
        adapter = new ItemAdapter(this, addedCountries);
        adapter.notifyDataSetChanged();
        countryListView.setAdapter(adapter);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateSharedPrefCountryData(){
        ArrayList<CountryViewTemplate> addedCountriesFromPref = getSharedPrefCountryData();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(addedCountriesKey);
        editor.commit();
        if(addedCountriesFromPref != null || ! (addedCountriesFromPref.isEmpty())){
            addedCountriesFromPref.forEach(countryData -> {
                String country = countryData.getCountry();
                addSharedPrefCountryData((listCountries.stream().filter(c ->
                        country.equalsIgnoreCase(c.getCountry())).findFirst().orElse(null)));
                    }
            );
        }
    }
}
