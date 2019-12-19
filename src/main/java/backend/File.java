package backend;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Future;

import static backend.Peer.toHexString;

public class File {
    private String rootHash,name,category,sharedBy;
    private String[] tags,chunkHashes;
    private long size;

    public File(java.io.File file,String name,String category,String[] tags,CurrentUser peer)throws Exception {
        try(BufferedInputStream in=new BufferedInputStream(new FileInputStream(file))){
            size=file.length();
            ArrayList<String> temp=new ArrayList<>();
            byte bbuf[]=new byte[256000];
            MessageDigest root= MessageDigest.getInstance("SHA-256");
            int len;
            while((len=in.read(bbuf))!=-1){
                root.update(bbuf);
                MessageDigest digest=MessageDigest.getInstance("SHA-256");
                byte[] hash=digest.digest(bbuf);
                temp.add(toHexString((hash)));
            }
            byte[] rootHash=root.digest();
            this.rootHash=(toHexString((rootHash)));
            Object[] arr=temp.toArray();
            chunkHashes= Arrays.copyOf(arr,arr.length,String[].class);
        }catch(Exception e){throw e;}
        String ext=file.getName();
        ext=ext.substring(ext.indexOf('.'));
        this.name=name+ext;
        this.category=category;
        this.tags=tags.clone();
        sharedBy=peer.getNodeID();
    }

    public String getRootHash() {
        return rootHash;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getSharedBy() {
        return sharedBy;
    }

    public String[] getTags() {
        return tags;
    }

    public String[] getChunkHashes() {
        return chunkHashes;
    }

    public long getSize() {
        return size;
    }

    public Future<HttpResponse> addFile() throws Exception {

        Gson gson=new Gson();

        String payload=gson.toJson(this);
        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_JSON);


        CloseableHttpAsyncClient client = Proxy.clientBuild();
        client.start();
        HttpPost request = new HttpPost("https://apnanet-central.herokuapp.com/files");
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json; charset=UTF-8");

        Future<HttpResponse> future = client.execute(request, null);
        return future;
//        System.out.println(gson.toJson(this));

    }
    public static Future<HttpResponse> getFiles() {

        CloseableHttpAsyncClient client = Proxy.clientBuild();
        client.start();
        HttpGet request = new HttpGet("https://apnanet-central.herokuapp.com/files");

        Future<HttpResponse> future = client.execute(request, null);
        return future;
    }

}
