package com.roberto.conversormonedasaxxml;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorSpinner extends ArrayAdapter<Moneda>
{
    ArrayList<Moneda> m;
    Context c;
    int layout;


    public AdaptadorSpinner (@NonNull Context context, int resource, @NonNull List<Moneda> objects)
    {
        super(context, resource, objects);
        this.c = context;
        this.layout = resource;
        this.m = (ArrayList<Moneda>)objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View v, @NonNull ViewGroup parent)
    {
        if (v == null)
        {
            v = ((MainActivity)c).getLayoutInflater().inflate(this.layout,parent,false);

            Log.i("Informacion","Se instancia un objeto en getView" + position);
        }

        Log.i("Informacion","Se ejecuta getView " + position);

        TextView t = v.findViewById(R.id.nombremoneda_text);
        t.setText(this.m.get(position).getNombre());

        //Cargar la imagen de internet??
        ImageView i = v.findViewById(R.id.moneda_imagen);

        String p = "https://www.ecb.europa.eu/shared/img/flags/" + this.m.get(position).getNombre() + ".gif";

        if (this.m.get(position).getNombre().equals("EUR"))
        {
            p = "https://www.xe.com//themes/xe/images/flags/svg/eur.svg";
            i.setImageResource(R.drawable.ic_eur);
        }
        else
        {
            Picasso.get().load(p).into(i);
        }

        return v;
    }

    @Override
    public int getCount () {
        return m.size();
    }

    @Override
    public View getDropDownView (int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = ((MainActivity)c).getLayoutInflater().inflate(this.layout,parent,false);

            Log.i("Informacion","Se instancia un objeto en getDropDownView" + position);
        }

        Log.i("Informacion","Se ejecuta getDropDownView " + position);

        TextView t = convertView.findViewById(R.id.nombremoneda_text);
        t.setText(this.m.get(position).getNombre());

        ImageView i = convertView.findViewById(R.id.moneda_imagen);
        String p = "https://www.ecb.europa.eu/shared/img/flags/" + this.m.get(position).getNombre() + ".gif";

        if (this.m.get(position).getNombre().equals("EUR"))
        {
            p = "https://www.xe.com//themes/xe/images/flags/svg/eur.svg";
            i.setImageResource(R.drawable.ic_eur);
        }
        else
        {
            Picasso.get().load(p).into(i);
        }

        return convertView;
    }

    public void deshabilitarElemento (int posicion)
    {

    }
}
