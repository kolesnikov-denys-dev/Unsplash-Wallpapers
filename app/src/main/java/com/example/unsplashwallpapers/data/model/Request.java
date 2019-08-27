
package com.example.unsplashwallpapers.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import static com.example.unsplashwallpapers.util.Constants.RESULTS;
import static com.example.unsplashwallpapers.util.Constants.TOTAL;

public class Request {

    @SerializedName(TOTAL)
    @Expose
    private Integer total;

    @SerializedName(RESULTS)
    @Expose
    private List<Result> results = null;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Result> getResults() { return results; }

    public void setResults(List<Result> results) {
        this.results = results;
    }

}
