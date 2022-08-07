# TESTE TECNICO BANCO

## FAUSTO CRISTIANO

### Cenário de Negócio:
Todo dia útil por volta das 6 horas da manhã um colaborador da retaguarda do banco recebe e organiza as informações de contas para enviar ao Banco Central. Todas agencias e cooperativas enviam arquivos Excel à Retaguarda. Hoje o banco já possiu mais de 4 milhões de contas ativas.
Esse usuário da retaguarda exporta manualmente os dados em um arquivo CSV para ser enviada para a Receita Federal, antes as 10:00 da manhã na abertura das agências.

### Requisito:
Usar o "serviço da receita" (fake) para processamento automático do arquivo.

### Funcionalidade:
0. Criar uma aplicação SprintBoot standalone. Exemplo: java -jar SincronizacaoReceita <input-file>
1. Processa um arquivo CSV de entrada com o formato abaixo.
2. Envia a atualização para a Receita através do serviço (SIMULADO pela classe ReceitaService).
3. Retorna um arquivo com o resultado do envio da atualização da Receita. Mesmo formato adicionando o resultado em uma nova coluna.
   
### Formato CSV:
1. agencia;conta;saldo;status
2. 0101;12225-6;100,00;A
3. 0101;12226-8;3200,50;A
4. 3202;40011-1;-35,12;I
5. 3202;54001-2;0,00;P
6. 3202;00321-2;34500,00;B

#### Implementações:
Para implementação do projeto solicitado pelo cliente, foi utilizado o framework SptrigBoot, e variando desse framework
utilizei o API openCsv. Foi utilizado as classe CSVWriter e CSVReader, visando a facilidade ja implementadas por elas, e 
reduzindo a complexidade do código.
O código esta completamente detalhado, ficando de forma facil seu entendimento.
Foi efetuado testes um arquivo CSV de 100.000 linhas, e atendeu com sucesso o requisito do cliente.
No arquivo CSV de retorno, foi criado uma coluna extra, conforme solicitado, com o nome de "retorno", e com o 
os estado de  "Atualizado" e "Não Atualizado".

