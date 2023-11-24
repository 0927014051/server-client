package rpc.chat.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import rpc.chat.interfaces.IProxy;
import rpc.chat.interfaces.IProxyFactory;

public class RPCRuntime implements Runnable {

	ServerSocket socket;
	
	boolean running = true;
	
	HashMap<String, IProxyFactory> factories = new HashMap<>();
	
	public RPCRuntime(ServerSocket srvs) {
		this.socket = srvs;
	}
	
	@Override
	public void run() {
		while (running) {
			try {
				
				Socket input = socket.accept();
				
				PrintWriter pw = new PrintWriter(input.getOutputStream());
				pw.println(factories.size());
				for (String name : factories.keySet()) {
					pw.println(name);
				}
				pw.flush();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(input.getInputStream()));
				String proxyName = br.readLine();
				
				if (factories.containsKey(proxyName)) {
					
					IProxy p = factories.get(proxyName).createProxy(br, pw);
					(new Thread(p)).start();
				
				} else {

					pw.println("404 - Proxy Not Found");
					pw.flush();
					socket.close();
					
				}
			
			} catch (IOException e) {
			
				e.printStackTrace();
			
			}
		}

	}
	
	public void register(String name, IProxyFactory proxyFactory) {
		factories.put(name, proxyFactory);
	}

}
