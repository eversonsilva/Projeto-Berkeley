/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Berkeley;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 114751
 */
public class Sincronismo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        if (args[0].equals("-m")) {

            //Passando os parametros do vetor args para variaveis;
            String d = args[1];

            //Criação e inicialização do arquivo de log pertinente à classe em questão
            new ArquivoLogMaster("Master");

            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket clientSocket = new DatagramSocket();
            DatagramPacket sendPacket;
            InetAddress IPAddress;

            //Definição da lista de conexões a serem realizadas.            
            //String[] IPsSlaves = {"172.16.2.96", "172.16.2.94"};
            //String[] IPsSlaves = {"localhost", "172.16.16.39", "172.16.16.36"};
            //
            String[] IPsSlaves = {"localhost"};
            //String[] IPsSlaves = {"172.16.2.94"};
            //String[] IPsSlaves = {"172.16.2.96"};
            //String[] IPsSlaves = {"172.16.19.77","172.16.19.89", "172.16.19.81", "172.16.19.91"};

            int porta = 9998;

            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];

            String sentence = "";

            List tempos = new ArrayList<>();

            while (true) {

                int tempoMaster = 18000000;
                tempoMaster += (int) (Math.random() * 60000 + 1000);
                System.out.println("Horário atual do Master: " + tempoMaster + " em milissegundos");
                new ArquivoLogMaster("Horário atual do Master em milissegundos: " + tempoMaster);

                //Formatando o horário atual do Master
                int segundos = (tempoMaster / 1000) % 60;      // se não precisar de segundos, basta remover esta linha.
                int minutos = (tempoMaster / 60000) % 60;     // 60000   = 60 * 1000
                int horas = tempoMaster / 3600000;            // 3600000 = 60 * 60 * 1000

                String formatado = String.format("%3d:%02d:%01d", horas, minutos, segundos);

                System.out.println("Horário atual do Master" + formatado);
                new ArquivoLogMaster("Horário atual do Master formatado: "+ formatado);
                System.out.println("");

                //Para cada IP existente no array de IPs Slaves, executa...
                for (int i = 0; i < IPsSlaves.length; i++) {

                    IPAddress = InetAddress.getByName(IPsSlaves[i]);

                    //Registro de log
                    new ArquivoLogMaster("Iniciando comunicação com: " + IPAddress);

                    sentence = "getHora";
                    new ArquivoLogMaster("Enviando sentença: " + sentence);

                    sendData = sentence.getBytes();
                    sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, porta);
                    clientSocket.send(sendPacket);
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                    String modifiedSentence = new String(receivePacket.getData());

                    tempos.add(Integer.valueOf(modifiedSentence.trim()));

                    System.out.println("Sincronismo - DO SERVIDOR " + IPsSlaves[i]);
                    System.out.println("");

                    System.out.println("Os tempos em milissegundos dos Slaves: " + tempos);
                    System.out.println("");

                }

                String[][] m = new String[4][IPsSlaves.length];
                int cont = 0;
                int novoRelogio;
                int soma = 0;

                for (int i = 0; i < IPsSlaves.length; i++) {
                    m[0][i] = IPsSlaves[i];
                }

                for (int i = 0; i < IPsSlaves.length; i++) {
                    m[1][i] = tempos.get(i).toString();
                }

                for (int i = 0; i < IPsSlaves.length; i++) {
                    m[2][i] = Integer.toString(tempoMaster - Integer.parseInt(m[1][i]));
                    if (Integer.parseInt(m[2][i]) <= Integer.parseInt(d)) {
                        m[3][i] = "Menor";
                    } else {
                        m[3][i] = "Maior";
                    }
                }

                for (int i = 0; i < IPsSlaves.length; i++) {
                    if (m[3][i].equals("Menor")) {
                        soma += Integer.parseInt(m[1][i]);
                        cont++;
                    }
                }

                novoRelogio = (tempoMaster + soma) / (cont + 1);
                

                System.out.println("Antes da Atualização :" + tempoMaster + " - " + formatado);
                new ArquivoLogMaster("Antes da Atualização :" + tempoMaster + " - " + formatado);

                //Atualizando o horario do Master
                tempoMaster = novoRelogio;
                new ArquivoLogMaster("Novo horário do Master: " + novoRelogio + " em milissegundos");

                //Formatando o horario do Master atualizado
                int segundos1 = (tempoMaster / 1000) % 60;      // se não precisar de segundos, basta remover esta linha.
                int minutos1 = (tempoMaster / 60000) % 60;     // 60000   = 60 * 1000
                int horas1 = tempoMaster / 3600000;            // 3600000 = 60 * 60 * 1000

                String formatado1 = String.format("%3d:%02d:%01d", horas1, minutos1, segundos1);

                // Imprime o tempo atualizado
                System.out.println("Depois da Atualização: " + tempoMaster + " - " + formatado1);
                System.out.println("");

                new ArquivoLogMaster("Depois da Atualização formatado: " + formatado1);

                System.out.println("Relogio correto - Master: " + formatado1);
                new ArquivoLogMaster("Relogio correto - Master: " + formatado1);
                
                for (int i = 0; i < IPsSlaves.length; i++) {
                    int correcao = novoRelogio - Integer.parseInt(m[1][i]);
                    
                    new ArquivoLogMaster("Enviando para o servidor " + IPsSlaves[i] + " correção de " + correcao + " milisegundos");

                    //define a sentence a ser enviada com o comando necessário e a correcao da hora.
                    sentence = "corrigeHora:" + correcao + ":" + tempoMaster;
                    sendData = sentence.getBytes();
                    IPAddress = InetAddress.getByName(IPsSlaves[i]);
                    sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, porta);
                    clientSocket.send(sendPacket);
                }
                
                System.out.println("=========================================================================================");
                new ArquivoLogMaster("=========================================================================================");


                //limpa o Arraylist para uma próxima varredura.
                tempos.clear();
                Thread.sleep(15000);
            }
        } else if (args[0].equals("-s")) {

            //Implementação de Threads para que múltiplos acessos possam ocorrer.
            new Thread() {
                @Override
                public void run() {

                    System.out.println("Esperando estabelecer comunicação com o Master");
                    System.out.println("");

                    int tempoSlave = 18000000;
                    tempoSlave += (int) (Math.random() * 60000 + 1000);

//                  Criação e inicialização do arquivo de log pertinente à classe em questão
                    new ArquivoLogSlaver("Slave");

                    DatagramSocket serverSocket = null;

                    try {
                        serverSocket = new DatagramSocket(9998);
                    } catch (SocketException ex) {
                        Logger.getLogger(Sincronismo.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    byte[] receiveData = new byte[1024];
                    byte[] sendData = new byte[1024];
                    String sentence;

                    while (true) {

                        //Recebimento da conexão
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        try {
                            serverSocket.receive(receivePacket);
                        } catch (IOException ex) {
                            Logger.getLogger(Sincronismo.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        sentence = new String(receivePacket.getData()).trim();
                        receiveData = new byte[1024];
                        InetAddress IPAddress = receivePacket.getAddress();
                        int port = receivePacket.getPort();

                        long timeMillis = 0;

                        //Ação a ser tomada através do recebimento do comando
                        if (sentence.contains("getHora")) {

                            new ArquivoLogSlaver("Comunicação com: " + receivePacket.getAddress());
                            new ArquivoLogSlaver("Comando recebido: " + sentence);
                            sentence = Integer.toString(tempoSlave);
                            timeMillis = Integer.parseInt(sentence);

                            sendData = sentence.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                            try {
                                serverSocket.send(sendPacket);
                            } catch (IOException ex) {
                                Logger.getLogger(Sincronismo.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        } else {

                            //Formatando o horário atual do Slave
                            int segundos = (tempoSlave / 1000) % 60;      // se não precisar de segundos, basta remover esta linha.
                            int minutos = (tempoSlave / 60000) % 60;     // 60000   = 60 * 1000
                            int horas = tempoSlave / 3600000;            // 3600000 = 60 * 60 * 1000

                            String formatado = String.format("%3d:%02d:%01d", horas, minutos, segundos);

                            System.out.println("Horário atual do Slave: " + tempoSlave + " em milissegundos");
                            new ArquivoLogSlaver("Horário atual do Slave: " + tempoSlave + " em milissegundos");
                            
                            System.out.println("Horário atual do Slave formatado: " + formatado);
                            new ArquivoLogSlaver("Horário atual do Slave formatado: " + formatado);
                            System.out.println("");

                            new ArquivoLogSlaver("Comando recebido: " + sentence);
                            String comando = sentence;
                            String array[] = new String[3];
                            array = comando.split(":");

                            int correcao = Integer.parseInt(array[1]);

                            //Atualização da Hora do Slave
                            new ArquivoLogSlaver("O Slave deve corrigir seu horário em: " + correcao);

                            // Obtem o tempo em Milissegundos
                            System.out.println("Antes da Atualização :" + tempoSlave + " - " + formatado);

                            new ArquivoLogSlaver("Antes da Atualizacao: " + tempoSlave + " - " + formatado);

                            // Imprime o relógio atualizado.
                            tempoSlave += correcao;

                            //Formatando o horario do Slave atualizado
                            int segundos2 = (tempoSlave / 1000) % 60;      // se não precisar de segundos, basta remover esta linha.
                            int minutos2 = (tempoSlave / 60000) % 60;     // 60000   = 60 * 1000
                            int horas2 = tempoSlave / 3600000;            // 3600000 = 60 * 60 * 1000

                            String formatado2 = String.format("%3d:%02d:%01d", horas2, minutos2, segundos2);

                            System.out.println("Depois da Atualização: " + tempoSlave + " - " + formatado2);
                            new ArquivoLogSlaver("Depois da Atualização: " + tempoSlave + " - " + formatado2);
                            System.out.println("");

                            System.out.println("Relogio correto - Slave: " + formatado2);
                            new ArquivoLogSlaver("Relogio correto - Slave: " + formatado2);
                            
                            System.out.println("===============================================================");
                            new ArquivoLogSlaver("===============================================================");

                            tempoSlave = 18000000 + (int) (Math.random() * 60000 + 1000);
                        }
                    }
                }
            }.start();
        } else {
            System.out.println("Por favor digitar uma letra válida, com um '-' antes da letra");
        }
    }
}
