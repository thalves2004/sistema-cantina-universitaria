package br.edu.utfpr.cantina.view;

import br.edu.utfpr.cantina.controller.*;
import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {
    private TelaDashboard dashboard;
    public TelaPrincipal(ClienteController clienteController, ProdutoController produtoController,
                         PedidoController pedidoController, RelatorioController relatorioController) {
        setTitle("Cantina Universitária | Gestão inteligente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180,780);setMinimumSize(new Dimension(980,680));setLocationRelativeTo(null);
        JTabbedPane abas=new JTabbedPane();abas.setFont(new Font("Segoe UI",Font.BOLD,13));
        dashboard=new TelaDashboard(relatorioController);
        abas.addTab("Painel",dashboard);
        abas.addTab("Clientes",new TelaClientes(clienteController));
        abas.addTab("Produtos e estoque",new TelaProdutos(produtoController));
        abas.addTab("Pedidos",new TelaPedidos(clienteController,produtoController,pedidoController));
        abas.addChangeListener(e->{if(abas.getSelectedIndex()==0)dashboard.atualizarDados();});
        setContentPane(abas);
    }
    public void atualizarPainel(){
        if (dashboard != null) {
            dashboard.atualizarDados();
        }
    }
    public static void aplicarTema(){
        try{for(UIManager.LookAndFeelInfo info:UIManager.getInstalledLookAndFeels())if("Nimbus".equals(info.getName())){UIManager.setLookAndFeel(info.getClassName());break;}}catch(Exception ignored){}
        UIManager.put("control",new Color(244,247,251));UIManager.put("nimbusBase",new Color(31,78,121));
        UIManager.put("nimbusBlueGrey",new Color(218,227,237));UIManager.put("text",new Color(45,55,72));
        UIManager.put("defaultFont",new Font("Segoe UI",Font.PLAIN,13));
    }
}
