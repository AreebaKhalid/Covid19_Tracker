package com.syedaareebakhalid.covid_19tracker;

import com.syedaareebakhalid.covid_19tracker.Models.CountryViewTemplate;
import com.syedaareebakhalid.covid_19tracker.Models.Template;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("{fullUrl}")
    Call<Template> getData(@Path("fullUrl") String fullUrl);

    @GET("{fullUrl}")
    Call<List<CountryViewTemplate>> getCountriesData(@Path("fullUrl") String fullUrl);

}
