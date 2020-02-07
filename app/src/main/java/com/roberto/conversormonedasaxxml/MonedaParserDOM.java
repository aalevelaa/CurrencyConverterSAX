package com.roberto.conversormonedasaxxml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MonedaParserDOM
{
    private URL urlcambios;
    private Document doc;

    public MonedaParserDOM(String url)
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

    public int abrirDOM()
    {
        doc = null;

        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);

            DocumentBuilder builder = factory.newDocumentBuilder();

            doc = builder.parse(urlcambios.openStream());
            doc.getDocumentElement().normalize();

            return 0;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public ArrayList<Moneda> parse()
    {
        ArrayList<Moneda> listadoMonedas = new ArrayList<Moneda>();

        String atributosNodo[] = new String[2];
        Element e;

        abrirDOM();

        NodeList nodelist = doc.getElementsByTagName("Cube");

        for (int i = 0; i < nodelist.getLength(); i++)
        {
            Moneda moneda = null;
            e = (Element) nodelist.item(i);

            if (e.getNodeType() == Node.ELEMENT_NODE && e.getAttributes().getLength() == 2)
            {
                atributosNodo[0] = e.getAttribute("currency");
                atributosNodo[1] = e.getAttribute("rate");
                moneda = new Moneda(atributosNodo[0], Float.parseFloat(atributosNodo[1]));
                listadoMonedas.add(moneda);
            }
        }
        return listadoMonedas;
    }

}
