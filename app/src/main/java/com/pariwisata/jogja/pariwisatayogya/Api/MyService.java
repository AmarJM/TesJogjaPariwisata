package com.pariwisata.jogja.pariwisatayogya.Api;

import com.pariwisata.jogja.pariwisatayogya.Model.ListTempat;
import com.pariwisata.jogja.pariwisatayogya.Model.SemuaTempat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MyService {
    @GET("jsonBootcamp.php")
    Call<ListTempat> getSemuaTempat();
}
