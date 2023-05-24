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


public class MainActivity extends AppCompatActivity {

    private API api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        api = API.getInstance();

        Button searchButton = findViewById(R.id.search_button);
        TableLayout table = findViewById(R.id.table);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText search = findViewById(R.id.stock_search_edittext);
                String symbol = search.getText().toString();
                StockData stockData = api.getStockData(symbol);
                if(stockData.getPrice() == 0)
                {
                    System.out.println("Invalid name");
                    return ;
                }

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
                stockPrice.setText(String.valueOf(stockData.getPrice()));

                TextView stockChange = new TextView(MainActivity.this);
                stockChange.setLayoutParams(layoutParams);
                if(stockData.getChange()>0)
                    stockChange.setTextColor(Color.GREEN);
                if(stockData.getChange()<0)
                    stockChange.setTextColor(Color.RED);

                stockChange.setText(String.valueOf(stockData.getChange()));

                row.addView(stockSymbol);
                row.addView(stockPrice);
                row.addView(stockChange);

                table.addView(row);
                // Clear the search line text
                search.setText("");
            }
        });
    }
}
