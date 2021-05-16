/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author thebrownboy
 */
public class IndexerManager {
    private IndexerDatabase mainDatabase ; 

    public IndexerManager() {
        mainDatabase= new IndexerDatabase(); 
    }
    
     
    public String readURL(){
        String URL="" ; 
        File myObj = new File("ClawerURLs.txt");
        try (Scanner myReader = new Scanner(myObj)) {
            if (myReader.hasNextLine()) {
                 URL= myReader.nextLine();
            }
        }   catch (FileNotFoundException ex) {
                System.out.println("Error while Reading the file");
        }  
        
        return URL ; 
    }  
    public Map<String, Integer> getPageTextContent(String  source){
        ContentExtractor extractor=new ContentExtractor(); 
        return extractor.getPageWords(source);
    }
    
    public String getPageSource(String URL) throws MalformedURLException, IOException{
        URL page = new URL(URL);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(page.openStream()));

        String inputLine, source=""; 
        while ((inputLine = in.readLine()) != null)
        {   source+=inputLine;
            source+="\n"; 
        }
        return source ; 
    }
    public static void main(String args[]){
        
            IndexerManager manager = new IndexerManager();
            String URL = manager.readURL();
            try {
                Map<String,Integer> wordOccurences= manager.getPageTextContent(manager.getPageSource(URL));
                System.out.println(wordOccurences);
             } 
             catch (IOException ex) {
                   System.out.println("Error While Reading the source of the Page");
             }
    
    
    }
    
}

