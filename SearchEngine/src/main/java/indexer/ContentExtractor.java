
package indexer;

import ca.rmen.porterstemmer.PorterStemmer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import org.jsoup.Jsoup;

/**
 *
 * @author thebrownboy
 */


// this class will return the content of the page after removing the HTML tags from it 
// it will return the unique words from the file in a set of words .. 
/*Documentation of this class


    this class contains only one function this function is responsilbe for  get the content of a specific page ...  it takes the source of the page and return map (Uniquewords->number of occurences)

    1- firstly this function using the Jsoup library to get only the text of the page "the words of the page "
    2- the upcoming text will be the words seperated by space so it will split the words on the "space dilemeter "
    3- then it loop over the words array removing all the special char from the word
    4- then make all the char lower case  if the string is emtpy then continue don't insert it in the mape or equal to space 
    5- removing all the numbers from the words 
    6- then removing all the stopping words from the map  we are searching by the stopping in the map "cuz stopping is smaller than the page content"
*/

public class ContentExtractor {
    
    Map<String,Integer> getPageWords(String source){
      
        String text2 = Jsoup.parse(source).text(); // get the word of the content of the page 
        String [] words= text2.split(" ");        // split over the dilemeter  " " space ; 
        Map<String,Integer> elements = new HashMap<String,Integer>(); 
        for(String a : words){
            PorterStemmer stemmer = new PorterStemmer();
            String parts[]=a.split("[^a-zA-Z0-9]"); 
            //a=a.replaceAll("[^a-zA-Z0-9]","" ); // replace all the specail chars  replace (!(a-z)(A-Z)(0-9))
            for(int i = 0 ; i<parts.length; i++){
               parts[i]=parts[i].toLowerCase();
               parts[i]=stemmer.stemWord(parts[i]); 

            }
            //a=a.toLowerCase(); 
           // a=stemmer.stemWord(a); 
            for(String b : parts){
                if(b.isEmpty() || b.equals(" "))
                    continue;
                try{
                    int i = Integer.parseInt(b.trim());
                    // removing the numbers from the set 
                    // if the execption happens then it is not a number 
                    // if no exception happens so it is a number and will not be added in the set 
                }catch(NumberFormatException e ){
                    if(elements.containsKey(b)){
                        Integer j = elements.get(b); 
                        j++; 
                        elements.replace(b, j); 
                    }
                    else
                        elements.put(b, 1) ; 
                }
            }
        }
        
        /* remvoing the stopping words*/ 
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
            if(elements.containsKey(a)){
                elements.remove(a); 
            }
        }
        return  elements ; 
    }
    
   
    
}
