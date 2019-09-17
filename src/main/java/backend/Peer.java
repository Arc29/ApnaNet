package backend;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.Future;

import com.google.gson.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;


public class Peer{
    public static Peer loggedIn;
    private String nodeID,name,email,passHash;
    public Peer(String name,String email,String password){

        this.name=name;
        this.email=email;
        passHash=BCrypt.hashpw(password, BCrypt.gensalt());
        try {
            MessageDigest temp = MessageDigest.getInstance("SHA-256");

            temp.update(name.getBytes());
            temp.update(email.getBytes());
            nodeID = Peer.toHexString((temp.digest()));
            loggedIn=this;
        }catch(Exception e){System.err.println(e);}
    }
    public static String toHexString(byte[] byteData)
    {
        // Convert byte array into signum representation
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
    public Future<HttpResponse> addUser() throws Exception {

        Gson gson=new Gson();

        String payload=gson.toJson(this);
        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_JSON);


        CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
        client.start();
        HttpPost request = new HttpPost("https://apnanet-central.herokuapp.com/register");
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json; charset=UTF-8");

        Future<HttpResponse> future = client.execute(request, null);
        return future;
//        System.out.println(gson.toJson(this));

    }

    public void loadLoggedIn(String dir)throws Exception {
       try {
           String path = dir + "loggedIn.json";

           BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
           Gson gson = new Gson();
           loggedIn = gson.fromJson(bufferedReader, Peer.class);
       }catch(IOException e){loggedIn=null;}
    }

    public void saveLoggedIn(String dir)throws IOException{
        if(loggedIn!=null){
        Gson gson=new Gson();
        dir=dir+"loggedIn.json";
        BufferedWriter writer=new BufferedWriter(new FileWriter(dir,false));
        writer.write(gson.toJson(loggedIn));
    }}
    public Future<HttpResponse> logout()throws IOException{
        if(loggedIn!=null){
            loggedIn=null;
            System.out.println("Logged out successfully");

            CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
            client.start();
            HttpDelete request = new HttpDelete("https://apnanet-central.herokuapp.com/signin/"+Peer.loggedIn.nodeID);
            Future<HttpResponse> future = client.execute(request, null);
            return future;

        }
        else
            System.out.println("Already logged out");
            return null;
    }
    public static Future<HttpResponse> login(String email)throws NoSuchAlgorithmException {

            MessageDigest temp = MessageDigest.getInstance("SHA-256");
            String emailHash = toHexString(temp.digest(email.getBytes()));


            String payload = "{ \"emailHash\": \"" + emailHash + "\" }";
            StringEntity entity = new StringEntity(payload,
                    ContentType.APPLICATION_JSON);


            CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
            client.start();
            HttpPost request = new HttpPost("https://apnanet-central.herokuapp.com/signin");
            request.setEntity(entity);
            request.setHeader("Content-Type", "application/json; charset=UTF-8");

            Future<HttpResponse> future = client.execute(request, null);
            return future;


    }

    public String getNodeID() {
        return nodeID;
    }
}