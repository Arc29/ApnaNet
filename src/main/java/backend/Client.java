package backend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.io.File;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.Future;


public class Client{
    public static String FILEPATH="./Downloads";
    public static int threads;
    private static Client[] files;
    private  ArrayList<TreeMap<String,String>> activepeers;
    private String rootHash,fileName;
    private String[] chunkHashes;
    private int totalChunks;
    private long fileSize;
    private long[] filePointers;
    private ArrayList<Integer> threadsEngaged;
    private final String settingsDir="./settings.json";
    private ThreadGroup threadGroup;
    public Client(String rootHash,String[] chunkHashes,long fileSize,String fileName){
        activepeers=new ArrayList<>();
        this.fileName=fileName;
        this.rootHash=rootHash;
        this.chunkHashes=chunkHashes.clone();
        this.fileSize=fileSize;
        this.totalChunks=(int)Math.ceil(fileSize/256000.0);
        filePointers=new long[totalChunks];
        loadPointers();
        threadsEngaged=new ArrayList<>();
        threadGroup=new ThreadGroup("downloads");
    }

    public static void checkHash(int chunk,String fileName,int chunkSize,String chunkHash,Client caller){
        try{
        RandomAccessFile file=new RandomAccessFile(FILEPATH+fileName,"r");
        byte[] buffer=new byte[256000];
        file.seek(chunk*256000);
        file.read(buffer,0,chunkSize);
        MessageDigest digest=MessageDigest.getInstance("SHA-256");
        String hash=Peer.toHexString(digest.digest(buffer));
        if(hash.equals(chunkHash)){
            System.out.println("Chunk "+chunk+ "downloaded");
            }
            else {
            caller.stop(true);
            throw new InvalidObjectException("Hashes don't match");
        }
        }catch(Exception e){System.err.println(e);}

        }
    public void stop(boolean reset){
        if(reset){
            for(int i=0;i<totalChunks;i++)
                filePointers[i]=i*256000;
        }
        savePointers();
        Thread[] list=new Thread[threadGroup.activeCount()];
        int count=threadGroup.enumerate(list);
        for(int i=0;i<count;i++)
            list[i].interrupt();
    }
    public void loadPointers(){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILEPATH+"/"+rootHash+"pointer.bin"))) {
            for(int i=0;i<totalChunks;i++)
                filePointers[i]=in.readLong();
        }catch(IOException e){
            for(int i=0;i<totalChunks;i++)
                filePointers[i]=256000*i;

        }
    }
    public void savePointers(){
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILEPATH+"/"+rootHash+"pointer.bin"))) {
            for (long n : filePointers) {
                out.writeLong(n);
            }
        }catch(IOException e){e.printStackTrace();}
    }

    public void loadSettings(){
        try{

            Gson gson = new Gson();
            String content= Files.readString(Paths.get(settingsDir));
            JsonElement jsonElement = new JsonParser().parse(content);
            JsonObject object = jsonElement.getAsJsonObject();
            FILEPATH = object.get("path").getAsString();
            threads=object.get("threads").getAsInt();

        }catch (IOException e){FILEPATH="Downloads";threads=3;}
    }
    public void saveSettings(String path,int thread){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(settingsDir,false))) {
            String json = "{ \"path\": " + path + ", \"threads\": " + thread + " }";
            writer.write(json);
        }catch (IOException e){e.printStackTrace();}
    }
    public static void loadFiles() {
        try {
            Gson gson = new Gson();
            String content = Files.readString(Paths.get("files.json"));
            files = gson.fromJson(content, Client[].class);
        } catch (Exception e) {
            files=null;
        }
    }
    public static void saveFiles() {
        if(files!=null){
            Gson gson=new Gson();
            String dir="files.json";

            try(BufferedWriter writer=new BufferedWriter(new FileWriter(dir,false))) {
                writer.write(gson.toJson(files));
            }catch (Exception e){e.printStackTrace();}
        }
    }
    public static ObservableList<Client> currentFiles(){

        ObservableList<Client> files= FXCollections.observableArrayList();
        if(Client.files==null)
            return files;
        File folder=new java.io.File(FILEPATH);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                for (int j = 0; j < Client.files.length; j++) {
                    if (Client.files[i].fileName.equals(listOfFiles[i].getName())) {
                        files.add(Client.files[i]);
                        break;
                    }
                }
            }
        }
        return files;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public static void addToFiles(Client c){
        boolean flag=true;
        if (files==null) {
            ArrayList<Client> temp = new ArrayList<>();
            temp.add(c);
            files = temp.toArray(new Client[1]);
        }
        else{
        for(int i=0;i<files.length;i++){
            if(files[i].equals(c)){
                flag=false;
                break;
            }
        }
        if(flag) {
            ArrayList<Client> temp = new ArrayList<>(Arrays.asList(files));
            temp.add(c);
            files = temp.toArray(new Client[files.length + 1]);
        }
        }
    }
    public void updatePeers()throws Exception{
        Gson gson=new Gson();
        CloseableHttpAsyncClient client = Proxy.clientBuild();
        client.start();
        HttpGet request = new HttpGet("https://apnanet-central.herokuapp.com/files/"+rootHash);
        Future<HttpResponse> future = client.execute(request, null);
        String json = EntityUtils.toString(future.get().getEntity(), StandardCharsets.UTF_8);

        ActivePeer[] peers = gson.fromJson(json,ActivePeer[].class);
        activepeers.clear();
        for(ActivePeer i:peers){
            if(!i.getNodeID().equals(Peer.loggedIn.getNodeID())) {
                TreeMap<String, String> map = new TreeMap<>();
                map.put("ipAddress", i.getIpAddress().substring(i.getIpAddress().lastIndexOf("ffff:")+5));
                map.put("port", Integer.toString(i.getPort()));
                activepeers.add(map);
            }
        }
        if(activepeers.isEmpty())
            System.out.println("No peers active");
        else
        System.out.println("Active peers: "+activepeers);
    }
    public TreeMap<String,String> getPeerWithChunk(int chunk){
        TreeMap<String,String> peer=null;
        for(TreeMap<String,String> i:activepeers){

            try(Socket sock=new Socket(i.get("ipAddress"),30303)){

                DataOutputStream dos=new DataOutputStream(sock.getOutputStream());
                dos.writeChar('P');
                dos.writeChars(rootHash);
                dos.writeLong(filePointers[chunk]);
                dos.close();
                DataInputStream dis=new DataInputStream(sock.getInputStream());
                if(dis.readChar()=='P'&&dis.readBoolean())
                    peer=i;
                dis.close();

            }catch(Exception e){e.printStackTrace();return  null;}
        }
        return peer;
    }
    public int getLastNonCompletedChunk(){ //start is starting chunk
        for(int i=0;i<totalChunks;i++) {
            if (i != totalChunks - 1 && filePointers[i] < (i + 1) * 256000 && !threadsEngaged.contains(i))
                return i;
            if (i == totalChunks - 1 && filePointers[i] < (fileSize )&& !threadsEngaged.contains(i))
                return i;
        }
        return -1;
    }

    public void download(){
        loadSettings();
        Client.addToFiles(this);
        int chunk=0;

        while(true){
            if(getLastNonCompletedChunk()==-1){
                System.out.println("Download complete");

                return;
            }
            while(threadsEngaged.size()<threads){
                try{
                    chunk=getLastNonCompletedChunk();
                    if(chunk==-1){System.out.println("Download complete");return;}
                    threadsEngaged.add(chunk);
                    TreeMap<String,String> map=getPeerWithChunk(chunk);
                    if(map==null){updatePeers();continue;}
                    Socket sock =new Socket(map.get("ipAddress"),30303);
                    int chunkSize=chunk!=totalChunks-1?256000:(int)(fileSize-(totalChunks-1)*256000);
                    Thread peer=new Thread(threadGroup,new ClientHandler(chunk,sock,chunkHashes[chunk],fileName,filePointers,threadsEngaged,chunkSize,this));
                    peer.setDaemon(true);
                    peer.start();


                }catch(Exception e){e.printStackTrace();}
            }
        }

    }



/*
    1)Get list of active peers sharing the file
    2)Request peers for numbered chunks(chunk000,chunk001,etc)
    3) Keep joining chunks
    */

}
