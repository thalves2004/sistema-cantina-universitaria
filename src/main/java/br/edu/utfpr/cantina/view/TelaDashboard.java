package br.edu.utfpr.cantina.view;

import br.edu.utfpr.cantina.controller.RelatorioController;
import br.edu.utfpr.cantina.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TelaDashboard extends JPanel {
    private static final Color FUNDO=new Color(244,247,251), AZUL=new Color(31,78,121), TEXTO=new Color(45,55,72);
    private final RelatorioController controller;
    private final NumberFormat moeda=NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
    private final JLabel clientes=valor(),produtos=valor(),estoque=valor(),abertos=valor(),hoje=valor(),mes=valor();
    private final JTextField inicio=new JTextField(LocalDate.now().withDayOfMonth(1).toString(),10), fim=new JTextField(LocalDate.now().toString(),10);
    private final JLabel totalPeriodo=new JLabel("Total do período: R$ 0,00");
    private final DefaultTableModel vendasModel=new DefaultTableModel(new Object[]{"Data","Pedidos","Faturamento"},0){public boolean isCellEditable(int r,int c){return false;}};
    private final DefaultTableModel estoqueModel=new DefaultTableModel(new Object[]{"Produto","Unidades disponíveis"},0){public boolean isCellEditable(int r,int c){return false;}};
    public TelaDashboard(RelatorioController controller){
        this.controller=controller;setLayout(new BorderLayout(16,16));setBackground(FUNDO);setBorder(BorderFactory.createEmptyBorder(20,22,20,22));
        JLabel titulo=new JLabel("Visão geral da cantina");titulo.setFont(new Font("Segoe UI",Font.BOLD,25));titulo.setForeground(TEXTO);
        JLabel subtitulo=new JLabel("Acompanhe a operação, as vendas e os níveis de estoque.");subtitulo.setForeground(new Color(105,116,130));
        JPanel cab=new JPanel();cab.setOpaque(false);cab.setLayout(new BoxLayout(cab,BoxLayout.Y_AXIS));cab.add(titulo);cab.add(Box.createVerticalStrut(5));cab.add(subtitulo);add(cab,BorderLayout.NORTH);
        JPanel centro=new JPanel(new BorderLayout(14,14));centro.setOpaque(false);
        JPanel cards=new JPanel(new GridLayout(2,3,12,12));cards.setOpaque(false);
        cards.add(card("CLIENTES CADASTRADOS",clientes,new Color(41,98,150)));cards.add(card("PRODUTOS",produtos,new Color(37,120,110)));cards.add(card("ESTOQUE BAIXO · ATÉ 5 UN.",estoque,new Color(190,112,35)));
        cards.add(card("PEDIDOS EM ABERTO",abertos,new Color(108,82,160)));cards.add(card("VENDAS DE HOJE",hoje,new Color(38,126,91)));cards.add(card("VENDAS DO MÊS",mes,new Color(44,104,160)));
        centro.add(cards,BorderLayout.NORTH);
        JPanel inferior=new JPanel(new GridLayout(1,2,14,0));inferior.setOpaque(false);
        JTable tv=new JTable(vendasModel);estilizar(tv);JTable te=new JTable(estoqueModel);estilizar(te);
        JPanel relatorio=new JPanel(new BorderLayout(8,8));relatorio.setBackground(Color.WHITE);relatorio.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(225,231,238)),BorderFactory.createEmptyBorder(12,12,12,12)));
        JLabel rt=new JLabel("Relatório de vendas por período");rt.setFont(new Font("Segoe UI",Font.BOLD,15));
        JPanel filtros=new JPanel(new FlowLayout(FlowLayout.LEFT,6,2));filtros.setOpaque(false);filtros.add(new JLabel("De"));filtros.add(inicio);filtros.add(new JLabel("Até"));filtros.add(fim);JButton gerar=new JButton("Gerar");gerar.addActionListener(e->carregarVendas());filtros.add(gerar);JButton exportar=new JButton("Exportar CSV");exportar.addActionListener(e->exportarCsv());filtros.add(exportar);
        JPanel topoRel=new JPanel(new BorderLayout());topoRel.setOpaque(false);topoRel.add(rt,BorderLayout.NORTH);topoRel.add(filtros,BorderLayout.SOUTH);relatorio.add(topoRel,BorderLayout.NORTH);relatorio.add(new JScrollPane(tv),BorderLayout.CENTER);totalPeriodo.setFont(new Font("Segoe UI",Font.BOLD,14));relatorio.add(totalPeriodo,BorderLayout.SOUTH);
        JPanel alerta=new JPanel(new BorderLayout(8,8));alerta.setBackground(Color.WHITE);alerta.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(225,231,238)),BorderFactory.createEmptyBorder(12,12,12,12)));
        JLabel at=new JLabel("Atenção ao estoque");at.setFont(new Font("Segoe UI",Font.BOLD,15));JLabel explicacao=new JLabel("Produtos com 5 unidades ou menos");explicacao.setForeground(new Color(115,124,137));
        JPanel ta=new JPanel(new BorderLayout());ta.setOpaque(false);ta.add(at,BorderLayout.NORTH);ta.add(explicacao,BorderLayout.SOUTH);alerta.add(ta,BorderLayout.NORTH);alerta.add(new JScrollPane(te),BorderLayout.CENTER);
        inferior.add(relatorio);inferior.add(alerta);centro.add(inferior,BorderLayout.CENTER);add(centro,BorderLayout.CENTER);
        JButton atualizar=new JButton("Atualizar painel");atualizar.addActionListener(e->carregarTudo());JPanel rodape=new JPanel(new FlowLayout(FlowLayout.RIGHT));rodape.setOpaque(false);rodape.add(atualizar);add(rodape,BorderLayout.SOUTH);
    }
    private JLabel valor(){JLabel l=new JLabel("—");l.setFont(new Font("Segoe UI",Font.BOLD,25));l.setForeground(Color.WHITE);return l;}
    private JPanel card(String titulo,JLabel numero,Color cor){JPanel p=new JPanel(new BorderLayout(4,8));p.setBackground(cor);p.setBorder(BorderFactory.createEmptyBorder(15,17,15,17));JLabel t=new JLabel(titulo);t.setForeground(new Color(235,242,249));t.setFont(new Font("Segoe UI",Font.BOLD,10));p.add(t,BorderLayout.NORTH);p.add(numero,BorderLayout.CENTER);return p;}
    private void estilizar(JTable t){t.setRowHeight(26);t.setFillsViewportHeight(true);t.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,11));}
    public void atualizarDados(){carregarTudo();}
    private void carregarTudo(){try{ResumoDashboard r=controller.resumo();clientes.setText(String.valueOf(r.getClientes()));produtos.setText(String.valueOf(r.getProdutos()));estoque.setText(String.valueOf(r.getEstoqueBaixo()));abertos.setText(String.valueOf(r.getPedidosAbertos()));hoje.setText(moeda.format(r.getVendasHoje()));mes.setText(moeda.format(r.getVendasMes()));}catch(Exception e){erro(e);}carregarVendas();carregarEstoque();}
    private void carregarVendas(){try{LocalDate d1=LocalDate.parse(inicio.getText().trim()),d2=LocalDate.parse(fim.getText().trim());List<VendaPorDia> lista=controller.vendas(d1,d2);vendasModel.setRowCount(0);BigDecimal total=BigDecimal.ZERO;for(VendaPorDia v:lista){vendasModel.addRow(new Object[]{v.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),v.getPedidos(),moeda.format(v.getTotal())});total=total.add(v.getTotal());}totalPeriodo.setText("Total do período: "+moeda.format(total));}catch(Exception e){erro(e);}}
    private void exportarCsv(){
        if(vendasModel.getRowCount()==0){JOptionPane.showMessageDialog(this,"Não há vendas no período para exportar.");return;}
        JFileChooser chooser=new JFileChooser();chooser.setDialogTitle("Salvar relatório de vendas");chooser.setSelectedFile(new java.io.File("relatorio-vendas.csv"));
        if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;
        try(java.io.Writer writer=java.nio.file.Files.newBufferedWriter(chooser.getSelectedFile().toPath(),java.nio.charset.StandardCharsets.UTF_8)){
            writer.write("Data;Quantidade de pedidos;Faturamento\n");
            for(int i=0;i<vendasModel.getRowCount();i++)writer.write(vendasModel.getValueAt(i,0)+";"+vendasModel.getValueAt(i,1)+";"+vendasModel.getValueAt(i,2)+"\n");
            writer.write("; ;"+totalPeriodo.getText().replace("Total do período: ","")+"\n");
            JOptionPane.showMessageDialog(this,"Relatório exportado com sucesso.");
        }catch(Exception e){erro(e);}
    }
    private void carregarEstoque(){try{estoqueModel.setRowCount(0);List<String[]> lista=controller.estoqueBaixo();for(String[] p:lista)estoqueModel.addRow(new Object[]{p[0],p[1]});if(lista.isEmpty())estoqueModel.addRow(new Object[]{"Nenhum produto em nível crítico","—"});}catch(Exception e){erro(e);}}
    private void erro(Exception e){JOptionPane.showMessageDialog(this,e.getMessage()==null?"Não foi possível carregar os indicadores.":e.getMessage(),"Painel da cantina",JOptionPane.WARNING_MESSAGE);}
}
