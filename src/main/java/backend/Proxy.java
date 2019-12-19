package backend;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import ui.Main;

public class Proxy {
    private boolean isProxySet,auth;
    private String ip,usr,pass;
    private int port;

    public boolean isProxySet() {
        return isProxySet;
    }

    public boolean isAuth() {
        return auth;
    }

    public String getIp() {
        return ip;
    }

    public String getUsr() {
        return usr;
    }

    public String getPass() {
        return pass;
    }

    public int getPort() {
        return port;
    }

    public Proxy(boolean isProxySet, boolean auth, String ip, String usr, String pass, int port) {
        this.isProxySet = isProxySet;
        this.auth = auth;
        this.ip = ip;
        this.usr = usr;
        this.pass = pass;
        this.port = port;
    }

    @Override
    public String toString() {
        return "Proxy{" +
                "isProxySet=" + isProxySet +
                ", auth=" + auth +
                ", ip='" + ip + '\'' +
                ", usr='" + usr + '\'' +
                ", pass='" + pass + '\'' +
                ", port=" + port +
                '}';
    }
    public static CloseableHttpAsyncClient clientBuild(){
        int timeout = 100;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();
        if(Main.setProxy==null)
            return HttpAsyncClients.custom().setDefaultRequestConfig(config).build();
        else{
            HttpHost proxy=new HttpHost(Main.setProxy.getIp(),Main.setProxy.getPort(),"http");
//                final HttpClientContext localcontext = HttpClientContext.adapt(new BasicHttpContext());
//                localcontext.setRequestConfig(config);
            if(!Main.setProxy.isAuth())
                return HttpAsyncClients.custom().setProxy(proxy).setDefaultRequestConfig(config).build();
            else {
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                        new AuthScope(AuthScope.ANY),
                        new UsernamePasswordCredentials(Main.setProxy.getUsr(), Main.setProxy.getPass()));
                return HttpAsyncClients.custom().setProxy(proxy)
                        .setDefaultCredentialsProvider(credsProvider)
                        .setDefaultRequestConfig(config)
                        .build();
            }
        }
    }
}
