package com.repiso.myhangman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private String[] categorys;
    private String[] levels;
    private Spinner categorySpinner, levelSpinner;

    protected static final String KEY_CATEGORY = "keyCategory";
    protected static final String KEY_LEVEL = "keyLevel";

    private boolean isLogin;
    private String nameUser;
    private int idUser, idImage;

    Button btn_start, btn_exit, btn_help;
    private TextView tv_name;

    private DBHelper dbHelper;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Almacenamos en un arrayList todas las categorias y niveles disponibles
        categorys = this.getResources().getStringArray(R.array.categoria);
        levels = this.getResources().getStringArray(R.array.nivel);

        categorySpinner = (Spinner)findViewById(R.id.spinner_category2);
        levelSpinner = (Spinner)findViewById(R.id.spinner_difficulty2);

        //Creamos un adapter para el spinner (contexto, layout, array)
        ArrayAdapter<String> adapterLevels = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, levels);
        adapterLevels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //le pasamos el adapter al spinner
        levelSpinner.setAdapter(adapterLevels);

        //Creamos un adapter para el spinner (contexto, layout, array)
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categorys);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //le pasamos el adapter al spinner
        categorySpinner.setAdapter(adapterCategory);

    }


    /**
     * Realiza las acciones correspondientes de los distintos botones
     * @param v
     */
    public void onClick (View v) {
        switch(v.getId()) {
            case R.id.btn_play_hangman:

                    Intent intent = new Intent(getApplicationContext(), HangmanActivity.class);
                    //almacena y envía a la siguiente actividad los elementos seleccionados

                    intent.putExtra(KEY_CATEGORY, categorySpinner.getSelectedItem().toString());
                    intent.putExtra(KEY_LEVEL, levelSpinner.getSelectedItem().toString());
                    startActivity(intent);

                break;

        }
    }

}