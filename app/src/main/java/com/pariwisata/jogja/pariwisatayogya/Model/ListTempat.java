package com.pariwisata.jogja.pariwisatayogya.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListTempat {
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("data")
    @Expose
    private List<SemuaTempat> data = null;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<SemuaTempat> getData() {
        return data;
    }

    public void setData(List<SemuaTempat> data) {
        this.data = data;
    }
}
