package com.o0live0o.app.appearance.service;

import com.o0live0o.app.appearance.log.L;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpTool {
    private static String UDPAdress;
    private static int UDPPort;
    private static int Times;


    public static void init(String address,int port,int times){
        UDPAdress = address;
        UDPPort = port;
        Times = times;
    }

    public static void send(final String hphm,final String msg) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket datagramSocket = new DatagramSocket(UDPPort);
                    InetAddress address = InetAddress.getByName(UDPAdress);

                    StringBuilder sb = new StringBuilder();
                    int upLen = hphm.length();
                    int downLen = msg.length();
                    byte[] totalLenByte = ByteTools.intToByteArray(upLen+downLen);
                    byte[] upLenByte = ByteTools.intToByteArray(upLen);
                    byte[] downByte = ByteTools.intToByteArray(downLen);
                    ByteArrayBuffer bufferArray = new ByteArrayBuffer();
                    bufferArray.append(new byte[]{0x01});
                    bufferArray.append(new byte[]{0x10});
                    bufferArray.append(new byte[]{0x03});
                    bufferArray.append(new byte[]{0x05});
                    bufferArray.append(hphm.getBytes());
                    bufferArray.append(msg.getBytes());
                    int crcVal = CRC16.CRC16_MODBUS(bufferArray.toByteArray());

                    ByteArrayBuffer buffer = new ByteArrayBuffer();
                    buffer.append(new byte[]{0x02});
                    buffer.append(new byte[]{0x01});
                    buffer.append(new byte[]{0x01});
                    buffer.append(new byte[]{0x10});
                    buffer.append(new byte[]{0x03});
                    buffer.append(new byte[]{0x05});
                    buffer.append(hphm.getBytes());
                    buffer.append(msg.getBytes());
                    buffer.append(ByteTools.intToByteArray(crcVal));
                    buffer.append(new byte[]{0x10,0x11});

                    byte[] data = buffer.toByteArray();
                    String s = ByteTools.bytes2HexString(data);
                    L.d("往LED屏发送指令："+s);
                    L.d("IP:"+UDPAdress+",端口："+String.valueOf(crcVal)+",CRC:"+String.valueOf(crcVal));
                    DatagramPacket packet = new DatagramPacket(data,data.length,address,UDPPort);
                    //for (int i=0;i<Times;i++) {
                    datagramSocket.send(packet);
                       // Thread.sleep(100);
                    //}
                    datagramSocket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread.start();

    }

    public  static  void  cal_crc()
    {

    }
}
