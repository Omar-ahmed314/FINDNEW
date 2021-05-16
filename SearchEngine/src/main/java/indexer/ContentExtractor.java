/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import org.jsoup.Jsoup;

/**
 *
 * @author thebrownboy
 */


// this class will return the content of the page after removing the HTML tags from it 
// it will return the unique words from the file in a set of words .. 

public class ContentExtractor {
    
    Set<String> getPageWords(String source){
      
        String text2 = Jsoup.parse(source).text();
        String [] words= text2.split(" "); 
        Set<String> elements = new HashSet<String>(); 
        for(String a : words){
            try{
                int i = Integer.parseInt(a.trim()); 
                // removing the numbers from the set 
                // if the execption happens then it is not a number 
                // if no exception happens so it is a number and will not be added in the set 
            }catch(NumberFormatException e ){
                elements.add(a); 
            }
        }

       Set <String>stopingList = new HashSet<String>();  
       try{
             File myObj = new File("stop_words_english.txt");
              try (Scanner myReader = new Scanner(myObj)) {
                 while (myReader.hasNextLine()) {
                     String data = myReader.nextLine();
                     stopingList.add((data));
                 }      
              }
       } catch (FileNotFoundException e) {
             System.out.println("An error occurred.");
         }
       
        for(String a : stopingList){
            if(elements.contains(a)){
                elements.remove(a); 
            }
        }
        return  elements ; 
     }
}
