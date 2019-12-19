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
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.protocol.BasicHttpContext;
import ui.Main;


public class Peer{
    public static CurrentUser loggedIn;
    private String nodeID,name,email,passHash;
    public Peer(String name,String email,String password){

        this.name=name;
        this.email=email;
        passHash=BCrypt.hashpw(password, BCrypt.gensalt());
        try {
            MessageDigest temp = MessageDigest.getInstance("SHA-256");


            temp.update(email.getBytes());
            nodeID = Peer.toHexString((temp.digest()));

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


        CloseableHttpAsyncClient client = Proxy.clientBuild();
        client.start();
        HttpPost request = new HttpPost("https://apnanet-central.herokuapp.com/register");
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json; charset=UTF-8");

        Future<HttpResponse> future = client.execute(request, null);
        return future;
//        System.out.println(gson.toJson(this));

    }


    public static Future<HttpResponse> login(String email)throws NoSuchAlgorithmException {

            MessageDigest temp = MessageDigest.getInstance("SHA-256");
            String emailHash = toHexString(temp.digest(email.getBytes()));


            String payload = "{ \"emailHash\": \"" + emailHash + "\" }";
            StringEntity entity = new StringEntity(payload,
                    ContentType.APPLICATION_JSON);


            CloseableHttpAsyncClient client;
            client=Proxy.clientBuild();
            client.start();
            HttpPost request = new HttpPost("https://apnanet-central.herokuapp.com/signin");
            request.setEntity(entity);
            request.setHeader("Content-Type", "application/json; charset=UTF-8");

            Future<HttpResponse> future = client.execute(request, null);
            return future;


    }
    public static Future<HttpResponse> getUser(String nodeID)  {



        CloseableHttpAsyncClient client = Proxy.clientBuild();
        client.start();
        HttpGet request = new HttpGet("https://apnanet-central.herokuapp.com/user/"+nodeID);

        Future<HttpResponse> future = client.execute(request, null);
        return future;
//        System.out.println(gson.toJson(this));

    }


    public String getNodeID() {
        return nodeID;
    }
}