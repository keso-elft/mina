package com.hbp;

import com.hbp.handler.ClientHandler1001;
import com.hbp.handler.ClientHandler1002;

public class ClientModule {
	public static void main(String[] args) {
		MinaClient client = new MinaClient();
		client.setServer("192.168.2.100:1234");
		client.addHandler("1001", new ClientHandler1001());
		client.addHandler("1002", new ClientHandler1002());
		client.autoConnect();
		
//		MinaClient client2 = new MinaClient();
//		client2.setServer("192.168.2.100:2234");
//		client2.addHandler("1001", new ClientHandler1001());
//		client2.addHandler("1002", new ClientHandler1002());
//		client2.autoConnect();
	}
}
