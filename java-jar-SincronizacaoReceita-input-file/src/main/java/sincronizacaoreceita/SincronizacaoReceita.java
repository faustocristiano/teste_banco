/*
Cenário de Negócio:
Todo dia útil por volta das 6 horas da manhã um colaborador da retaguarda do Sicredi recebe e organiza as informações de contas para enviar ao Banco Central. Todas agencias e cooperativas enviam arquivos Excel à Retaguarda. Hoje o Sicredi já possiu mais de 4 milhões de contas ativas.
Esse usuário da retaguarda exporta manualmente os dados em um arquivo CSV para ser enviada para a Receita Federal, antes as 10:00 da manhã na abertura das agências.

Requisito:
Usar o "serviço da receita" (fake) para processamento automático do arquivo.

Funcionalidade:
0. Criar uma aplicação SprintBoot standalone. Exemplo: java -jar SincronizacaoReceita <input-file>
1. Processa um arquivo CSV de entrada com o formato abaixo.
2. Envia a atualização para a Receita através do serviço (SIMULADO pela classe ReceitaService).
3. Retorna um arquivo com o resultado do envio da atualização da Receita. Mesmo formato adicionando o resultado em uma nova coluna.


Formato CSV:
agencia;conta;saldo;status
0101;12225-6;100,00;A
0101;12226-8;3200,50;A
3202;40011-1;-35,12;I
3202;54001-2;0,00;P
3202;00321-2;34500,00;B
...

*/
package sincronizacaoreceita;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;


@SpringBootApplication
public class SincronizacaoReceita{

    public static void main(String[] args) throws RuntimeException, InterruptedException,IOException, CsvValidationException {
        
        
            
            try{                             
                //String contendo o caminho relativo do arquivo CSV de origem
                final String CSVORIG = "java-jar-SincronizacaoReceita-input-file/entrada.csv";
           
                //Como o arquivo origem é separado por ";", e o API opencsv separa por default ","  criase esse parser com separator por ";"
                CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
                
                //Instancia o objeto csvReader da classe CSVReader, contina no framework SPRING
                CSVReader csvReader = new CSVReaderBuilder(new FileReader(new File(CSVORIG))).withCSVParser(parser).build();       
               
                //String contendo o caminho relativo do destino CSV de origem
                final String CSVDEST = "java-jar-SincronizacaoReceita-input-file/return.csv";
               
                //Instancia o objeto fileWriter da classe FileWriter, contina no API opencsv, com o caminho de destino a ser criado o CSV
                FileWriter fileWriter=new FileWriter(CSVDEST); 
                
                //Instancia o objeto csvWriter, delimitando o separdor por ";", visto que o API opencsv por default utiliza ","
                CSVWriter csvWriter = new CSVWriter(fileWriter, ';', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

                //Cria-se uma lista de lista de String, para armazenar a leitura do CSV e posterior escrita.
                List<String[]> exportCSV = new ArrayList<String[]>();

                //instancia o objeto da classe ReceitaService, fornecida no projeto
                ReceitaService receitaService = new ReceitaService();               
                
                //Cria-se o cabeçalho para o arquivo de escrita.
                String[] head ={"agencia","conta","saldo","status","retorno"};

                //adiciona o cabeçalho na lista, para posterior escrita
                exportCSV.add(head);                
            
                //lẽ a primeira linha do CSV origem, é o cabeçalho, para não entrar no loop while
                csvReader.readNext();

                //cria um vetor de String, onde serão armazenadas os dados lidos do CSV
                String[] coluna;      

                //loop até a ultima linha do arquivo, o API csvReader ira definir o tamnho do vetor de String,
                //nessse caso, será um vetor de 0-3, com 4 posições, 1 para cada coluna.
                while ((coluna = csvReader.readNext()) != null){

                    //copia as colunas do csvReader para uma String comum, pois a classe ReceitaService
                    //não aceita como parametero vetor
                    String agencia = coluna[0]; 
                    
                    //como a classe Receitaservice solciita a contasem o "-", ja foi suprimido com o replace               
                    String conta=coluna[1].replace("-", "");

                    //converte a String saldo para double, confome solciitado na classe Receitaservice
                    //como a conversão para double tem q usar "." e não "," conforme esta no CSV, ja foi feito com o replace.                           
                    double saldo = Double.valueOf(coluna[2].replace(",", "."));
                    String status=coluna[3]; 

                    //condição que se caso seje verdadeira no retorno da inserção da 
                    //classe  ReceitaService, o atualização na base é verdadeira, os dados estão OK.                  
                    if(receitaService.atualizarConta(agencia, conta, saldo,status)){
                        //vetor de String, para inserção na lista a ser escrita no CSV
                        String[] resultado ={coluna[0],coluna[1],coluna[2],coluna[3],"Atualizado"}; 

                        //adiociona o vetor na lista                                        
                        exportCSV.add(resultado);  

                    }else{
                        //vetor de String, para inserção na lista a ser escrita no CSV, e não atualizado
                        //caso o retorno da classe  ReceitaService seja false
                        String[] resultado ={coluna[0],coluna[1],coluna[2],coluna[3],"Não Atualizado"}; 
                        
                        //adiociona o vetor na lista 
                        exportCSV.add(resultado);                            
                    }                        
                        
                }                  
            //escreve de uma unica vez toda a lista no arquivo CSV, 
            //funcionalidade do API opencsv               
            csvWriter.writeAll(exportCSV); 

            //fecha a escrita
            csvWriter.close();

            //fecha o arquivo 
            fileWriter.close();   
            System.out.println("Arquivo gerado com sucesso");
            return; 

            //caso o aquivo origem não exista             
            }catch(FileNotFoundException e) {
                System.err.println("arquivo inválido");                
            }
        }
    }
       