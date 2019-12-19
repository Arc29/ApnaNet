package backend;

import org.apache.http.entity.ContentType;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class ClientHandler implements Runnable{
    private Socket sock;
    DataInputStream dis;
    DataOutputStream dos;
    private int chunk,chunkSize;
    private long[] filePointer;
    private String chunkHash,fileName;
    private ArrayList<Integer> engaged;
    private boolean running;
    private RandomAccessFile file;
    private Client caller;

    ClientHandler(int chunk,Socket sock,String chunkHash,String fileName,long[] filePointer,ArrayList<Integer> engaged,int chunkSize,Client c){
        this.chunkHash=chunkHash;
        this.chunk=chunk;
        this.sock = sock;
        this.fileName=fileName;
        this.filePointer=filePointer;
        dis=null;
        dos=null;
        this.engaged=engaged;
        this.chunkSize=chunkSize;
        running=true;
        caller=c;
    }

    public void run() {
        if (running) {
            try {

                dos = new DataOutputStream(sock.getOutputStream());
                dos.writeChar('D');

                dos.writeChars(fileName);
                dos.writeLong(filePointer[chunk]);
                dos.close();
                dis = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
                InputStream is=sock.getInputStream();
                file = new RandomAccessFile(Client.FILEPATH +"/"+ fileName, "rw");
                file.seek(filePointer[chunk]);
                if(dis.readChar()=='D'){
                byte[] buffer = new byte[8192];
                int count;
                while ((count = is.read(buffer)) > 0) {
                    file.write(buffer, 0, count);

                    if((filePointer[chunk] = file.getFilePointer())==((chunk*256000)+chunkSize)){
                        Client.checkHash(chunk,fileName,chunkSize,chunkHash,caller);
                        interrupt();
                    }
                }}
                interrupt();


            } catch (Exception e) {
                e.printStackTrace();
                interrupt();
                System.out.println("Some error occured");
            }
        }
    }
    public void interrupt(){
        engaged.remove(Integer.valueOf(chunk));
        try{sock.close();file.close();caller.updatePeers();}catch (Exception e){System.out.println(e);}
        running=false;
    }

}
