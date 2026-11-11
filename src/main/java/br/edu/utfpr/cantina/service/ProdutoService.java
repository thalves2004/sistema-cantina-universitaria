package br.edu.utfpr.cantina.service;

import br.edu.utfpr.cantina.model.Produto;
import br.edu.utfpr.cantina.repository.ProdutoRepositoryPort;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ProdutoService {
    private final ProdutoRepositoryPort repo;

    public ProdutoService(ProdutoRepositoryPort repo) {
        this.repo = Objects.requireNonNull(repo, "O repositório de produtos é obrigatório.");
    }

    public List<Produto> listar() throws SQLException { return repo.listar(); }

    public void salvar(Produto produto) throws SQLException {
        if (produto == null) throw new IllegalArgumentException("Informe os dados do produto.");
        if (produto.getNome() == null || produto.getNome().trim().isEmpty())
            throw new IllegalArgumentException("Informe o nome do produto.");
        String nome = produto.getNome().trim();
        if (nome.length() > 120) throw new IllegalArgumentException("Nome deve ter até 120 caracteres.");
        if (produto.getPreco() == null || produto.getPreco().signum() < 0)
            throw new IllegalArgumentException("Informe um preço válido.");
        if (produto.getQuantidadeEstoque() < 0)
            throw new IllegalArgumentException("Estoque não pode ser negativo.");
        produto.setNome(nome);
        repo.salvar(produto);
    }

    public void excluir(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Selecione um produto.");
        repo.excluir(id);
    }
}
