/**
 *
 * @author thebrownboy
 */
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
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/* the Documentation of TagswordsExtractor 

    this class is very simple it just extracts the content of each tag , it gets the word that this tag contains 
    it has two function one main function that take  source of the page and the tag name 
    it has a Map called tagsMap (tagname->map(word->number of Occurences))
    it get the content of the tag and get the unique words  and the number of occurencess 
    then it removes the stopping words from the tag content 
*/



// this class will return a map , keys will be the HTML tags names values will be the words of the text
// of the tags 
public class TagsTextExtractor {
    Map<String ,Map<String, Integer>> tagsMap;

    public TagsTextExtractor() {
        tagsMap= new HashMap<>();
    }
    
    
    private void getTagtext(String source, String tagName ){
        Document doc = Jsoup.parse(source); 
        Elements ptags = doc.select(tagName); // select specific tag 
         
        Map<String,Integer> unique_words =new HashMap<>(); 
        for(int i = 0 ; i<ptags.size();i++){
            String text = ptags.get(i).text();
            String[] words=text.split(" "); 
            for(String a : words){
                a=a.replaceAll("[^a-zA-Z0-9]", ""); 
                a=a.toLowerCase(); 
                PorterStemmer stemmer = new PorterStemmer();
                a=stemmer.stemWord(a); 
                //TO-DO for example if you have something like that "python.org" 
                // if you search by the word python you will not find it because the word pythonorg which will be put in the indexer not python 
                if(unique_words.containsKey(a)){
                    Integer j = unique_words.get(a); 
                    j++; 
                    unique_words.replace(a, j); 
                }
                else
                    unique_words.put(a, 1); 
            }
            // TO-DO we should remove the Stoping words and nubmers from words added to the set done 
            // we should genralize the concept over the whole tags  that can contain text  done 
            // we should add number of occurneces per word in per tag name  done 
            
        Set <String>stopingList = new HashSet<>();  
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
             if(unique_words.containsKey(a)){
                 unique_words.remove(a); 
             }
         }

         }
         tagsMap.put(tagName, unique_words);
        
    } 
     public Map<String ,Map<String, Integer>> getAllTagsText(String source ){
         getTagtext(source, "title");
         for(int i = 1 ; i<=6; i++){
             getTagtext(source, "h"+i);
         }
         getTagtext(source, "p");
         getTagtext(source, "a");
         
         getTagtext(source, "li");
         
         
         getTagtext(source, "ol");
         getTagtext(source, "dt");
         getTagtext(source, "dl");
         getTagtext(source, "label");
         getTagtext(source, "button");
         
         
         return tagsMap ; 
     }
    
}
