/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Berkeley;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Everson
 */
public class ArquivoLogSlaver {

    File arq;
    FileReader fr;
    BufferedReader bf;
    FileWriter fw;
    BufferedWriter bw;
    
    public ArquivoLogSlaver(String comando) {
        escreverLog(comando);
    }
   
    
    private void escreverLog(String comando) {
        try {
            
            arq = new File("C:\\temp\\SlaverLog.txt");
            fr = new FileReader(arq);
            bf = new BufferedReader(fr);
            
            List linha = new ArrayList();
            
            while(bf.ready()){
                linha.add(bf.readLine());
            }
            
            fw = new FileWriter(arq);
            bw = new BufferedWriter(fw);
            
            for (int i = 0; i < linha.size(); i++) {
                bw.write(linha.get(i).toString());
                bw.newLine();
            }
            
            bw.write(comando);
            
            bf.close();
            bw.close();
            
        } catch (FileNotFoundException ex) {
            try {
                arq.createNewFile();
                escreverLog(comando);
            } catch (IOException ex1) {
                System.exit(0);
            }
        } catch (IOException ex){
            System.exit(0);
        }
    }
    
}
