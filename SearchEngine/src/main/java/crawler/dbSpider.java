package main.java.crawler;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class dbSpider implements Runnable
{
    final Set<String> visited=new HashSet<String>();
    static final Queue<String> notVisited=new LinkedList<String>();
    public static final Database db=new Database();
    int MAX_DOCS=5000;
    Integer cur_num_docs=0;
    FileWriter out;


    public dbSpider()
    {
        try {
            out = new FileWriter(new File(".\\SearchEngine\\links.txt"),true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void crawl()
    {

        while(db.getCount("visited")<=MAX_DOCS)
        {

            int numRows= db.getCount("not_visited");
            if(numRows<=0) break;

            String link=null;

            synchronized (db)
            {
                link= db.dbDeque("not_visited");
            }
            if(db.exists("visited",link)) continue;

            /*
            String html = getHTML(link);
            if(html==null) continue;
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("a");
            for (Element e : elements)
            {
                String href = e.attr("href");
                href = postProLink(href, link);
                if(href==null) continue;
                //if (!visited.contains(href) && !notVisited.contains(href)) {
                if(!db.exists("visited",href)&&!db.exists("not_visited",href))
                {
                    db.dbEnqueue("not_visited",href);
                    System.out.println("This is the href: "+ href+" and numDocs is "+cur_num_docs);
                    synchronized (cur_num_docs) {
                    cur_num_docs++;
                    }

                }

            }
            if(link!=null) db.dbEnqueue("visited",link);
*/          String[] subLinks=new String[100];
            int count=hasRobot(subLinks,100,link);
            try {
                Document doc = request(link);
                if (doc != null) {
                    for (Element linkElement : doc.select("a[href]")) {
                        String nextLink = linkElement.absUrl("href");
                        //synchronized (db)
                        //{
                        for(int i=0;i<count;i++)
                        {
                            if (new URL(nextLink).getHost().equals(new URL(subLinks[i]).getHost()))
                            {
                                if(nextLink.contains(subLinks[i]))
                                {
                                    System.out.println("Robots.txt disallows crawling this link");
                                    nextLink=null;
                                    break;
                                }
                            }
                        }
                        if (!db.exists("visited", nextLink) && !db.exists("not_visited", nextLink)&&nextLink!=null) {
                                //System.out.println(Thread.currentThread()+ ": Received webpage at "+ nextLink);
                                db.dbEnqueue("not_visited", nextLink);
                        }
                        //}
                    }
                }
            }
            catch(Exception e){
                System.out.println("Error in crawling!");
                e.printStackTrace();
            }

            //synchronized (cur_num_docs) {
            //    cur_num_docs++;
            //}
            //System.out.println(Thread.currentThread()+" : "+ link + " Num docs= "+ cur_num_docs );


        }
        try {
            out.close();
        }
        catch(Exception e)
        {e.printStackTrace();}
    }


    private Document request(String url)
    {
        try
        {
            Connection con= Jsoup.connect(url);
            Document doc=con.get();
            if(con.response().statusCode()==200)
            {
                synchronized (db) {
                    out.write(url + "\n");
                }
                try {
                    out.flush();
                }
                catch(Exception e)
                {e.printStackTrace();}
                System.out.println(Thread.currentThread()+ ": Received webpage at "+ url);
                String title= doc.title();
                System.out.println(title);
                //visited.add(url);
                db.dbEnqueue("visited",url);
                return doc;
            }
            return null;
        }
        catch (IOException ioe)
        {
            //ioe.printStackTrace();
            return null;
        }
    }

    int hasRobot(String[] subLinks,int size,String link)
    {
        if(link==null) return -1;
        URL url;
        try {
            url = new URL(link);
        }catch(Exception e){e.printStackTrace();url=null;}

        String robot = url.getProtocol()+"://"+url.getHost()+"/robots.txt";
        //Connection con = Jsoup.connect(robot);
        //Connection s =con.requestBody(robot);
        //System.out.println(s);
        //return -1;
        //if(!link.endsWith(".com")&&!link.endsWith("/")) return -1;
        //if(link.endsWith("/")) link=link.substring(0,link.length()-1);
        boolean res=true;
        int count=0;
        try(BufferedReader in = new BufferedReader(new InputStreamReader(new URL(robot).openStream()))) {
            String line = null;
            while((line = in.readLine()) != null) {
                if(line.startsWith("User-agent: *"))
                {
                    line=in.readLine();
                    while(line!=null&&!line.startsWith("User-agent:"))
                    {
                        while(line.startsWith("Disallow: /"))
                        {
                            subLinks[count++]=url.getProtocol()+"://"+url.getHost()+line.substring(10,line.length());
                            //System.out.println(subLinks[count-1]);
                            line=in.readLine();
                        }
                        line=in.readLine();

                    }
                }
               // else if(Disallow :/

            }
        } catch (IOException e) {
            res=false;
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public void run()
    {
        crawl();
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


    /*
     *base is broken down into
     * protocol://host/path/file?query
     *Realative path
     *protocol:/host/path/realtive
     */
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
                /*else if(link.startsWith("#"))   //better remove this
                {

                    link=base+link;
                    //link=link.substring(0,link.length()-1);
                }*/
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
                else
                {
                    return null;
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

}
