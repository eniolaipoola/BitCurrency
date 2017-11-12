package com.dev.ehnyn.bitmoney;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;



public class HomeActivity extends AppCompatActivity {

    public static final String LOG_TAG = HomeActivity.class.getSimpleName();
    private static  String cryptoUrl =
            "https://min-api.cryptocompare.com/data/pricemulti?fsyms=ETH,BTC&tsyms=USD,EUR,MC,BTD,FTP,TOT,TELL,ENE,STO,WAY,TAB,XMR,CDS,GB,LC,CNMT,ICN,EXB,LYB,KMD,NEO,NGN&extraParams=your_app_name";

    @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.home_page);

        BitCurrency bitCurrency = new BitCurrency();
        bitCurrency.execute();

     }

     public void uiResponse(Data bit) {
         TextView titleTextView = (TextView) findViewById(R.id.currency);
         titleTextView.setText(bit.currency);

         TextView dateTextView = (TextView) findViewById(R.id.price);
         dateTextView.setText(bit.price);
     }



    private class BitCurrency extends AsyncTask<URL, Void, Data>{
        @Override
        protected Data doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(cryptoUrl);


            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error getting json response from url");
            }

            // Extract relevant fields from the JSON response
            Data bitCoin = extractDataFromJson(jsonResponse);
            return bitCoin;
        }



    private String makeHttpRequest(URL url) throws IOException{
        String jsonREsponse = "";
        int responseCode;
        if(url == null){
            return jsonREsponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setAllowUserInteraction(false);
            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                inputStream = urlConnection.getInputStream();
            }
            jsonREsponse = readFromStream(inputStream);

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(inputStream !=  null) {
                inputStream.close();
            }
        }
        return jsonREsponse;
    }

    /**
     * convert link into a string which contains json response
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    protected void onPostExecute(Data bitCoin) {
        if (bitCoin == null) {
            return;
        }
        uiResponse(bitCoin);
    }

    private Data extractDataFromJson(String bitJSON) {
        try {
            JSONObject baseJsonResponse = new JSONObject(bitJSON);
            JSONObject ethObject = baseJsonResponse.getJSONObject("ETH");
            JSONObject btcObject = baseJsonResponse.getJSONObject("BTC");

            // If there are results in the ETH object
            if(ethObject.length() > 0){
                Iterator<?> keys = ethObject.keys();
                while (keys.hasNext()){
                    String key = (String) keys.next();
                    int value = ethObject.getInt(key);
                    return new Data(key, value);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results", e);
        }
        return null;
    }
    }
}
