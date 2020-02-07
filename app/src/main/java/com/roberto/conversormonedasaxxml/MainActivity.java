package com.roberto.conversormonedasaxxml;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;


import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
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

        textoOrig = findViewById(R.id.origen_edit);
        textoDest = findViewById(R.id.destino_edit);

        switchCurrency = findViewById(R.id.buttonSwitch);

        tvSax = findViewById(R.id.tvSAX);
        tvDom = findViewById(R.id.tvDOM);


        CargarXmlTask tarea = new CargarXmlTask();
        tarea.execute("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml", "SAX");

        textoOrig.setText("1");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        final Toolbar toolbar = findViewById(R.id.myToolbar);

        final int colorTexto = ContextCompat.getColor( toolbar.getContext(), R.color.textLight);
        final int colorPrimario = ContextCompat.getColor( toolbar.getContext(), R.color.colorPrimary);
        final int colorSecundario = ContextCompat.getColor( toolbar.getContext(), R.color.colorSecondary);

        toolbar.setTitle("Conversor [ SAX ]");
        toolbar.setTitleTextColor(colorTexto);

        getMenuInflater().inflate(R.menu.switch_menu, menu);

        this.switchDOMSAX = menu.findItem(R.id.app_bar_switch).getActionView().findViewById(R.id.switch1);

        this.switchDOMSAX.setOnCheckedChangeListener(
                new Switch.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (!isChecked)
                        {
                            toolbar.setTitle("Conversor [ SAX ]");
                            toolbar.setTitleTextColor(colorTexto);
                            toolbar.setBackgroundColor(colorPrimario);
                            switchCurrency.setTextColor(colorPrimario);

                            CargarXmlTask tarea = new CargarXmlTask();
                            tarea.execute("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml", "SAX");
                        }
                        else
                        {
                            toolbar.setTitle("Conversor [ DOM ]");
                            toolbar.setTitleTextColor(colorTexto);
                            toolbar.setBackgroundColor(colorSecundario);
                            switchCurrency.setTextColor(colorSecundario);

                            CargarXmlTask tarea = new CargarXmlTask();
                            tarea.execute("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml", "DOM");
                        }
                    }
                }
        );
        return true;
    }


    private class CargarXmlTask extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground (String... cadena)
        {
            URL url = null;
            try {
                url = new URL(cadena[0]);
            } catch (MalformedURLException e) {
                throw new RuntimeException();
            }

            //Se ejecuta este hilo para no esperar al fichero XML
            switch (cadena[1])
            {
                case "SAX":
                    MonedaParserSAX parser = new MonedaParserSAX(url);
                    monedas = parser.parse();
                    break;
                case "DOM":
                    MonedaParserDOM parse = new MonedaParserDOM(url);
                    monedas = parse.parse();
                    break;
            }

            monedas.add(0, new Moneda("EUR", 1f));

            return true;
        }


        @Override
        protected void onPostExecute (Boolean o)
        {
            // Una vez cargado el XML se ejecuta este metodo

            spinOrigen = findViewById(R.id.origen_spinner);
            spinDestino = findViewById(R.id.destino_spinner);
            AdaptadorSpinner adaptador1 = new AdaptadorSpinner(MainActivity.this, R.layout.layout_elemento_spinner, monedas);
            AdaptadorSpinner adaptador2 = new AdaptadorSpinner(MainActivity.this, R.layout.layout_elemento_spinner, monedas);

            spinOrigen.setAdapter(adaptador1);
            spinDestino.setAdapter(adaptador2);
            spinDestino.setSelection(1);

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

                            ((AdaptadorSpinner)spinDestino.getAdapter()).deshabilitarElemento(posOrigen);
                        }

                        @Override
                        public void onNothingSelected (AdapterView<?> parent)
                        {
                            Log.i("Info","No se seleccionó ninguna divisa");
                        }
                    });


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

                            ((AdaptadorSpinner)spinOrigen.getAdapter()).deshabilitarElemento(posDestino);
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

                    ((AdaptadorSpinner)spinDestino.getAdapter()).deshabilitarElemento(posOrigen);
                    ((AdaptadorSpinner)spinOrigen.getAdapter()).deshabilitarElemento(posDestino);
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
