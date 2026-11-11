package br.edu.utfpr.cantina.service;

import br.edu.utfpr.cantina.model.*;
import br.edu.utfpr.cantina.repository.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ServicosTest {
    @Test void clienteValidoNormalizaNomeEEmail() throws Exception {
        FakeClienteRepository repo = new FakeClienteRepository();
        Cliente cliente = new Cliente("  Ana Silva  ", " ANA@EXEMPLO.COM ");
        new ClienteService(repo).salvar(cliente);
        assertEquals("Ana Silva", repo.salvo.getNome());
        assertEquals("ana@exemplo.com", repo.salvo.getEmail());
        assertEquals(1, repo.insercoes);
    }

    @Test void clienteInvalidoNaoChegaAoRepositorio() {
        FakeClienteRepository repo = new FakeClienteRepository();
        assertThrows(IllegalArgumentException.class, () -> new ClienteService(repo).salvar(new Cliente("", "ana@exemplo.com")));
        assertEquals(0, repo.insercoes);
    }

    @Test void produtoValidoEDelegadoAoRepositorio() throws Exception {
        FakeProdutoRepository repo = new FakeProdutoRepository();
        Produto produto = new Produto(0, "  Café  ", new BigDecimal("5.50"), 10);
        new ProdutoService(repo).salvar(produto);
        assertEquals("Café", repo.salvo.getNome());
        assertEquals(1, repo.salvamentos);
    }

    @Test void produtoComPrecoNegativoERejeitado() {
        FakeProdutoRepository repo = new FakeProdutoRepository();
        Produto produto = new Produto(0, "Café", new BigDecimal("-0.01"), 10);
        assertThrows(IllegalArgumentException.class, () -> new ProdutoService(repo).salvar(produto));
        assertEquals(0, repo.salvamentos);
    }

    @Test void produtoComEstoqueNegativoERejeitado() {
        FakeProdutoRepository repo = new FakeProdutoRepository();
        Produto produto = new Produto(0, "Café", new BigDecimal("5.00"), -1);
        assertThrows(IllegalArgumentException.class, () -> new ProdutoService(repo).salvar(produto));
        assertEquals(0, repo.salvamentos);
    }

    @Test void pedidoSemItensERejeitado() {
        FakePedidoRepository repo = new FakePedidoRepository();
        Pedido pedido = new Pedido(0, new Cliente(1, "Ana", "ana@exemplo.com"), LocalDateTime.now(), "PENDENTE");
        assertThrows(IllegalArgumentException.class, () -> new PedidoService(repo).registrar(pedido));
        assertFalse(repo.registrado);
    }

    @Test void pedidoValidoEDelegadoAoRepositorio() throws Exception {
        FakePedidoRepository repo = new FakePedidoRepository();
        Pedido pedido = new Pedido(0, new Cliente(1, "Ana", "ana@exemplo.com"), LocalDateTime.now(), "PENDENTE");
        pedido.adicionarItem(new ItemPedido(new Produto(2, "Suco", new BigDecimal("7.00"), 4), 2));
        new PedidoService(repo).registrar(pedido);
        assertTrue(repo.registrado);
    }

    @Test void periodoInvertidoERejeitado() {
        FakeRelatorioRepository repo = new FakeRelatorioRepository();
        RelatorioService service = new RelatorioService(repo);
        assertThrows(IllegalArgumentException.class, () -> service.vendas(LocalDate.of(2026, 2, 2), LocalDate.of(2026, 2, 1)));
        assertFalse(repo.consultado);
    }

    private static class FakeClienteRepository implements ClienteRepositoryPort {
        Cliente salvo; int insercoes;
        public List<Cliente> listarTodos(){ return Collections.emptyList(); }
        public void inserir(Cliente c){ salvo=c; insercoes++; }
        public void atualizar(Cliente c){ salvo=c; }
        public void excluir(int id){}
    }
    private static class FakeProdutoRepository implements ProdutoRepositoryPort {
        Produto salvo; int salvamentos;
        public List<Produto> listar(){ return Collections.emptyList(); }
        public void salvar(Produto p){ salvo=p; salvamentos++; }
        public void excluir(int id){}
    }
    private static class FakePedidoRepository implements PedidoRepositoryPort {
        boolean registrado;
        public void registrar(Pedido p){ registrado=true; }
        public List<Pedido> listarTodos(){ return Collections.emptyList(); }
        public List<Pedido> listarPorCliente(int id){ return Collections.emptyList(); }
        public void atualizarStatus(int id,String status){}
    }
    private static class FakeRelatorioRepository implements RelatorioRepositoryPort {
        boolean consultado;
        public ResumoDashboard carregarResumo(){ return null; }
        public List<VendaPorDia> vendasPorPeriodo(LocalDate i,LocalDate f){ consultado=true; return Collections.emptyList(); }
        public List<String[]> produtosComEstoqueBaixo(){ return Collections.emptyList(); }
    }
}
