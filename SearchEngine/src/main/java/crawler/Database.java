package main.java.crawler;

import javax.swing.plaf.nimbus.State;
import java.sql.*;

public class Database
{
    Connection connection;
    Statement statement;
    ResultSet resultSet;
    //PreparedStatement preparedStatement;

    public Database()
    {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/crawler_db", "root", "data6OO6suck5");
            statement = connection.createStatement();
        } catch (Exception e) {
            System.out.println("Error in connecting to database!");
        }
    }

    public int getCount(String table)
    {
        int num=-1;
        try {
           //preparedStatement=connection.prepareStatement("SELECT COUNT(*) AS count FROM " + table + ";");
            Statement st=connection.createStatement();
            resultSet = st.executeQuery("SELECT COUNT(*) AS count FROM " + table + ";");
            resultSet.next();
            num=resultSet.getInt("count");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Error in getCount!");
        }
        return num;
    }

    public String dbDeque(String table)
    {
        String link=null;
        String selectQuery="SELECT link AS link FROM " + table + " ORDER BY id LIMIT 1";
        String deleteQuery="DELETE FROM " + table + " ORDER BY id LIMIT 1";
        try
        {
            Statement st=connection.createStatement();
            resultSet=st.executeQuery(selectQuery);
            if(resultSet.next())
                link=resultSet.getString("link");
            statement.executeUpdate(deleteQuery);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Error in dbDeque!");
        }
        return link;
    }

    boolean exists(String table,String link)
    {
        int result=-1;
        String query="SELECT EXISTS(SELECT * FROM " + table + " WHERE link='" + link + "')AS link";
        try {
            Statement st= connection.createStatement();
            resultSet = st.executeQuery(query);
            resultSet.next();
            result=resultSet.getInt("link");
        }
        catch(Exception e)
        {
            System.out.println("Error in empty!");
        }
        return result == 1;
    }

    void dbEnqueue(String table, String link)
    {
        String query= "INSERT INTO " + table + " (link) VALUES ('" + link + "');";
        try
        {
            Statement st= connection.createStatement();
            st.executeUpdate(query);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error in dbEnqueue");
        }
    }

}
