
package br.edu.utfpr.cantina.view;

import br.edu.utfpr.cantina.controller.ClienteController;
import br.edu.utfpr.cantina.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class TelaClientes extends JPanel {

    private final ClienteController controller;
    private final JTextField campoNome = new JTextField(24);
    private final JTextField campoEmail = new JTextField(24);

    private final DefaultTableModel modelo = new DefaultTableModel(
        new Object[]{"ID", "Nome", "E-mail"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tabela = new JTable(modelo);
    private int idSelecionado = 0;

    public TelaClientes(ClienteController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel("Cadastro de clientes");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(titulo, BorderLayout.WEST);
        add(topo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(
            BorderFactory.createTitledBorder("Dados do cliente")
        );

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0;
        g.gridy = 0;
        formulario.add(new JLabel("Nome:"), g);

        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(campoNome, g);

        g.gridx = 0;
        g.gridy = 1;
        g.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("E-mail:"), g);

        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(campoEmail, g);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton novo = new JButton("Limpar / Novo");
        JButton salvar = new JButton("Salvar");
        JButton excluir = new JButton("Excluir");
        JButton atualizar = new JButton("Atualizar lista");

        botoes.add(novo);
        botoes.add(salvar);
        botoes.add(excluir);
        botoes.add(atualizar);

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(formulario, BorderLayout.NORTH);
        centro.add(botoes, BorderLayout.SOUTH);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(25);

        centro.add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        novo.addActionListener(e -> limpar());
        salvar.addActionListener(e -> salvar());
        excluir.addActionListener(e -> excluir());
        atualizar.addActionListener(e -> carregar());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                preencherCampos();
            }
        });

        carregar();
    }

    private void carregar() {
        try {
            modelo.setRowCount(0);

            for (Cliente c : controller.listar()) {
                modelo.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNome(),
                    c.getEmail()
                });
            }

        } catch (SQLException e) {
            erroBanco(e);
        }
    }

    private void salvar() {
        try {
            controller.salvar(
                new Cliente(
                    idSelecionado,
                    campoNome.getText(),
                    campoEmail.getText()
                )
            );

            JOptionPane.showMessageDialog(
                this,
                idSelecionado == 0
                    ? "Cliente cadastrado com sucesso!"
                    : "Cliente atualizado com sucesso!"
            );

            limpar();
            carregar();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Verifique os dados",
                JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {
            erroBanco(e);
        }
    }

    private void excluir() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(
                this,
                "Selecione um cliente na tabela."
            );
            return;
        }

        if (JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente excluir este cliente?",
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION
        ) != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.excluir(idSelecionado);
            limpar();
            carregar();

            JOptionPane.showMessageDialog(this, "Cliente excluído.");

        } catch (SQLException e) {
            String msg = e.getErrorCode() == 1451
                ? "Este cliente possui pedidos vinculados e não pode ser excluído."
                : e.getMessage();

            JOptionPane.showMessageDialog(
                this,
                msg,
                "Não foi possível excluir",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void preencherCampos() {
        int r = tabela.getSelectedRow();

        idSelecionado = (int) modelo.getValueAt(r, 0);
        campoNome.setText((String) modelo.getValueAt(r, 1));
        campoEmail.setText((String) modelo.getValueAt(r, 2));
    }

    private void limpar() {
        idSelecionado = 0;
        campoNome.setText("");
        campoEmail.setText("");
        tabela.clearSelection();
        campoNome.requestFocus();
    }

    private void erroBanco(SQLException e) {
        JOptionPane.showMessageDialog(
            this,
            "Não foi possível acessar o banco de dados. "
            + "Confira se o MySQL está iniciado e se as configurações estão corretas.\n"
            + "Detalhe: " + e.getMessage(),
            "Erro de banco de dados",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
