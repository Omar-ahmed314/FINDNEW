/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author thebrownboy
 */

// this class will return a map , keys will be the HTML tags names values will be the words of the text
// of the tags 
public class TagsTextExtractor {
    
    Map<String, Map<String,Integer>> getTagsContext(String source ){
        Document doc = Jsoup.parse(source);
        Elements ptags = doc.select("p");
        Map<String ,Map<String, Integer>> pmap= new HashMap<>(); 
        Map<String,Integer> unique_words =new HashMap<>(); 
        for(int i = 0 ; i<ptags.size();i++){
            String text = ptags.get(i).text();
            String[] words=text.split(" "); 
            for(String a : words){
                if(unique_words.containsKey(a)){
                    Integer j = unique_words.get(a); 
                    j++; 
                    unique_words.replace(a, j); 
                }
                else
                    unique_words.put(a, 1); 
            }
            // TO-DO we should remove the Stoping words and nubmers from words added to the set
            // we should genralize the concept over the whole tags  that can contain text 
            // we should add number of occurneces per word in per tag name 
             
        }
        pmap.put("p", unique_words);
        return pmap ;
    } 
    
}