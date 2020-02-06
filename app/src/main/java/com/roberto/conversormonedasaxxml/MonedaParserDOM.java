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

        String datos_nodo[] = null;
        Node node;

        abrirDOM();

        Node raiz = doc.getFirstChild();
        NodeList nodelist = raiz.getChildNodes();

        for (int i = 0; i < nodelist.getLength(); i++)
        {
            Moneda moneda = null;
            node = nodelist.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                datos_nodo = procesarMoneda(node);
                moneda = new Moneda(datos_nodo[0], Float.parseFloat(datos_nodo[1]));
            }
            listadoMonedas.add(moneda);
        }

        return listadoMonedas;
    }

    protected String[] procesarMoneda(Node n)
    {
        String datos[] = new String[3];
        Node ntemp = null;
        int contador = 1;

        datos[0] = n.getAttributes().item(0).getNodeValue();

        NodeList nodos = n.getChildNodes();
        for (int i = 0; i < nodos.getLength(); i++)
        {
            ntemp = nodos.item(i);

            if (ntemp.getNodeType() == Node.ELEMENT_NODE)
            {
                datos[contador] = ntemp.getChildNodes().item(0).getNodeValue();
                contador++;
            }
        }

        return datos;
    }
}
