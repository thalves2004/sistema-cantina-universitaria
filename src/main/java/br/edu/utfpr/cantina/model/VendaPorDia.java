package br.edu.utfpr.cantina.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VendaPorDia {
    private final LocalDate data; private final int pedidos; private final BigDecimal total;
    public VendaPorDia(LocalDate data,int pedidos,BigDecimal total){this.data=data;this.pedidos=pedidos;this.total=total;}
    public LocalDate getData(){return data;} public int getPedidos(){return pedidos;} public BigDecimal getTotal(){return total;}
}
