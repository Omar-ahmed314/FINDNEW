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
    
    Map<String, Set<String>> getTagsContext(String source ){
        Document doc = Jsoup.parse(source);
        Elements ptags = doc.select("p");
        Map<String ,Set<String>> pmap= new HashMap<>(); 
        Set<String> unique_words =new HashSet<>(); 
        for(int i = 0 ; i<ptags.size();i++){
            String text = ptags.get(i).text();
            String[] words=text.split(" "); 
            for(String a : words){
                unique_words.add(a); 
            }
            // TO-DO we should remove the Stoping words and nubmers from words added to the set
            // we should genralize the concept over the whole tags  that can contain text 
            // 
             
        }
        pmap.put("p", unique_words);
        return pmap ;
    } 
    
}
