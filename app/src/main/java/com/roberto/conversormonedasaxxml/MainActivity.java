package com.roberto.conversormonedasaxxml;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    ArrayList<Moneda> monedas;
    Spinner spinOrigen, spinDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CargarXmlTask tarea = new CargarXmlTask();
        tarea.execute("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");





    }

    private class CargarXmlTask extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground (String... p)
        {
            //Se ejecuta este hilo para no esperar al fichero XML

            MonedaParserSAX parser = new MonedaParserSAX(p[0]);
            monedas = (ArrayList<Moneda>)parser.parse();

            monedas.add(0,new Moneda("EUR",1f));

            return true;
        }


        @Override
        protected void onPostExecute (Boolean o)
        {
            // Una vez cargado el XML se ejecuta este metodo

            spinOrigen = findViewById(R.id.origen_spinner);
            spinDestino = findViewById(R.id.destino_spinner);
            final AdaptadorSpinner adaptador = new AdaptadorSpinner(MainActivity.this, R.layout.layout_elemento_spinner, monedas);

            spinOrigen.setAdapter(adaptador);
            spinDestino.setAdapter(adaptador);
            spinDestino.setSelection(1);

            spinOrigen.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
                {


                }

                @Override
                public void onNothingSelected (AdapterView<?> parent)
                {
                    Log.i("Info","No se seleccionó ninguna divisa");
                }
            });
        }
    }
}
