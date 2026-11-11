
package br.edu.utfpr.cantina;

import br.edu.utfpr.cantina.controller.ClienteController;
import br.edu.utfpr.cantina.database.Conexao;
import br.edu.utfpr.cantina.repository.JdbcClienteRepository;
import br.edu.utfpr.cantina.repository.JdbcProdutoRepository;
import br.edu.utfpr.cantina.controller.ProdutoController;
import br.edu.utfpr.cantina.controller.PedidoController;
import br.edu.utfpr.cantina.repository.JdbcPedidoRepository;
import br.edu.utfpr.cantina.repository.JdbcRelatorioRepository;
import br.edu.utfpr.cantina.service.RelatorioService;
import br.edu.utfpr.cantina.controller.RelatorioController;
import br.edu.utfpr.cantina.service.PedidoService;
import br.edu.utfpr.cantina.service.ProdutoService;
import br.edu.utfpr.cantina.service.ClienteService;
import br.edu.utfpr.cantina.view.TelaPrincipal;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        TelaPrincipal.aplicarTema();
        SwingUtilities.invokeLater(() -> {
            ClienteController controller = new ClienteController(
                new ClienteService(new JdbcClienteRepository())
            );

            ProdutoController produtoController = new ProdutoController(new ProdutoService(new JdbcProdutoRepository()));
            PedidoController pedidoController = new PedidoController(new PedidoService(new JdbcPedidoRepository()));
            RelatorioController relatorioController = new RelatorioController(new RelatorioService(new JdbcRelatorioRepository()));
            TelaPrincipal tela = new TelaPrincipal(controller, produtoController, pedidoController, relatorioController);

            if (!Conexao.testarConexao()) {
                JOptionPane.showMessageDialog(
                    tela,
                    "Não foi possível conectar ao MySQL.\n"
                    + "Verifique se o servidor está iniciado, "
                    + "se executou database/schema.sql e "
                    + "se configurou CANTINA_DB_USER e CANTINA_DB_PASSWORD.\n\n"
                    + "A tela abrirá, mas o cadastro só funcionará "
                    + "após a conexão.",
                    "Banco de dados não conectado",
                    JOptionPane.WARNING_MESSAGE
                );
            } else {
                tela.atualizarPainel();
            }

            tela.setVisible(true);
        });
    }
}
