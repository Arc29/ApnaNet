package backend;

import javafx.concurrent.Task;

import java.io.RandomAccessFile;

public class DownloadManager extends Task<Void> {
    private Client c;
    public DownloadManager(Client client){
        c=client;
    }
    @Override
    protected Void call() throws Exception {
        long fileSize=c.getFileSize();
        RandomAccessFile file=new RandomAccessFile(Client.FILEPATH+"/"+c.getFileName(),"r");
        while (file.length()<fileSize) {
            Thread.sleep(500);
            updateProgress(file.length(), fileSize);
        }
        return null;
    }
    @Override
    protected void failed() {
        System.out.println("failed");
    }

    @Override
    protected void succeeded() {
        System.out.println("downloaded");
    }
}
