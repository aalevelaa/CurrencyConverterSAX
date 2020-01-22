package com.roberto.conversormonedasaxxml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MonedaParserSAX
{
    private URL urlcambios;

    public MonedaParserSAX (String url)
    {
        try
        {
            //Se crea la URL del RECURSO
            this.urlcambios = new URL(url);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<Moneda> parse()
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try
        {
            //Obtenemos el parser
            SAXParser parser = factory.newSAXParser();

            //Definimos el manejador
            ManejadorMonedasSAX handler = new ManejadorMonedasSAX();

            //Vinculamos el fichero XML con el manejador, en este punto se invocan a todos
            //los metodos callback del manejador, construyendo el ArrayList de monedas
            parser.parse(this.getInputStream(), handler);

            return handler.getListadoMonedas();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private InputStream getInputStream()
    {
        try
        {
            return this.urlcambios.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    // Manejador para leer el XML de divisas del BCE

    private class ManejadorMonedasSAX extends DefaultHandler
    {
        private ArrayList<Moneda> listadoMonedas;
        private Moneda moneda;

        public ArrayList<Moneda> getListadoMonedas()
        {
            return listadoMonedas;
        }

        public void setListadoMonedas(ArrayList<Moneda> listadoMonedas)
        {
            this.listadoMonedas = this.listadoMonedas;
        }

        @Override
        public void startDocument() throws SAXException
        {
            super.startDocument();
            listadoMonedas = new ArrayList<Moneda>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
            super.startElement(uri, localName, qName, attributes);

            if ((localName.equals("Cube")) && (attributes.getLength() == 2))
            {
                //Leyendo una moneda
                listadoMonedas.add(
                        new Moneda(attributes.getValue("currency"), Float.parseFloat(attributes.getValue("rate")))
                );
            }
        }
    }
}
