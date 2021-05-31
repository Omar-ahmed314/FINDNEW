package main.java.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class Spider implements Runnable
{
    private static final int maxDepth=4;
    private Thread thread;
    private String first_link;
    private ArrayList<String> visited=new ArrayList<String>();
    private int id;

    public Spider(String link, int id)
    {
        System.out.println("Spider created");
        first_link=link;
        this.id=id;
        thread=new Thread(this);    //use in main
        thread.start();   //use in main
    }
    @Override
    public void run()
    {
        crawl(1,first_link);
    }

    private void crawl(int depth,String url)
    {
        try {
            if (depth <= maxDepth) {
                Document doc = request(url);
                if (doc != null) {
                    for (Element link : doc.select("a[href]")) {
                        String nextLink = link.absUrl("href");
                        if (!visited.contains(nextLink)) {
                            crawl(++depth, nextLink);
                        }
                    }
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    private Document request(String url)
    {
        try
        {
            Connection con= Jsoup.connect(url);
            Document doc=con.get();
            if(con.response().statusCode()==200)
            {
                System.out.println("Spider ID:" + id + " Received webpage at "+ url);
                String title= doc.title();
                System.out.println(title);
                visited.add(url);
                return doc;
            }
            return null;
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            return null;
        }
    }

    public Thread getThread()
    {
        return thread;
    }
}
