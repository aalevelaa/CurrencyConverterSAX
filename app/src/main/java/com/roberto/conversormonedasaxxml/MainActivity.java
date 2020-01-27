package com.roberto.conversormonedasaxxml;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private ArrayList<Moneda> monedas;
    private Spinner spinOrigen, spinDestino;
    private EditText textoOrig, textoDest;
    private TextView tvDom, tvSax;

    private Button switchCurrency;
    private Switch switchDOMSAX;

    private int posOrigen, posDestino;
    private float exchangeValueOri, rateOri;
    private float exchangeValueDest, rateDest;
    private float finalValue;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar)findViewById(R.id.myToolbar));

        CargarXmlTask tarea = new CargarXmlTask();
        tarea.execute("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");

        textoOrig = findViewById(R.id.origen_edit);
        textoDest = findViewById(R.id.destino_edit);

        switchCurrency = findViewById(R.id.buttonSwitch);
        switchDOMSAX = findViewById(R.id.switch2);

        tvSax = findViewById(R.id.tvSAX);
        tvDom = findViewById(R.id.tvDOM);

        textoOrig.setText("1");

        switchDOMSAX.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                Toolbar toolbar = findViewById(R.id.myToolbar);

                if (buttonView.isChecked())
                {
                    toolbar.setBackgroundColor(ContextCompat.getColor(switchDOMSAX.getContext(), R.color.colorSecondary));
                    switchCurrency.setTextColor(ContextCompat.getColor(switchDOMSAX.getContext(), R.color.colorSecondary));
                }
                else
                {
                    toolbar.setBackgroundColor(ContextCompat.getColor(switchDOMSAX.getContext(), R.color.colorPrimary));
                    switchCurrency.setTextColor(ContextCompat.getColor(switchDOMSAX.getContext(), R.color.colorPrimary));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.switch_menu, menu);
        return true;
    }

    private class CargarXmlTask extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground (String... cadena)
        {
            //Se ejecuta este hilo para no esperar al fichero XML

            MonedaParserSAX parser = new MonedaParserSAX(cadena[0]);
            monedas = parser.parse();

            monedas.add(0, new Moneda("EUR", 1f));

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

            spinDestino.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected (AdapterView<?> parent, View view, int pos, long id)
                        {
                            Moneda mon = (Moneda) parent.getItemAtPosition(pos);
                            posDestino = pos;
                            exchangeValueDest = mon.getCambio();

                            rateDest = mon.getCambio() / exchangeValueOri;

                            finalValue = rateDest * Float.parseFloat(String.valueOf(textoOrig.getText()));
                            textoDest.setText(String.valueOf(finalValue));
                        }

                        @Override
                        public void onNothingSelected (AdapterView<?> parent)
                        {
                            Log.i("Info","No se seleccionó ninguna divisa");
                        }
                    });

            spinOrigen.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected (AdapterView<?> parent, View view, int pos, long id)
                    {
                        Moneda mon = (Moneda) parent.getItemAtPosition(pos);
                        posOrigen = pos;
                        exchangeValueOri = mon.getCambio();

                        rateOri = exchangeValueDest / mon.getCambio();

                        finalValue = rateOri * Float.parseFloat(String.valueOf(textoOrig.getText()));
                        textoDest.setText(String.valueOf(finalValue));
                    }

                    @Override
                    public void onNothingSelected (AdapterView<?> parent)
                    {
                        Log.i("Info","No se seleccionó ninguna divisa");
                    }
                });


            switchCurrency.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (textoOrig.getText().length() >= 1)
                    {
                        spinOrigen.setSelection(posDestino);
                        spinDestino.setSelection(posOrigen);
                    }
                    else
                    {
                        textoOrig.setText("1");
                        spinOrigen.setSelection(posDestino);
                        spinDestino.setSelection(posOrigen);
                    }
                }
            });

            textoOrig.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s)
                {
                    Moneda m1 = (Moneda) spinOrigen.getSelectedItem();
                    Moneda m2 = (Moneda) spinDestino.getSelectedItem();

                    if (textoOrig.getText().length() >= 1)
                    {
                        rateOri = m2.getCambio() / m1.getCambio();

                        finalValue = rateOri * Float.parseFloat(String.valueOf(textoOrig.getText()));
                        textoDest.setText(String.valueOf(finalValue));
                    }
                    else
                    {
                        return;
                    }
                }
            });
        }
    }
}
