package com.castlerockresearch;

import java.io.*;
import java.net.*;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.jmdns.impl.JmDNSImpl;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;

public class WindTalk extends Activity {
	private JmDNS jmdns;

	private MulticastLock multicastLock;

	public final static String SERVICE_TYPE = "_classroomresponse._tcp.local.";

	public static final String HOST_NAME = "Android";

	public final static String WIFI_LOCK_KEY = "solaro_respond";
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		WifiInfo wifiInfo = wifi.getConnectionInfo();

		int intaddr = wifiInfo.getIpAddress();
		byte[] byteaddr = new byte[] { (byte) (intaddr & 0xff),
				(byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff),
				(byte) (intaddr >> 24 & 0xff) };
		try {
			InetAddress addr = InetAddress.getByAddress(byteaddr);
			multicastLock = wifi.createMulticastLock("testing");
			multicastLock.setReferenceCounted(true);
			multicastLock.acquire();

			jmdns = JmDNS.create();

			jmdns.addServiceListener("_workstation._tcp.local.", new ServiceListener() {
				
				@Override
				public void serviceResolved(ServiceEvent event) {
					System.out.println(event.getInfo().getNiceTextString());
					getMoreDetails(event);
					
					//runServer();
				}
				
				@Override
				public void serviceRemoved(ServiceEvent event) {
					System.out.println(event.getInfo().getNiceTextString());
					
				}
				
				@Override
				public void serviceAdded(ServiceEvent event) {
					System.out.println(event.getInfo().getNiceTextString());
					getMoreDetails(event);
				}
			});
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		

	}
	
	private void getMoreDetails(ServiceEvent event){
		JmDNSImpl impl = (JmDNSImpl) event.getSource();
        javax.jmdns.ServiceInfo info = impl.getServiceInfo(event.getType(), event.getName());
        int port = info.getPort();
        String server = info.getInet4Address().getHostAddress();
        String name = info.getName();
        System.out.println(server+":::"+port +" ("+name+")");
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		multicastLock.release();
	}
}
