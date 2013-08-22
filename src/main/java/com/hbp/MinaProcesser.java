package com.hbp;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hbp.handler.MinaHandler;
import com.hbp.message.MinaMessage;

public class MinaProcesser {
	protected static Logger log = LogManager.getLogger(MinaProcesser.class);

	private BlockingQueue<MinaMessage> cache = new ArrayBlockingQueue<MinaMessage>(1000);

	private MinaClient client;

	boolean isStop = false;

	public boolean put(MinaMessage obj) {
		return cache.offer(obj);
	}

	public void start() {
		if (!isStop) {
			try {
				new Thread() {
					public void run() {
						while (true) {
							try {
								MinaMessage msg = (MinaMessage) cache.take();
								if (msg == null)
									continue;
								log.info("本地队列获取到msg:" + msg);

								List<MinaMessage> msgList = dispatch(msg);
								for (MinaMessage rtnMsg : msgList) {
									client.send(rtnMsg);
								}
							} catch (Throwable e) {
								log.error("保存到数据库失败", e);
							}
						}
					}
				}.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private List<MinaMessage> dispatch(MinaMessage msg) {
		MinaHandler handler = client.getHandler(msg.getCN());
		return handler.handleMsg(msg);
	}

	public void stop() {
		isStop = true;
	}

	public MinaClient getClient() {
		return client;
	}

	public void setClient(MinaClient client) {
		this.client = client;
	}
}
