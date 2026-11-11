package br.edu.utfpr.cantina.model;

import java.math.BigDecimal;

public class Produto {
    private int idProduto;
    private String nome;
    private BigDecimal preco;
    private int quantidadeEstoque;

    public Produto(int idProduto, String nome, BigDecimal preco, int quantidadeEstoque) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }
    @Override public String toString() { return nome + " - R$ " + preco + " (estoque: " + quantidadeEstoque + ")"; }
}
