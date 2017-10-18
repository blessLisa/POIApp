package com.example.whkang.poiapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button gas_button = (Button)findViewById(R.id.gas_button);
        Button res_button = (Button)findViewById(R.id.res_button);
        Button hos_button= (Button)findViewById(R.id.hos_button);
        Button par_button = (Button)findViewById(R.id.par_button);

        gas_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String type = "gas_station";
                Intent intent = new Intent(getApplicationContext(), CategorizedActivity.class);
                intent.putExtra("TYPE", type);
                startActivity(intent);
            }
        });

        res_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String type = "restaurant";
                Intent intent = new Intent(getApplicationContext(), CategorizedActivity.class);
                intent.putExtra("TYPE", type);
                startActivity(intent);
            }
        });
        hos_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String type = "hospital";
                Intent intent = new Intent(getApplicationContext(), CategorizedActivity.class);
                intent.putExtra("TYPE", type);
                startActivity(intent);
            }
        });
        par_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String type = "parking";
                Intent intent = new Intent(getApplicationContext(), CategorizedActivity.class);
                intent.putExtra("TYPE", type);
                startActivity(intent);
            }
        });
    }

}
