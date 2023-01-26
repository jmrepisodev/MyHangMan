package com.repiso.myhangman;

import static com.repiso.myhangman.MainActivity.KEY_LEVEL;
import static com.repiso.myhangman.MainActivity.KEY_CATEGORY;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HangmanActivity extends AppCompatActivity {

    protected static final String KEY_SCORE = "keyScore";
    protected static final String KEY_GANADAS = "keyGanadas";
    protected static final String KEY_PERDIDAS = "keyPerdidas";

    private DBHelper dbHelper;
    private MediaPlayer mp;
    private int intentos = 0;
    private int puntos = 0;
    private int wordLength;
    private String word, category, level;
    private LinearLayout layout, layoutBtns;
    private TextView tv_score, tv_intentos, tv_category, tv_level;
    private ImageView img;
    private ArrayList<View> btns;

    ArrayList<Word> wordList;

    private boolean isSoundON;
    private boolean isVibrationON;
    private long backPressedTime;
    private int ganadas=0;
    private int perdidas=0;
    private int sinAcabar=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangman);


        //asociaciones de elementos
        layout = (LinearLayout)findViewById(R.id.lyt_words);
        layoutBtns = (LinearLayout)findViewById(R.id.footer);
        tv_score = (TextView)findViewById(R.id.tv_score_hangman);
        tv_intentos = (TextView)findViewById(R.id.tv_counter);
        tv_level= (TextView)findViewById(R.id.tv_level_hangman);
        tv_category = (TextView)findViewById(R.id.tv_category_hangman);
        img = (ImageView)findViewById(R.id.img);

        //rellena el array de views con los botones de la vista
        btns = layoutBtns.getTouchables();

        //Recuperamos los extras de la actividad anterior
        if (getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();

            category = extras.getString(KEY_CATEGORY);
            level = extras.getString(KEY_LEVEL);
        }

        isSoundON=true;
        isVibrationON=false;

        //Creamos un objeto de la clase auxiliar (conexion a la base de datos).
        dbHelper=DBHelper.getInstance(this);

        //Genera la partida
        Construye();

    }

    /**
     * Método que comprueba si las letras introducidas por el usuario pertenecen a la palabra
     * @param view
     */
    public void checkWord(View view) {
        Button button = (Button)view;
        //se consigue el texto (letra) del botón
        String str = button.getText().toString();
        //se desactiva el botón
        button.setEnabled(false);
        //Verifica si la palabra contiene la letra
        if(word.contains(str)) {
            //Equivale a un bucle while. Devuelve el índice de la primera ocurrencia del substring.
            // Si no encuentra nada se retorna -1
            for (int i = -1; (i = word.indexOf(str, i + 1)) != -1; ) {
                //Activa/Desactiva el sonido, según las preferencias
                if(isSoundON==true){
                    //reproduce un sonido
                    mp = MediaPlayer.create(this, R.raw.win);
                    mp.start();
                }

                //dibuja la letra en la vista correspondiente al lugar q ocupa, según id
                TextView txt = (TextView) findViewById(i);
                txt.setText(str);
                //reduce el contador de letras
                wordLength--;
            }
            //si la palabra ya no contiene más letras -> has completado la palabra
            if(wordLength == 0) {
                //lanza un mensaje de alerta
                //Toast.makeText(this,"Enhorabuena, Has acertado",Toast.LENGTH_LONG).show();

                //Actualiza la puntuación en pantalla y en la base de datos
                ganadas++;
                puntos+=10;
                tv_score.setText(String.valueOf(puntos));
                try{
                    //  hangmanDBHelper.updateScoreHangman(idUser,puntos,category,level);
                }catch(SQLiteException e){
                    Toast.makeText(this,"Error: No se ha podido actualizar las puntuaciones",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                showDialogSuccess();

            }
        }
        //si la palabra no contiene la letra introducida por el usuario
        else
        {
            //disminuye un intento
            intentos--;
            //Activa/Desactiva el sonido, según las preferencias
            if(isSoundON==true){
                //reproduce un sonido de fail
                mp = MediaPlayer.create(this, R.raw.fail);
                mp.start();
            }
            //Activa/Desactiva la vibración, según las preferencias
            if(isVibrationON==true){
                Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(500);
            }



            //reinicia el dibujo
            img.setImageDrawable(null);
            //actualiza el número de intentos en la pantalla
            tv_intentos.setText(String.valueOf(intentos));

            if(level.equalsIgnoreCase("FACIL"))
            {
                switch (intentos)
                {
                    case 5:
                        img.setBackgroundResource(R.drawable.ima2);
                        break;
                    case 4:
                        img.setBackgroundResource(R.drawable.ima3);
                        break;
                    case 3:
                        img.setBackgroundResource(R.drawable.ima41);
                        break;
                    case 2:
                        img.setBackgroundResource(R.drawable.ima4);
                        break;
                    case 1:
                        img.setBackgroundResource(R.drawable.ima51);
                        break;
                    case 0:
                        img.setBackgroundResource(R.drawable.ima5);

                        //cuando se agotan todos los intentos, has perdido el juego.
                        perdidas++;
                        // Actualiza la base de datos
                        try{
                            // hangmanDBHelper.updateScoreHangman(idUser,puntos,category,level);
                        }catch(SQLiteException e){
                            Toast.makeText(this,"Error: No se ha podido actualizar las puntuaciones",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        // Lanza mensaje de alerta
                        showDialogGameOver();

                        break;
                }
            }
            else
            {
                switch (intentos)
                {
                    case 3:
                        img.setBackgroundResource(R.drawable.ima2);
                        break;
                    case 2:
                        img.setBackgroundResource(R.drawable.ima3);
                        break;
                    case 1:
                        img.setBackgroundResource(R.drawable.ima4);
                        break;
                    case 0:
                        img.setBackgroundResource(R.drawable.ima5);

                        showDialogGameOver();

                        break;
                }
            }
        }
    }


    /**
     * Método que genera la partida
     */
    void Construye()
    {
        //Estable el número de intentos disponibles, según nivel seleccionado
        if(level.equalsIgnoreCase("FACIL"))
        {
            intentos = 6;
            tv_intentos.setText("6");
        }
        else
        {
            intentos = 4;
            tv_intentos.setText("4");
        }
        tv_score.setText(String.valueOf(puntos));
        tv_level.setText(level);
        tv_category.setText(category);
        //reinicia las vistas anteriores
        layout.removeAllViews();
        img.setImageDrawable(null);
        img.setBackgroundResource(R.drawable.ima1);

        //activa todos los botones
        for(View touchable : btns)
        {
            if( touchable instanceof Button )
            {
                touchable.setEnabled(true);
            }
        }

        //obtiene una palabra aleatoria de la base de datos local
        word=dbHelper.getWord(category);

        // Toast.makeText(this,"Palabra: "+word,Toast.LENGTH_LONG).show();

        wordLength = word.length();
        //Dibuja un TextView por cada letra de la palabra actual
        for(int i = 0; i < wordLength; i++) {
            TextView row = new TextView(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            //asigna un id a cada vista de letra de la palabra
            row.setId(i);
            row.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            row.setPadding(0,0,10,0);
            row.setWidth(40);
            row.setBackgroundColor(Color.WHITE);
            row.setGravity(Gravity.CENTER);
            row.setBackgroundResource(R.drawable.letter_bg);
            layout.addView(row);
        }

    }


    /**
     * Muestra una ventana de dialogo, con opciones para el usuario
     */
    public void showDialogGameOver(){

        if(isSoundON==true){
            //reproduce un sonido
            mp = MediaPlayer.create(this, R.raw.game_over);
            mp.start();
        }

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("GAME OVER");
        builder.setMessage("¡Has perdido! \n\n" + "La palabra era: " + word);
        builder.setCancelable(false);
        builder.setPositiveButton("Jugar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Construye();
            }
        });
        builder.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishHangman();
            }
        });
        builder.show();

/*
        //Create the Dialog here
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_game_over);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialgog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button Okay = dialog.findViewById(R.id.btn_okay);
        Button Cancel = dialog.findViewById(R.id.btn_cancel);

        if(isSoundON==true){
            //reproduce un sonido
            mp = MediaPlayer.create(this, R.raw.game_over);
            mp.start();
        }

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                dialog.dismiss();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();
  */
    }

    /**
     * Muestra una ventana de dialogo, con opciones para el usuario
     */
    public void showDialogSuccess(){

        if(isSoundON==true){
            //reproduce un sonido
            mp = MediaPlayer.create(this, R.raw.game_win);
            mp.start();
        }

        //Lanza un mensaje de felicitación y ofre la oportunidad de volver a jugar
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("¡FELICIDADES!");
        builder.setMessage("¡Enhorabuena! ¡Has adivinado la palabra!");
        builder.setCancelable(false);
        builder.setPositiveButton("Jugar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Construye();
            }
        });
        builder.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishHangman();
            }
        });
        builder.show();

/*

        //Create the Dialog here
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_success);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialgog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button Okay = dialog.findViewById(R.id.btn_okay);
        Button Cancel = dialog.findViewById(R.id.btn_cancel);

        if(isSoundON==true){
            //reproduce un sonido
            mp = MediaPlayer.create(this, R.raw.game_win);
            mp.start();
        }

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                dialog.dismiss();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();
*/
    }



    /**
     * Finaliza y se dirige hacia la pantalla de resultado
     */
    protected void finishHangman() {

    }

    /**
     * Método que permite salir de la aplicación si presionames el botón volver dos veces,
     * con cierto intervalo de tiempo (2 seguntos)
     */
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finish();
        } else {
            Toast.makeText(this, "Pulsa dos veces para finalizar", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }


}