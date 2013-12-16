package com.hbp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hbp.handler.MinaHandler;
import com.hbp.message.MinaMessage;

public class MinaProcesser implements Runnable{

	protected static Logger log = LogManager.getLogger(MinaProcesser.class);

	private BlockingQueue<MinaMessage> cache = new ArrayBlockingQueue<MinaMessage>(1000);

	private MinaClient client;

	boolean isStop = false;

	public MinaProcesser(MinaClient client) {
		super();
		this.client = client;
	}

	public boolean put(MinaMessage obj) {
		return cache.offer(obj);
	}

	public void run() {
		while (!isStop) {
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
				log.error("[MinaProcesser]处理消息错误", e);
			}
		}
	}

	private List<MinaMessage> dispatch(MinaMessage msg) {
		List<MinaMessage> result = new ArrayList<MinaMessage>();

		MinaHandler handler = client.getHandler(msg.getCN());
		if (handler != null)
			result = handler.handleMsg(msg);

		return result;
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
