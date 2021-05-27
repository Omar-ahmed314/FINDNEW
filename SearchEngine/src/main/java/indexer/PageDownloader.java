
/**
 *
 * @author thebrownboy
 */
package indexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author thebrownboy
 */


/*documentation of PageDownloader class 
    this class is very simple it has just one function which downlad the source of the page it just takes the URL of the page and return the html source of it 
 
 */

// this Class will help you  to download any web page source , you can use the "downloadpage function " 
// to get the source of the page as text ; 
public class PageDownloader {

    public PageDownloader()  {
        
    }
    String downloadPage(String URL) throws MalformedURLException, IOException{
        URL page = new URL(URL);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(page.openStream()));
        String inputLine, text=""; 
        while ((inputLine = in.readLine()) != null)
        {   text+=inputLine;
            text+="\n"; 
        }
        return text ; 
    }
    
}
