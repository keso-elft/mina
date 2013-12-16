package com.hbp.util;

import java.util.ArrayList;
import java.util.List;

import com.hbp.MinaClient;

public class ClientUtil {

	protected static List<MinaClient> clients = new ArrayList<MinaClient>();

	public static void addClient(MinaClient client) {
		clients.add(client);
	}

	public static MinaClient getClient(String address) {
		for (MinaClient client : clients) {
			if (address != null && address.equalsIgnoreCase(client.getServer())) {
				return client;
			}
		}
		return null;
	}
}
