/**
 *
 * @author thebrownboy
 */
package indexer;


import java.util.HashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


// this class will return a map , keys will be the HTML tags names values will be the words of the text
// of the tags 
public class TagsTextExtractor {
    Map<String ,Map<String, Integer>> pmap;

    public TagsTextExtractor() {
        pmap= new HashMap<>();
    }
    
    
    private void getTagtext(String source, String tagName ){
        Document doc = Jsoup.parse(source);
        Elements ptags = doc.select(tagName);
         
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
        pmap.put(tagName, unique_words);
        
    } 
     public Map<String ,Map<String, Integer>> getAllTagsText(String source ){
         for(int i = 1 ; i<=6; i++){
             getTagtext(source, "h"+i);
         }
         getTagtext(source, "a");
         getTagtext(source, "p");
         getTagtext(source, "li");
         getTagtext(source, "title");
         getTagtext(source, "meta");
         getTagtext(source, "div");
         getTagtext(source, "ol");
         getTagtext(source, "dt");
         getTagtext(source, "dl");
         getTagtext(source, "label");
         getTagtext(source, "button");
//         getTagtext(source, "style");
         
         
         
         return pmap ; 
     }
    
}
