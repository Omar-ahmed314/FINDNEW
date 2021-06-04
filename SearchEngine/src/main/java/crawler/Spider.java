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

public class Spider implements Runnable
{
    //private String start_url="https://www.bbc.com/";
    final Set<String> visited=new HashSet<String>();
    static final Queue<String> notVisited=new LinkedList<String>();
    int MAX_DOCS=100;
     Integer cur_num_docs=0;
    static Connection connection=null;
    static Statement statement= null;
    static ResultSet resultset=null;



    public static void setNotVisited(String link)
    {
        notVisited.add(link);
    }

    public Spider()
    {
        try {
            connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/crawler_db","root","data6OO6suck5");
            statement = connection.createStatement();

        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

    }


    public void crawl()
    {
        //String html=getHTML(this.start_url);

        //while(!notVisited.isEmpty()&&cur_num_docs<=MAX_DOCS)
        while(/*numRows!=0&&*/cur_num_docs<=MAX_DOCS)
        {

            int numRows=-1;
            try {
                resultset=statement.executeQuery("SELECT COUNT(*) FROM not_visited");
                numRows=Integer.parseInt(resultset.getString("count(*)"));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if(numRows<=0) break;

            String link = null;

            synchronized (notVisited)
            {
                //link = notVisited.poll();
                try {
                    resultset = statement.executeQuery("SELECT COUNT(*) FROM not_visited LIMIT 1");

                    link = resultset.getString("link");
                }
                catch(Exception e){e.printStackTrace();}

                /*if(visited.contains(link)) {
                    continue;
                }*/


                if(link==null) {
                    try {
                        notVisited.wait();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    notVisited.notifyAll();
                }
            }
                   /* while(link == null) {
                        try {
                           notVisited.wait();
                            link = notVisited.poll();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    */
                    //notVisited.notifyAll();



            /*if(visited.contains(link))
            {
                continue;
            }*/
            String html = getHTML(link);
            if(html==null) continue;
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("a");
            for (Element e : elements)
            {
                String href = e.attr("href");
                href = postProLink(href, link);
                    if (!visited.contains(href) && !notVisited.contains(href)) {
                        notVisited.add(href);
                    }

            }
            synchronized (cur_num_docs) {
                cur_num_docs++;
            }
            System.out.println(Thread.currentThread()+" : "+ link+ " Num docs= "+ cur_num_docs );
            //todo out document of link
                    visited.add(link);

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
