package com.castlerockresearch;

import java.io.*;
import java.net.*;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;


import android.app.Activity;
import android.content.Context;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;

public class WindTalk extends Activity {
	private JmDNS jmdns;

	private MulticastLock multicastLock;

	public final static String SERVICE_TYPE = "_vishnu._tcp.local.";
	
	public final static String SERVICE_NAME = "test_service";

	public static final String HOST_NAME = "Android";
	
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		try {
			multicastLock = wifi.createMulticastLock("testing");
			multicastLock.setReferenceCounted(true);
			multicastLock.acquire();

			jmdns = JmDNS.create();
			
			ServiceInfo sInfo = ServiceInfo.create(SERVICE_TYPE,SERVICE_NAME,2008,"some_text");
			jmdns.registerService(sInfo);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
			
	@Override
	protected void onPause() {
		super.onPause();
		multicastLock.release();
	}
}
