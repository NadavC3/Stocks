package com.example.stock2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.graphics.Color;




import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private API api;
    private TableLayout table;
    private List<String> savedStocks;

    private static final String PREFS_NAME = "StocksPrefs";
    private static final String PREFS_KEY_STOCKS = "SavedStocks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = API.getInstance();
        table = findViewById(R.id.table);
        savedStocks = new ArrayList<>();

        loadSavedStocks();

        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText search = findViewById(R.id.stock_search_edittext);
                String symbol = search.getText().toString();
                StockData stockData = api.getStockData(symbol);

                if (stockData.getPrice() == 0) {
                    System.out.println("Invalid name");
                    return;
                }

                addStockToTable(symbol, stockData.getPrice(), stockData.getChange());
                savedStocks.add(symbol);
                saveStocks();

                search.setText("");
            }
        });
    }

    private void loadSavedStocks() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> stockSet = prefs.getStringSet(PREFS_KEY_STOCKS, null);
        if (stockSet != null) {
            savedStocks.addAll(stockSet);
            for (String symbol : savedStocks) {
                StockData stockData = api.getStockData(symbol);
                if (stockData.getPrice() != 0) {
                    addStockToTable(symbol, stockData.getPrice(), stockData.getChange());
                }
            }
        }
    }

    private void saveStocks() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(PREFS_KEY_STOCKS, new HashSet<>(savedStocks));
        editor.apply();
    }

    private void addStockToTable(String symbol, double price, double change) {
        TableRow row = new TableRow(MainActivity.this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(15, 0, 25, 0);

        TextView stockSymbol = new TextView(MainActivity.this);
        stockSymbol.setLayoutParams(layoutParams);
        stockSymbol.setText(symbol);

        TextView stockPrice = new TextView(MainActivity.this);
        stockPrice.setLayoutParams(layoutParams);
        stockPrice.setText(String.valueOf(price));

        TextView stockChange = new TextView(MainActivity.this);
        stockChange.setLayoutParams(layoutParams);
        if (change > 0)
            stockChange.setTextColor(Color.GREEN);
        if (change < 0)
            stockChange.setTextColor(Color.RED);
        stockChange.setText(String.valueOf(change));

        Button removeButton = new Button(MainActivity.this);
        removeButton.setLayoutParams(layoutParams);
        removeButton.setText("Remove");
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                table.removeView(row);
                savedStocks.remove(symbol);
                saveStocks();
            }
        });

        row.addView(stockSymbol);
        row.addView(stockPrice);
        row.addView(stockChange);
        row.addView(removeButton);

        table.addView(row);
    }
}


