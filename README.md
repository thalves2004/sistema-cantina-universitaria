# Sistema de Gerenciamento de Pedidos — Cantina Universitária

Projeto acadêmico em Java 21, Swing, Maven, JDBC e MySQL, organizado em camadas de apresentação, controle, serviço, modelo e persistência.

## Funcionalidades implementadas nesta versão
- Cadastro de clientes: listar, cadastrar, editar e excluir, com validação de nome/e-mail.
- Cadastro de produtos: listar, cadastrar, editar e excluir, com preço e quantidade em estoque.
- Persistência MySQL por JDBC e consultas parametrizadas (`PreparedStatement`).
- Registro de pedidos vinculados a clientes, com inclusão de vários produtos e quantidades.
- Cálculo do total do pedido com base no preço registrado de cada item.
- Consulta de pedidos e atualização de status (PENDENTE, EM PREPARO, PRONTO, ENTREGUE e CANCELADO).
- Histórico de pedidos por cliente, com visualização dos itens, quantidades, preços históricos, total, data e status.
- Baixa de estoque transacional ao registrar pedido; cancelamento restaura automaticamente as quantidades.
- Dashboard com indicadores de clientes, produtos, pedidos em aberto, vendas do dia e vendas do mês.
- Relatórios de faturamento por intervalo de datas, com totalização e exportação para CSV. Pedidos cancelados não entram no faturamento.
- Alertas visuais de estoque baixo: produtos com 5 unidades ou menos são destacados na tela de produtos e listados no painel.
- Interface Swing reorganizada com tema Nimbus, cartões de indicadores e tabelas aprimoradas.
- Validações de domínio nas classes de serviço.
- Inversão de dependência (DIP) aplicada aos serviços de clientes, produtos, pedidos e relatórios por meio dos contratos `ClienteRepositoryPort`, `ProdutoRepositoryPort`, `PedidoRepositoryPort` e `RelatorioRepositoryPort`; as implementações JDBC ficam separadas.

## Situação dos requisitos do projeto
| Requisito | Situação no código entregue |
|---|---|
| RF01 — cadastrar clientes | Implementado |
| RF02 — cadastrar/gerenciar produtos | Implementado |
| RF03 — consultar produtos e estoque | Implementado na listagem de produtos |
| RF04 — registrar pedidos vinculados a cliente | Implementado |
| RF05 — adicionar produtos/quantidades ao pedido | Implementado |
| RF06 — calcular total do pedido | Implementado no modelo e na tela de pedidos |
| RF07 — consultar pedidos | Implementado |
| RF08 — atualizar status do pedido | Implementado |
| RF09 — controlar estoque a partir dos pedidos | Implementado com transação e restauração no cancelamento |
| RF10 — persistência de clientes, produtos e pedidos | Implementado via MySQL/JDBC |

**Observação:** o fluxo principal dos requisitos RF01–RF10 e as funcionalidades complementares descritas acima estão implementados no código. A compilação Java foi verificada; a integração com o MySQL deve ser validada no computador de execução usando os testes abaixo.

## Requisitos
- JDK 21.
- Maven 3.9 ou compatível.
- MySQL 8 ou compatível.
- IDE Java (VS Code com Extension Pack for Java, IntelliJ ou Eclipse).

## Preparar o banco de dados
1. Abra `database/schema.sql` no MySQL Workbench.
2. Execute o script.
3. Confirme com `USE cantina_universitaria; SHOW TABLES;`.

## Configurar conexão
A aplicação lê variáveis de ambiente:
- `CANTINA_DB_URL` (opcional; existe URL padrão para `localhost:3306/cantina_universitaria`).
- `CANTINA_DB_USER` (padrão: `root`).
- `CANTINA_DB_PASSWORD` (padrão vazio).

PowerShell:
```powershell
$env:CANTINA_DB_USER = "root"
$env:CANTINA_DB_PASSWORD = "SUA_SENHA_DO_MYSQL"
```
Não coloque senhas no código ou no GitHub.

## Compilar e executar
No terminal, dentro da pasta que contém `pom.xml`:
```powershell
mvn clean compile
mvn exec:java
```
Na primeira execução, o Maven precisa baixar o MySQL Connector/J.

## Testes automatizados
Os testes unitários dos serviços utilizam JUnit 5 e repositórios simulados (fakes), sem exigir conexão com MySQL:

```powershell
mvn test
```

A suíte em `src/test/java/br/edu/utfpr/cantina/service/ServicosTest.java` cobre normalização e validação de cliente, validação de produto (preço e estoque), validação e encaminhamento de pedido e rejeição de período inválido em relatório. Esses testes isolam a camada de serviço; não substituem testes de integração com um MySQL real.

## Testes manuais recomendados
1. Cadastrar cliente com nome e e-mail válidos.
2. Tentar cadastrar e-mail repetido e conferir a mensagem do banco.
3. Editar e excluir cliente sem pedidos associados.
4. Cadastrar produto com preço decimal e estoque inteiro.
5. Testar preço inválido, estoque negativo, edição e exclusão de produto.
6. Registrar um pedido com mais de um produto e conferir o total.
7. Conferir a redução do estoque após registrar o pedido.
8. Cancelar um pedido e conferir a restauração do estoque.
9. Reiniciar a aplicação e confirmar a persistência dos registros.
10. Abrir o Painel e conferir os indicadores de clientes, produtos, pedidos em aberto e faturamento.
11. Gerar um relatório de vendas usando datas inicial e final e exportar o resultado para CSV.
12. Consultar o histórico de um cliente e selecionar um pedido para visualizar seus itens.
13. Cadastrar um produto com estoque igual ou inferior a 5 e conferir o alerta no Painel e na tela de produtos.

## Estrutura principal
```text
src/main/java/br/edu/utfpr/cantina/
  App.java
  controller/       # controladores Swing
  database/         # conexão JDBC
  model/            # Cliente, Produto, Pedido e ItemPedido
  repository/       # contratos (*Port) e implementações JDBC
  service/          # validações e regras de negócio
  view/             # telas Swing
```
