package com.repiso.myhangman;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;


public class LetterAdapter extends BaseAdapter {
    private String[] letters;
    private LayoutInflater layoutInflater;
    Button button;

    LetterAdapter(Context context){
        letters=new String[26];
        for(int i=0;i<letters.length;i++){
            //llena el array con las letras del abecedario de la A a la Z
            letters[i]=""+(char)(i+'A');
        }
        //inflar/crear la vista
        layoutInflater=LayoutInflater.from(context);
    }

    /**
     * Retorna la cantidad de elementos de la vista letras
     * @return número de letras
     */
    @Override
    public int getCount() {
        return letters.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Genera la vista de los botones
     * @param position posición de la letra dentro del array
     * @param convertView vista
     * @param parent padre de la vista
     * @return vista del botón
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            button=(Button) layoutInflater.inflate(R.layout.btn_letter,parent,false);
        }else{
            button=(Button) convertView;
        }
        button.setText(letters[position]);
        return button;
    }
}
