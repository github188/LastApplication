package com.cms.iermu.cms.upnp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class SSDPSocket {
	
	MulticastSocket mSSDPSocket;
	
	public SSDPSocket() throws IOException {
		mSSDPSocket = new MulticastSocket();  // can bind port or address
	}
	
	/* Used to send SSDP packet */
	public void send(String data) throws IOException {
		// for broadcast
		InetAddress broadcastAddress = InetAddress.getByName(SSDPConstants.ADDRESS_BROADCAST);
		DatagramPacket dp_broad = new DatagramPacket(data.getBytes(), data.length(), broadcastAddress, SSDPConstants.PORT);
		
		// for multicast
		InetAddress multicastAddress = InetAddress.getByName(SSDPConstants.ADDRESS);
		mSSDPSocket.joinGroup(multicastAddress);
		DatagramPacket dp_multi = new DatagramPacket(data.getBytes(), data.length(), multicastAddress, SSDPConstants.PORT);
		
		mSSDPSocket.send(dp_broad);
		mSSDPSocket.send(dp_multi);
	}
	
	/* Used to receive SSDP packet */
	public DatagramPacket receive() throws IOException {
		byte[] buf = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buf, buf.length);
		mSSDPSocket.setSoTimeout(500);
		mSSDPSocket.receive(dp);
		return dp;
	}
	
	/* Used to receive SSDP packet */
	public DatagramPacket receive(int iTimeout) throws IOException {
		byte[] buf = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buf, buf.length);
		mSSDPSocket.setSoTimeout(iTimeout);
		mSSDPSocket.receive(dp);
		return dp;
	}
	
	public void close() {
		if (mSSDPSocket != null) {
			mSSDPSocket.close();
		}
	}
}
