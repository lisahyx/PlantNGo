package api;

import com.google.gson.annotations.SerializedName;

public class PlantNetResponse {

    @SerializedName("query")
    private String query;

    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
