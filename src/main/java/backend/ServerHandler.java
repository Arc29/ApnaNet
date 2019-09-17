package backend;

import java.io.*;
import java.net.*;

import static backend.Client.FILEPATH;


public class ServerHandler implements Runnable{
    private Socket sock;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean running;

    public ServerHandler(Socket sock){
        this.sock = sock;
        this.running = false;
    }

    public void run() {
        while (running) {
            try {
                dis = new DataInputStream(sock.getInputStream());
                dos = new DataOutputStream(sock.getOutputStream());
                while (true) {
                    if (dis.readChar() == 'P') {
                        String rootHash = dis.readUTF();
                        long chunk = dis.readLong();
                        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILEPATH + "/" + rootHash + "pointer.bin"))) {
                            long fp = -1;
                            for (int i = 0; i <= chunk; i++)
                                fp = in.readLong();
                            dos.writeChar('P');
                            if (fp > chunk)
                                dos.writeBoolean(true);
                            else
                                dos.writeBoolean(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (dis.readChar() == 'D') {
                        String fileName = dis.readUTF();
                        long pointer = dis.readLong();
                        RandomAccessFile file = new RandomAccessFile(FILEPATH + "/" + fileName, "r");
                        file.seek(pointer);
                        dos.writeChar('D');
                        byte[] buffer = new byte[8192];
                        int count;
                        while ((count = file.read(buffer)) > 0)
                            dos.write(buffer, 0, count);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();

                System.out.println("Some error occured");
                interrupt();
            }
        }
    }

    private void interrupt() {
        try{sock.close();}catch (Exception e){e.printStackTrace();}
        running=false;
    }

}
