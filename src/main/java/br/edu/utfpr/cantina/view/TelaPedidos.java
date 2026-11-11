package br.edu.utfpr.cantina.view;

import br.edu.utfpr.cantina.controller.*;
import br.edu.utfpr.cantina.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.sql.SQLException;

public class TelaPedidos extends JPanel {
    private final ClienteController clientes;
    private final ProdutoController produtos;
    private final PedidoController pedidos;
    private final JComboBox<Cliente> comboCliente=new JComboBox<>();
    private final JComboBox<Produto> comboProduto=new JComboBox<>();
    private final JSpinner quantidade=new JSpinner(new SpinnerNumberModel(1,1,999,1));
    private final DefaultTableModel modeloItens=new DefaultTableModel(new Object[]{"ID","Produto","Quantidade","Preço unitário","Subtotal"},0){public boolean isCellEditable(int r,int c){return false;}};
    private final JTable tabelaItens=new JTable(modeloItens);
    private final DefaultTableModel modeloPedidos=new DefaultTableModel(new Object[]{"Pedido","Cliente","Data","Itens","Total","Status"},0){public boolean isCellEditable(int r,int c){return false;}};
    private final JTable tabelaPedidos=new JTable(modeloPedidos);
    private final JComboBox<Cliente> comboHistorico=new JComboBox<>();
    private final DefaultTableModel modeloHistorico=new DefaultTableModel(new Object[]{"Pedido","Data","Total","Status"},0){public boolean isCellEditable(int r,int c){return false;}};
    private final JTable tabelaHistorico=new JTable(modeloHistorico);
    private final DefaultTableModel modeloDetalhes=new DefaultTableModel(new Object[]{"Produto","Quantidade","Preço unitário","Subtotal"},0){public boolean isCellEditable(int r,int c){return false;}};
    private final JTable tabelaDetalhes=new JTable(modeloDetalhes);
    private List<Pedido> pedidosHistorico=new ArrayList<>();
    private final List<ItemPedido> itens=new ArrayList<>();
    private final JLabel totalLabel=new JLabel("Total: R$ 0,00");
    private final NumberFormat moeda=NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
    private final DateTimeFormatter data=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public TelaPedidos(ClienteController c,ProdutoController p,PedidoController pe){
        this.clientes=c;this.produtos=p;this.pedidos=pe;setLayout(new BorderLayout(10,10));setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JTabbedPane abas=new JTabbedPane();abas.addTab("Novo pedido",criarNovoPedido());abas.addTab("Consultar e atualizar",criarConsulta());abas.addTab("Histórico por cliente",criarHistorico());add(abas,BorderLayout.CENTER);recarregarCombos();atualizarPedidos();
    }
    private JPanel criarNovoPedido(){
        JPanel painel=new JPanel(new BorderLayout(8,8));JPanel topo=new JPanel(new GridLayout(2,2,8,8));topo.add(new JLabel("Cliente"));topo.add(comboCliente);topo.add(new JLabel("Produto"));topo.add(comboProduto);painel.add(topo,BorderLayout.NORTH);
        JPanel centro=new JPanel(new BorderLayout(6,6));JPanel controles=new JPanel(new FlowLayout(FlowLayout.LEFT));controles.add(new JLabel("Quantidade:"));controles.add(quantidade);JButton adicionar=new JButton("Adicionar produto");adicionar.addActionListener(e->adicionarItem());controles.add(adicionar);JButton remover=new JButton("Remover selecionado");remover.addActionListener(e->removerItem());controles.add(remover);centro.add(controles,BorderLayout.NORTH);centro.add(new JScrollPane(tabelaItens),BorderLayout.CENTER);painel.add(centro,BorderLayout.CENTER);
        JPanel rodape=new JPanel(new BorderLayout());totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD,18f));rodape.add(totalLabel,BorderLayout.WEST);JPanel botoes=new JPanel(new FlowLayout(FlowLayout.RIGHT));JButton limpar=new JButton("Limpar");limpar.addActionListener(e->limparItens());JButton registrar=new JButton("Registrar pedido");registrar.addActionListener(e->registrarPedido());botoes.add(limpar);botoes.add(registrar);rodape.add(botoes,BorderLayout.EAST);painel.add(rodape,BorderLayout.SOUTH);return painel;
    }
    private JPanel criarConsulta(){JPanel p=new JPanel(new BorderLayout(8,8));p.add(new JScrollPane(tabelaPedidos),BorderLayout.CENTER);JPanel a=new JPanel(new FlowLayout(FlowLayout.LEFT));JComboBox<String> status=new JComboBox<>(new String[]{"PENDENTE","EM PREPARO","PRONTO","ENTREGUE","CANCELADO"});JButton atualizar=new JButton("Atualizar status do selecionado");atualizar.addActionListener(e->{int row=tabelaPedidos.getSelectedRow();if(row<0){aviso("Selecione um pedido na tabela.");return;}try{int id=(int)modeloPedidos.getValueAt(row,0);pedidos.atualizarStatus(id,(String)status.getSelectedItem());atualizarPedidos();aviso("Status atualizado com sucesso.");}catch(Exception ex){erro(ex);}});JButton recarregar=new JButton("Recarregar");recarregar.addActionListener(e->{recarregarCombos();atualizarPedidos();});a.add(new JLabel("Novo status:"));a.add(status);a.add(atualizar);a.add(recarregar);p.add(a,BorderLayout.SOUTH);return p;}
    private JPanel criarHistorico(){
        JPanel p=new JPanel(new BorderLayout(8,8));JPanel topo=new JPanel(new FlowLayout(FlowLayout.LEFT));topo.add(new JLabel("Cliente:"));topo.add(comboHistorico);
        JButton buscar=new JButton("Consultar histórico");buscar.addActionListener(e->carregarHistorico());topo.add(buscar);p.add(topo,BorderLayout.NORTH);
        tabelaHistorico.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);tabelaHistorico.setRowHeight(24);tabelaDetalhes.setRowHeight(24);
        tabelaHistorico.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())mostrarDetalhesHistorico();});
        JSplitPane divisao=new JSplitPane(JSplitPane.VERTICAL_SPLIT,new JScrollPane(tabelaHistorico),new JScrollPane(tabelaDetalhes));divisao.setResizeWeight(0.55);p.add(divisao,BorderLayout.CENTER);
        return p;
    }
    private void carregarHistorico(){Cliente c=(Cliente)comboHistorico.getSelectedItem();if(c==null){aviso("Selecione um cliente.");return;}try{pedidosHistorico=pedidos.listarPorCliente(c.getIdCliente());modeloHistorico.setRowCount(0);modeloDetalhes.setRowCount(0);for(Pedido p:pedidosHistorico)modeloHistorico.addRow(new Object[]{p.getIdPedido(),p.getDataHora().format(data),moeda.format(p.calcularTotal()),p.getStatus()});if(pedidosHistorico.isEmpty())aviso("Este cliente ainda não possui pedidos.");}catch(Exception e){erro(e);}}
    private void mostrarDetalhesHistorico(){int row=tabelaHistorico.getSelectedRow();modeloDetalhes.setRowCount(0);if(row<0||row>=pedidosHistorico.size())return;Pedido p=pedidosHistorico.get(row);for(ItemPedido i:p.getItens())modeloDetalhes.addRow(new Object[]{i.getProduto().getNome(),i.getQuantidade(),moeda.format(i.getProduto().getPreco()),moeda.format(i.calcularSubtotal())});}
    private void recarregarCombos(){try{comboCliente.removeAllItems();comboHistorico.removeAllItems();for(Cliente c:clientes.listar()){comboCliente.addItem(c);comboHistorico.addItem(c);}comboProduto.removeAllItems();for(Produto p:produtos.listar())if(p.getQuantidadeEstoque()>0)comboProduto.addItem(p);}catch(Exception e){erro(e);}}
    private void adicionarItem(){Produto p=(Produto)comboProduto.getSelectedItem();if(p==null){aviso("Cadastre um produto com estoque disponível.");return;}int qtd=(Integer)quantidade.getValue();if(qtd>p.getQuantidadeEstoque()){aviso("Quantidade maior que o estoque disponível ("+p.getQuantidadeEstoque()+").");return;}for(ItemPedido i:itens)if(i.getProduto().getIdProduto()==p.getIdProduto()){if(i.getQuantidade()+qtd>p.getQuantidadeEstoque()){aviso("A quantidade total deste produto ultrapassa o estoque.");return;}i.setQuantidade(i.getQuantidade()+qtd);atualizarItens();return;}itens.add(new ItemPedido(p,qtd));atualizarItens();}
    private void removerItem(){int row=tabelaItens.getSelectedRow();if(row<0){aviso("Selecione um item.");return;}itens.remove(row);atualizarItens();}
    private void atualizarItens(){modeloItens.setRowCount(0);BigDecimal total=BigDecimal.ZERO;for(ItemPedido i:itens){BigDecimal sub=i.calcularSubtotal();total=total.add(sub);modeloItens.addRow(new Object[]{i.getProduto().getIdProduto(),i.getProduto().getNome(),i.getQuantidade(),moeda.format(i.getProduto().getPreco()),moeda.format(sub)});}totalLabel.setText("Total: "+moeda.format(total));}
    private void limparItens(){itens.clear();atualizarItens();}
    private void registrarPedido(){Cliente c=(Cliente)comboCliente.getSelectedItem();if(c==null){aviso("Cadastre e selecione um cliente.");return;}if(itens.isEmpty()){aviso("Adicione produtos ao pedido.");return;}Pedido pedido=new Pedido(0,c,java.time.LocalDateTime.now(),"PENDENTE");for(ItemPedido i:itens)pedido.adicionarItem(new ItemPedido(i.getProduto(),i.getQuantidade()));try{pedidos.registrar(pedido);aviso("Pedido registrado! Estoque atualizado automaticamente.");limparItens();recarregarCombos();atualizarPedidos();}catch(Exception e){erro(e);recarregarCombos();}}
    private void atualizarPedidos(){try{modeloPedidos.setRowCount(0);for(Pedido p:pedidos.listar()){modeloPedidos.addRow(new Object[]{p.getIdPedido(),p.getCliente().getNome(),p.getDataHora().format(data),p.getItens().size(),moeda.format(p.calcularTotal()),p.getStatus()});}}catch(Exception e){erro(e);}}
    private void aviso(String m){JOptionPane.showMessageDialog(this,m,"Cantina Universitária",JOptionPane.INFORMATION_MESSAGE);}private void erro(Exception e){JOptionPane.showMessageDialog(this,e.getMessage()==null?"Erro inesperado.":e.getMessage(),"Não foi possível concluir",JOptionPane.ERROR_MESSAGE);}
}
