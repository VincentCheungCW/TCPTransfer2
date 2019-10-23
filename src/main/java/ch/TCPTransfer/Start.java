package ch.TCPTransfer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ResourceBundle;

public class Start {
    public static ResourceBundle properties; //存储.properties配置文件数据
    static String GPSAddr;
    static String GPSPort;
    static UDPBroadCaster groupCaster;
    private static Client client;

    public static void main(String[] args) throws Exception {
        //读配置文件configfile.properties
        properties = ResourceBundle.getBundle("configTest");
        GPSAddr = properties.getString("GPSAddr");
        GPSPort = properties.getString("GPSPort");
        String localAddr = properties.getString("LocalAddr");
        String groupAddr = properties.getString("GroupAddr");
        String groupPort = properties.getString("GroupPort");

        try {
            //初始化UDP组播
            groupCaster = new UDPBroadCaster(new InetSocketAddress(groupAddr, Integer.valueOf(groupPort)),
                    InetAddress.getByName(localAddr));

            //启动客户端，连接到GPS主机，接收差分码
            client = new Client();
            client.run();
        } finally {
            //groupCaster.stop();
            //client.stop();
        }

    }
}

