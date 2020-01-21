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

    public MonedaParserSAX(String url)
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
        //Se obtiene una instancia para crear el parseador
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
            return handler.getListadomonedas();
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


    // Clase Manejador para leer el archivo XML de monedas del BCE

    private class ManejadorMonedasSAX extends DefaultHandler
    {
        private ArrayList<Moneda> listadomonedas;
        private Moneda moneda;

        public ArrayList<Moneda> getListadomonedas()
        {
            return listadomonedas;
        }

        public void setListadomonedas(ArrayList<Moneda> listadomonedas)
        {
            this.listadomonedas = listadomonedas;
        }

        @Override
        public void startDocument() throws SAXException
        {
            super.startDocument();
            listadomonedas = new ArrayList<Moneda>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
            super.startElement(uri, localName, qName, attributes);

            if((localName.equals("Cube"))&&(attributes.getLength()==2))
            {
                //EStamos dentro de una moneda
                listadomonedas.add(new Moneda(attributes.getValue("currency"),Float.parseFloat(attributes.getValue("rate"))));
            }
        }
    }
}
