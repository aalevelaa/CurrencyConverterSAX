package com.roberto.conversormonedasaxxml;

public class Moneda
{
    private String nombre;
    private float cambio;

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public float getCambio()
    {
        return cambio;
    }

    public void setCambio(float cambio)
    {
        this.cambio = cambio;
    }

    public Moneda(String nombre, float cambio)
    {
        this.nombre = nombre;
        this.cambio = cambio;
    }
}
