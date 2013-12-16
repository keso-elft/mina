package com.hbp;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hbp.handler.Handler1000;
import com.hbp.handler.Handler1001;
import com.hbp.handler.Handler1011;
import com.hbp.handler.Handler1012;
import com.hbp.handler.Handler1021;
import com.hbp.handler.Handler1022;
import com.hbp.handler.Handler1031;
import com.hbp.handler.Handler1032;
import com.hbp.handler.Handler1041;
import com.hbp.handler.Handler1042;
import com.hbp.handler.Handler1061;
import com.hbp.handler.Handler1062;
import com.hbp.handler.Handler1072;
import com.hbp.handler.Handler2011;
import com.hbp.handler.Handler2021;
import com.hbp.handler.Handler2031;
import com.hbp.handler.Handler2041;
import com.hbp.handler.Handler2051;
import com.hbp.handler.Handler2071;
import com.hbp.handler.Handler2072;
import com.hbp.handler.Handler3011;
import com.hbp.handler.Handler3012;
import com.hbp.handler.Handler3014;
import com.hbp.util.ClientUtil;

public class ClientModule {

	protected Logger log = LoggerFactory.getLogger(ClientModule.class);

	protected ApplicationContext context;
	
	/**
	 * 按照列表,多server启动
	 */
	public void start() {
		Charset defaultCharset = java.nio.charset.Charset.defaultCharset();
		System.out.println(defaultCharset.name());
		try {
			loadProperties();
			context = new ClassPathXmlApplicationContext(new String[] {
					"applicationContext-resources.xml",
					"applicationContext-hibernate.xml",
					"applicationContext-task.xml"
				});

			log.info("client init ...");
			String allAddresses = System.getProperty("client.address");
			String[] addresses = allAddresses.split(",");

			for (String address : addresses) {
				start(address);
			}
			log.info("all the clients start ...");

		} catch (Exception e) {
			log.error("module started error:" + e.getMessage(), e);
		}
	}

	/**
	 * 单启一个client
	 */
	public void start(String address) {

		MinaClient client = new MinaClient(address);

		client.addHandler("1011", new Handler1011());
		client.addHandler("1012", new Handler1012());
		client.addHandler("1072", new Handler1072());
		client.addHandler("2011", new Handler2011());
		client.addHandler("2021", new Handler2021());
		client.addHandler("2051", new Handler2051());
		client.addHandler("2031", new Handler2031());
		client.addHandler("2041", new Handler2041());
		client.addHandler("2071", new Handler2071());
		client.addHandler("2072", new Handler2072());
		client.addHandler("1022", new Handler1022());

		client.addHandler("1021", new Handler1021());
		client.addHandler("1032", new Handler1032());
		client.addHandler("1031", new Handler1031());
		client.addHandler("1042", new Handler1042());
		client.addHandler("1041", new Handler1041());
		client.addHandler("3011", new Handler3011());
		client.addHandler("1062", new Handler1062());
		client.addHandler("1061", new Handler1061());
		client.addHandler("1000", new Handler1000());
		client.addHandler("1001", new Handler1001());
		client.addHandler("3012", new Handler3012());
		client.addHandler("3014", new Handler3014());

		client.run();
		ClientUtil.addClient(client);
	}

	public void loadProperties() {
		Properties prop = System.getProperties();
		try {
			String fileName = "system.properties";
			File file = new File(fileName);

			if (!file.exists()) {
				fileName = "cfg/system.properties";
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
		if (args.length == 1) {
			module.start(args[0]);
		} else if (args.length == 0) {
			module.start();
		}
	}
}
