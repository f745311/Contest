package dmcl.csmuse2016;

import android.app.Application;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class connect extends Application {
    //用於連接php的class
    private String urlString = "http://dmcl.twbbs.org/csmuse/userAccount.php?sql=";
    private String getFromServer;
    private InputStream in;
    private String response;
    private String sqlCommand;

    public connect(String sqlCommand){
        try {
            this.sqlCommand= URLEncoder.encode(sqlCommand, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public String getServerUpdate() {
        try {
            String connectToDB = urlString + sqlCommand;
            URL url = new URL(connectToDB);
            in = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String n;
            StringBuilder sb = new StringBuilder();
            while((n = br.readLine())!=null){
                sb.append(n);
            }
            getFromServer = "done";
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

        return getFromServer;
    }

    public String getServerConnect() {
        try {
            String connectToDB = urlString + sqlCommand;
            //Log.v("connect1", connectToDB);
            URL url=new URL(connectToDB);
            in=url.openStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(in));
            String n=new String();
            StringBuilder sb=new StringBuilder();
            while((n=br.readLine())!=null){
                sb.append(n);
            }
            //Log.v("connect",sb.toString());
            String[] check=sb.toString().split(":");
            if(check[0].equals("Warning"))
                getFromServer="wrong";
            else
                getFromServer=sb.toString();

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

        return getFromServer;
    }
    //登入時的php check
    public String LoginCheck(){
        try {
            String connectToDB=urlString+sqlCommand;
            Log.v("connect1",connectToDB);
            URL url=new URL(connectToDB);
            in=url.openStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(in));
            String n= new String();
            StringBuilder sb=new StringBuilder();
            while((n=br.readLine())!=null){
                sb.append(n);
            }
            Log.v("connect",sb.toString());
            return sb.toString();//all check updated 2016/04/03


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
       return "";
    }

}