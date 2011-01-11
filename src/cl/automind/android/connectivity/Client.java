package cl.automind.android.connectivity;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Logger;

import cl.automind.android.utils.ExportableObject;
/**
 * El puente Java-C# usando un Socket
 * @author Guillermo
 *
 */
public class Client implements IClient{
	//	private String HOST = "192.168.1.100";
	//	private String HOST = "127.0.0.1";
	private String HOST = "votes.automind.cl";
	private String PORT = "8001";
	private int TIMEOUT = 2000;
	private Socket socket;
	public static final String SUCCESS = "S";
	public static final String FAIL = "F";
	private static Client instance;

	private Client(){
	}

	private Client(String host, String port){
		try{
			Properties p = new Properties();
			p.load(Client.class.getResourceAsStream("/connection.properties"));
			p.getProperty("host", HOST);
			p.getProperty("port", PORT);	
		} catch (Exception e){
			HOST = host;
			PORT = port;
		}
	}

	public static Client getClient(){
		if (instance == null) instance =  new Client();
		return instance;
	}
	public String exportObject(ExportableObject data){
		String output = FAIL;
		try {
			// Code in parent process
			InetAddress address = InetAddress.getByName(HOST);  
			socket = new Socket();  
			InetSocketAddress isa = new InetSocketAddress(address, Integer.parseInt(PORT));  
			ThreadGroup tgroup = new ThreadGroup("mysockgrp");  
			   
			// Set to thirty seconds here  
			SocketTimeoutThread sct = new SocketTimeoutThread(tgroup, "myname", TIMEOUT, socket);  
			sct.start();  
			socket.connect(isa); //<--- if passes here then thread remember set to null  
			sct.set(null);  
			
			// send
			OutputStream os = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(os, true);
			String[] values = data.getValues();
			for (String s:values) pw.println(s);
			// receive
			InputStream  is = socket.getInputStream();
			BufferedReader bf = new BufferedReader(new InputStreamReader(is));
			String incoming = bf.readLine();
			
			output = incoming.equals("S") ? SUCCESS : FAIL;
			pw.close();
			os.close();			
			socket.close();		
		} catch (Exception e) {
			output = FAIL;
		} 
		Logger.global.warning(output);
		return output;
	}

	public ExportableObject importObject() {
		return null;
	}
	/******* Below thread class ***/  
	class SocketTimeoutThread extends Thread{  

		private int          sleepValue = 10000; // In millis!!! 10 seconds  
		private Socket       remember;  

		public SocketTimeoutThread(ThreadGroup tg, String u, int i, Socket sock){  
			super(tg, u);  
			sleepValue = i;  
			remember = sock;  
		}  

		public void set(Socket s){  
			remember = s;  
		}  

		public void run(){  
			try{Thread.sleep(sleepValue);  
			if(remember != null){  
				remember.close();  
				System.out.println("Socket thread expired, had to closed");  
			} else  
				System.out.println("Socket thread expired, all okay");  
			}catch(Exception ie) {}  
		}  

		public void end(){  
			interrupt();  
		}  
	}
}
