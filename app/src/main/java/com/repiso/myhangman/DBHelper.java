package com.repiso.myhangman;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyHangmanDatabase.db";
    private SQLiteDatabase database;
    private static DBHelper dbHelper;

    private ArrayList<Word> wordList;

    private Resources resources;
    //Array de categorías
    private String[] categorys;
    //Array de elementos de la categoría
    private String[] categorysData;
    private String[] levels;


    private static final String CREAR_WORDS_TABLE="CREATE TABLE WORDS (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "WORD TEXT," +
            "CATEGORY TEXT" +
            ")";

    private static final String ELIMINAR_WORDS_TABLE ="DROP TABLE IF EXISTS WORDS" ;



    /**
     * Método constructor: contexto, nombre base datos, cursorFactory, versión base de datos.
     * @param context
     */
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context=context;
    }

    /**
     * crea la base de datos por primera vez.
     * @param database nombre de la base de datos
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        this.database=database;

        database.execSQL(CREAR_WORDS_TABLE);

        //Llenamos la base de datos con datos de prueba
        fillWordsTableFromResources();

    }

    /**
     * Actualiza la base de datos a una versión posterior
     * @param database base de datos
     * @param oldVersion version Antigua
     * @param newVersion versión nueva
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        //Se elimina la versión anterior de la tabla
        database.execSQL(ELIMINAR_WORDS_TABLE);

        //Se crea la nueva versión de la tabla
        onCreate(database);

    }

    /**
     * Habilita restricciones de clave foránea
     * @param database base de datos
     */
    @Override
    public void onConfigure(SQLiteDatabase database) {
        super.onConfigure(database);
        database.setForeignKeyConstraintsEnabled(true);
    }


    /**
     * Administra las conexiones simultáneas a la base de datos.
     * Garantiza que solo exista una instancia de DBHelper en un momento dado.
     * @param context Contexto de aplicación
     * @return Objeto de la clase auxiliar SQlite
     */
    public static synchronized DBHelper getInstance(Context context) {
        //Consigue el contexto de aplicación del contexto de la actividad
        if (dbHelper == null) {
            dbHelper= new DBHelper(context);
        }
        return dbHelper;
    }

    /**
     * Actualiza la base de datos a una versión anterior
     * @param database base de datos
     * @param oldVersion
     * @param newVersion
     */
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        onUpgrade(database, oldVersion, newVersion);
    }



    /*******************************************************************************************/


    /****** PALABRAS ******/



    /**
     * Rellena la tabla de palabras
     */
    public void fillWordsTableFromResources() {

        //Obtiene el array de categorías y niveles de los recursos
        categorys =context.getResources().getStringArray(R.array.categoria);
        levels = context.getResources().getStringArray(R.array.nivel);

        //Doble bucle: Recorre el array de categorías e inserta las categorías en la base de datos
        for (String category : categorys) {
            //Devuelve un identificador de recurso para el nombre de recurso dado (categoría): nombre recurso, tipo, paquete
            int id_category= context.getResources().getIdentifier(category, "array", context.getPackageName());
            //Llena un array con todos los elementos de cada categoría
            categorysData = context.getResources().getStringArray(id_category);
            for (int i = 0; i < categorysData.length; i++) {

                ContentValues value = new ContentValues();
                value.put("WORD", categorysData[i]);
                value.put("CATEGORY", category);
                //Inserta una palabra en la tabla words
                database.insert("WORDS", null, value);

            }
        }

    }

    /**
     * Llena la base de datos de palabras con datos de prueba
     */
    public void fillWordsTable(ArrayList<Word> wordList){
        for (Word word: wordList) {
            insertWord(word);
        }
    }

    /**
     * Inserta una palabra en la base de datos. Parte de una conexión abierta
     * @param word
     */
    private void insertWord(Word word) {

        ContentValues value = new ContentValues();
        value.put("WORD", word.getName());
        value.put("CATEGORY", word.getCategory());
        //Inserta una palabra en la tabla words
        database.insert("WORDS", null, value);

    }


    /**
     * Agrega una palabra en la base de datos. Abre una conexión DB
     * @param word palabra
     */
    public void addWord(Word word) {
        //Abre una conexión con la base de datos
        database = getWritableDatabase();
        //Inserta un nuevo usuario en la tabla Users
        if (database.isOpen()) {
            insertWord(word);
        }
        //cierra la conexión
        if(database !=null){
            database.close();
        }

    }


    /**
     * Obtiene una palabra aleatoria de la base de datos, según categoría elegida
     * @param category categoría de la palabra
     * @return
     */
    @SuppressLint("Range")
    public String getWord(String category) {
        String word="";
        ArrayList<String> wordList=new ArrayList<>();
        Random random;
        //Abre una conexión a la base de datos
        database = getReadableDatabase();

        if(database.isOpen()){
            //Array de parámetros
            String[] parametros = new String[] {category};
            //Argumentos de selección
            //  String args="ID? AND CATEGORY=?";
            // String sql="SELECT WORD FROM WORDS WHERE ID=? AND CATEGORY=?";
            Cursor cursor;

            //Opción 1: Realiza una consulta con los parámetros seleccionados
            cursor= database.rawQuery("SELECT WORD FROM WORDS WHERE CATEGORY=?",parametros);

            //Opción 2: consulta con parámetros
            // cursor = db.query("WORDS", null, args, parametros, null, null, null);

            //si hay registros, recorre las palabras de la categoría y las almacena en un array
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    word = cursor.getString(cursor.getColumnIndex("WORD"));
                    wordList.add(word);
                } while (cursor.moveToNext());
            }

            //cerramos el cursor
            if(cursor!=null) cursor.close();
        }

        //cierra la conexión
        if(database !=null){
            database.close();
        }

        random=new Random();

        word=wordList.get(random.nextInt(wordList.size()));

        return word;
    }

    /**
     * Obtiene una lista completa de palabras, según su categoría
     * @param category Categoría o área temática
     * @return lista de preguntas
     */
    @SuppressLint("Range")
    public ArrayList<String> getWordList(String category) {
        String word="";
        ArrayList<String> wordList=new ArrayList<>();

        //abre la base de datos, en modo lectura
        database = getReadableDatabase();

        //Si la base de datos está abierta.
        if(database.isOpen()) {
            //Array de parámetros
            String[] parametros = new String[] {category};
            //Argumentos de selección
            String args="category=?";
            Cursor cursor;

            //Opción 1: Realiza una consulta con los parámetros seleccionados
            cursor= database.rawQuery("select * from words where category=?",parametros);

            //Opción 2: consulta con parámetros
            // cursor = db.query("questions", null, args, parametros, null, null, null);

            //si la base de datos tiene algún registro
            if (cursor != null && cursor.getCount() > 0) {
                //mientras la base de datos tenga registros
                if (cursor.moveToFirst()) { //si hay registros
                    do {
                        word = cursor.getString(cursor.getColumnIndex("WORD"));
                        wordList.add(word);

                    } while (cursor.moveToNext()); //mientras haya registros
                }
                //cerramos el cursor
                cursor.close();
            }
            //cerramos la base de datos
            database.close();
        }

        return wordList;
    }

}
