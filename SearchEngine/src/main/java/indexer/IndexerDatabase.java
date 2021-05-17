/**
 *
 * @author thebrownboy
 */
package indexer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author thebrownboy
 */
public class IndexerDatabase {
    Map<String,Set<DocumentInfo>> indexerMap ; 

    public IndexerDatabase() {
        indexerMap=new HashMap<>(); 
    }
    
    
    public void addDocument(String word, DocumentInfo doc){
        if(indexerMap.containsKey(word)){
            Set<DocumentInfo> wordSet= indexerMap.get(word); 
            wordSet.add(doc);
            indexerMap.replace(word, wordSet); 
        }
        else{
            Set<DocumentInfo> docSet= new HashSet<>();
            docSet.add(doc); 
            indexerMap.put(word, docSet); 
            
        }
        
    }
    public Set<DocumentInfo> getWordSet(String word){
        if(indexerMap.containsKey(word)){
            return indexerMap.get(word); 
        }
        return null ; 
    }
    
    /*TO-DO we should give every Document a specific id or identifer to be able to handle it */
    
}
