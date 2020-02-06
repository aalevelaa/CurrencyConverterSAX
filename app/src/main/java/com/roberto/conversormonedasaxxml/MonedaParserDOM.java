package com.roberto.conversormonedasaxxml;

import org.w3c.dom.Document;
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

        String atributosNodo[] = null;
        Node node;

        abrirDOM();

        NodeList nodelist = doc.getElementsByTagName("Cube");

        for (int i = 0; i < nodelist.getLength(); i++)
        {
            Moneda moneda = null;
            node = nodelist.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE && node.getAttributes().getLength() == 2)
            {
                atributosNodo = procesarMoneda(node);
                moneda = new Moneda(atributosNodo[0], Float.parseFloat(atributosNodo[1]));
            }
            listadoMonedas.add(moneda);
        }

        return listadoMonedas;
    }

    protected String[] procesarMoneda(Node n)
    {
        String atributos[] = new String[2];
        Node ntemp = null;
        int contador = 1;

        atributos[0] = n.getAttributes().item(0).getNodeValue();
        atributos[1] = n.getAttributes().item(1).getNodeValue();

        NodeList nodos = n.getChildNodes();

        for (int i = 0; i < nodos.getLength(); i++)
        {
            ntemp = nodos.item(i);

            if (ntemp.getNodeType() == Node.ELEMENT_NODE)
            {
                atributos[contador] = ntemp.getChildNodes().item(0).getNodeValue();
                contador++;
            }
        }

        return atributos;
    }
}
