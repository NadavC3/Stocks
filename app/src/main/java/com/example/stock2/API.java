package com.example.stock2;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.net.URL;

public class API {
    private static final String KEY = "EBZBY3X2BJ8UU8T6";
    private static API instance = null;

    private API() {
        // Private constructor to prevent instantiation from outside the class
    }

    public static synchronized API getInstance() {
        if (instance == null) {
            instance = new API();
        }
        return instance;
    }
    public StockData getStockData(String symbol) {
        String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + symbol + "&interval=1min&apikey=" + KEY;

        try {
            PriceRequestTask priceRequestTask = new PriceRequestTask();
            double price = priceRequestTask.execute(apiUrl).get();
            double change = getPrice24(symbol);
            return new StockData(price, change);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private double getPrice24(String symbol) {
        String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + symbol + "&interval=1min&outputsize=full&apikey=" + KEY;

        try {
            Price24RequestTask price24RequestTask = new Price24RequestTask();
            return price24RequestTask.execute(apiUrl).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private static class PriceRequestTask extends AsyncTask<String, Void, Double> {
        @Override
        protected Double doInBackground(String... urls) {
            String apiUrl = urls[0];
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();

                // Parse the JSON response
                JSONObject responseJson = new JSONObject(content.toString());

                // Extract the desired information, such as the price
                JSONObject timeSeries = responseJson.getJSONObject("Time Series (1min)");
                String latestData = timeSeries.keys().next();  // Get the latest data point timestamp
                JSONObject latestEntry = timeSeries.getJSONObject(latestData);
                double price = latestEntry.getDouble("4. close");  // Assuming the price is stored as "4. close"

                return price;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0.0;
        }
    }

    private static class Price24RequestTask extends AsyncTask<String, Void, Double> {
        @Override
        protected Double doInBackground(String... urls) {
            String apiUrl = urls[0];
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();

                // Parse the JSON response
                JSONObject responseJson = new JSONObject(content.toString());

                // Extract the desired information, such as the price from 24 hours ago
                JSONObject timeSeries = responseJson.getJSONObject("Time Series (1min)");

                // Get the latest data point timestamp
                String latestData = timeSeries.keys().next();
                JSONObject latestEntry = timeSeries.getJSONObject(latestData);
                double latestPrice = latestEntry.getDouble("4. close");

                // Calculate the timestamp for 24 hours ago
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(latestData));
                calendar.add(Calendar.HOUR_OF_DAY, -24);
                String previousData = dateFormat.format(calendar.getTime());
                Log.d("DATA", "DATE 24 Hours ago: "+previousData);

                // Get the data for 24 hours ago
                JSONObject previousEntry = timeSeries.getJSONObject(previousData);
                double previousPrice = previousEntry.getDouble("4. close");

                double change = ((latestPrice - previousPrice) / previousPrice) * 100;
                change = Math.round(change * 100.0) / 100.0;  // Round to two decimal places
                Log.d("DATA", "PRICE 24 Hours ago: "+previousPrice);
                return change;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0.0;
        }
    }
}
