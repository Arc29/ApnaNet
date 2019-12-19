package backend;

import java.net.*;


public class Server implements Runnable{
    private final static int port = 30303;
    private boolean running;

    public Server(){running=true;}


    public void run(){
        while ((running)){
        ServerSocket ssock = null;
        try{

            ssock = new ServerSocket(port);

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error occured...failed to initialize Server Socket");
        }

        while(true){
            Socket sock = null;
            try{
            	System.out.println("Waiting for connection..!!");
                sock = ssock.accept();
                System.out.println("Connection received...!!!");
               new Thread(new ServerHandler(sock)).start();

            }catch(Exception e){
                e.printStackTrace();
                System.out.println("Failed to connect to client..!!");
            }
        }
    }}

    public void interrupt(){
        running=false;
    }
}
