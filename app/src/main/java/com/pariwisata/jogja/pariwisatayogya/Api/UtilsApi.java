package com.pariwisata.jogja.pariwisatayogya.Api;

public class UtilsApi {
    public static final String BASE_URL_API_DATA = "http://erporate.com/bootcamp/";

    // Mendeklarasikan Interface BaseApiService
    public static MyService getAPIService(){
        return RetrofitClient.getClient(BASE_URL_API_DATA).create(MyService.class);
    }
}
