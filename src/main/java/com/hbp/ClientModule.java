package com.hbp;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hbp.handler.Handler1011;
import com.hbp.handler.Handler1012;
import com.hbp.handler.Handler1021;
import com.hbp.handler.Handler1072;
import com.hbp.handler.Handler2011;
import com.hbp.handler.Handler2021;
import com.hbp.handler.Handler2051;

public class ClientModule {

	protected Logger log = LoggerFactory.getLogger(ClientModule.class);

	MinaClient client;

	MinaProcesser processer;

	/**
	 * 启动
	 */
	public void start() {
		init();
		processer.start();
		client.autoConnect();
	}

	public void init() {
		loadProperties();

		client = new MinaClient();
		client.setServer("127.0.0.1:1234");

		client.addHandler("1011", new Handler1011());
		client.addHandler("1012", new Handler1012());
		client.addHandler("1072", new Handler1072());
		client.addHandler("2011", new Handler2011());
		client.addHandler("2021", new Handler2021());
		client.addHandler("2051", new Handler2051());
		client.addHandler("1021", new Handler1021());

		processer = new MinaProcesser();
		processer.setClient(client);

		client.setProcesser(processer);
	}

	public void loadProperties() {
		Properties prop = System.getProperties();
		try {
			String fileName = "../system.properties";
			File file = new File(fileName);

			if (!file.exists()) {
				fileName = "system.properties";
				file = new File(fileName);
				if (!file.exists()) {
					URL url = Thread.currentThread().getContextClassLoader().getResource("system.properties");
					if (url == null)
						throw new Exception("未找到配置文件system.properties");
					fileName = url.getFile();
				}
			}

			prop.load(new FileInputStream(fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ClientModule module = new ClientModule();
		module.start();
	}
}
