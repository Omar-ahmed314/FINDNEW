/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


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
    public static void main(String args[]){
        IndexerManager manager = new IndexerManager(); 
        String URL = manager.readURL(); 
    
    
    }
    
}

