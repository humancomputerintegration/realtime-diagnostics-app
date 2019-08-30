package com.example.mobilehealthprototype;

import android.util.Log;

import java.net.*;
import java.util.*;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/*
 * If you encounter unexpected authorization errors, double-check these values
 * against the endpoint for your Bing Web search instance in your Azure
 * dashboard.
 */

public class WebSearcher {
    //TODO - find a way to replace this later & Delete
    //This api-key is placed here for portability of testers/interested individuals.
    String subscriptionKey, host, path;
//    static String searchTerm = "Microsoft Cognitive Services";

    public WebSearcher(String subscriptionKey, String host, String path){

        if (subscriptionKey.length() != 32) {
            Log.d("TESTING","Invalid Bing Search API subscription key!");
            Log.d("TESTING","Please paste yours into the source code.");
        }
        this.subscriptionKey = subscriptionKey;
        this.host = host;
        this.path = path;
    }

    public SearchResults SearchWeb (String searchQuery) throws Exception {
        // Construct the URL.
        URL url = new URL(host + path + "?q=" +  URLEncoder.encode(searchQuery, "UTF-8"));

        // Open the connection.
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

        // Receive the JSON response body.
        InputStream stream = connection.getInputStream();
        String response = new Scanner(stream).useDelimiter("\\A").next();

        // Construct the result object.
        SearchResults results = new SearchResults(new HashMap<String, String>(), response);

        // Extract Bing-related HTTP headers.
        Map<String, List<String>> headers = connection.getHeaderFields();
        for (String header : headers.keySet()) {
            if (header == null) continue;      // may have null key
            if (header.startsWith("BingAPIs-") || header.startsWith("X-MSEdge-")){
                results.relevantHeaders.put(header, headers.get(header).get(0));
            }
        }
        stream.close();
        return results;
    }

    public static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(json_text).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    public void SearchInternet(String query){
        try{
            Log.d("TESTING","Searching the web for: " + query);
            SearchResults result = SearchWeb(query);
            Log.d("TESTING","RELEVANT HTTP HEADERS:");
            for (String header: result.relevantHeaders.keySet()){
                Log.d("TESTING",header + ": " + result.relevantHeaders.get(header));
            }
            Log.d("TESTING","JSON RESPONSE");
            Log.d("TESTING", prettify(result.jsonResponse));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static class SearchResults{
        HashMap<String, String> relevantHeaders;
        String jsonResponse;
        SearchResults(HashMap<String, String> headers, String json) {
            relevantHeaders = headers;
            jsonResponse = json;
        }
    }

}
