package br.edu.utfpr.cantina.model;

import java.math.BigDecimal;

public class ResumoDashboard {
    private final int clientes, produtos, estoqueBaixo, pedidosAbertos;
    private final BigDecimal vendasHoje, vendasMes;
    public ResumoDashboard(int clientes, int produtos, int estoqueBaixo, int pedidosAbertos, BigDecimal vendasHoje, BigDecimal vendasMes) {
        this.clientes=clientes; this.produtos=produtos; this.estoqueBaixo=estoqueBaixo; this.pedidosAbertos=pedidosAbertos;
        this.vendasHoje=vendasHoje; this.vendasMes=vendasMes;
    }
    public int getClientes(){return clientes;} public int getProdutos(){return produtos;}
    public int getEstoqueBaixo(){return estoqueBaixo;} public int getPedidosAbertos(){return pedidosAbertos;}
    public BigDecimal getVendasHoje(){return vendasHoje;} public BigDecimal getVendasMes(){return vendasMes;}
}
