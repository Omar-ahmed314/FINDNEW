package main.java.crawler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class dbSpider implements Runnable
{
    //private String start_url="https://www.bbc.com/";
    final Set<String> visited=new HashSet<String>();
    static final Queue<String> notVisited=new LinkedList<String>();
    static final Database db=new Database();
    int MAX_DOCS=100;
    Integer cur_num_docs=0;



    public static void setNotVisited(String link)
    {
        notVisited.add(link);
    }

    public dbSpider()
    {

    }


    public void crawl()
    {
        while(cur_num_docs<=MAX_DOCS)
        {

            int numRows= db.getCount("not_visited");
            if(numRows<=0) break;

            String link=null;

            synchronized (db)
            {
                link= db.dbDeque("not_visited");

                /*if(visited.contains(link)) {
                    continue;
                }*/

            }
            if(db.exists("visited",link)) continue;

            String html = getHTML(link);
            if(html==null) continue;
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("a");
            for (Element e : elements)
            {
                String href = e.attr("href");
                href = postProLink(href, link);
                //if (!visited.contains(href) && !notVisited.contains(href)) {
                if(!db.exists("visited",href)&&!db.exists("not_visited","href"))
                {
                    db.dbEnqueue("not_visited",href);
                }

            }
            synchronized (cur_num_docs) {
                cur_num_docs++;
            }
            System.out.println(Thread.currentThread()+" : "+ link + " Num docs= "+ cur_num_docs );
            //todo out document of link
            db.dbEnqueue("visited",link);

        }
    }

    private String getHTML(String url)
    {
        URL u;
        try
        {
            u=new URL(url);
            URLConnection con=u.openConnection();
            con.setRequestProperty("User.Agent","Bot1.0");
            con.setRequestProperty("Accept.Charset","UTF-8");
            InputStream is=con.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader((is)));
            String line,html="";
            while((line=reader.readLine())!=null)
            {
                html+= line+"\n";

            }
            html=html.trim();
            return html;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private String postProLink(String link, String base)
    {
        try
        {
            URL url=new URL(base);
            if(link!=null)
                if (link.startsWith("./"))
                {
                    link=link.substring(2,link.length());
                    link= url.getProtocol()+"://"+ url.getAuthority()+ removeName(url.getPath())+link;
                }
                else if(link.startsWith("#"))   //better remove this
                {

                    link=base+link;
                    link=link.substring(0,link.length()-1);
                }
                else if(link.startsWith("/"))
                {
                    link=link.substring(1,link.length());
                    link= url.getProtocol()+"://"+ url.getAuthority()+ removeName(url.getPath())+link;
                }
                else if(link.startsWith("javascript:"))
                {
                    link=null;
                }
                else if(link.startsWith("../"))
                {
                    link= url.getProtocol()+"://"+ url.getAuthority()+ removeName(url.getPath())+link;
                }
            return link;
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            return null;
        }
    }

    private String removeName(String path)
    {
        int pos=path.lastIndexOf("/");
        return pos<=-1?path:path.substring(0,pos+1);
    }
    @Override
    public void run()
    {
        crawl();
    }
}
