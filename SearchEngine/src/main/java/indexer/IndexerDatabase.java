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



/*Documentation of the IndexerDatabase class one of the most important classes in the indexer .. 

    this Class contain a map of (word->setofDocumentinfo that contains this word)
    1- addDocument takes word and Documentinfo  and put it in the indexr map 
    2- get methods .. 
    3- function to check if a specific word exists in the database or not 


*/
public class IndexerDatabase {
    Map<String,Set<DocumentInfo>> indexerMap ; 

    public IndexerDatabase() {
        indexerMap=new HashMap<>(); 
    }
    
    
    public void addDocument(String word, DocumentInfo doc){
        if(indexerMap.containsKey(word)){
            // if the map contains this word just add the new document info 
            // if not initialize the set with that doc  
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
    boolean containsWord(String word){
        if(indexerMap.containsKey(word)){
            return true ; 
        }
        return  false ; 
    }
  
    
    /*TO-DO we should give every Document a specific id or identifer to be able to handle it */
    
}
