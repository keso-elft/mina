package com.hbp;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hbp.message.MinaMessage;
import com.hbp.receiver.MinaReceiver;

public class MinaClientHandlerAdapter extends IoHandlerAdapter {

	protected Logger log = LoggerFactory.getLogger(MinaClientHandlerAdapter.class);

	private MinaClient minaClient;

	public MinaClientHandlerAdapter(MinaClient connector) {
		this.minaClient = connector;
	}

	/**
	 * 接收消息
	 * 
	 * @param msg
	 * @return
	 */
	public void messageReceived(IoSession session, Object message) {
		log.debug("===> client received @" + message + "");
		if (message instanceof String) {
			MinaMessage msg = new MinaMessage((String) message);

			try {
				MinaMessage resp = this.replyMsg(msg);
				// 需应答的消息需要缓存起来
				if (msg.getRtnFlag() % 2 == 1) {
					minaClient.putHasReplyMsg(msg);
				}
				if (resp != null && session.isConnected()) {
					session.write(resp);
				}
				// 只有验证成功且为请求消息才加入队列
				if (resp != null && resp.isSuccess() && msg.isRequest()) {
					dispatch2handler(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("client message Received exception : " + e.getMessage() + e.getCause());
			}
		}
	}

	/**
	 * 回复消息
	 * 
	 * @param msg
	 * @return
	 */
	public MinaMessage replyMsg(MinaMessage msg) {
		MinaReceiver receiver = minaClient.getReceiver();
		MinaMessage respMsg = null;
		if (receiver != null) {
			respMsg = receiver.replyMsg(msg);
		}

		return respMsg;
	}

	/**
	 * 将待处理消息分发到处理器
	 * 
	 * @param msg
	 */
	public void dispatch2handler(MinaMessage msg) {
		minaClient.getProcesser().put(msg);
	}

	public void sessionOpened(IoSession session) throws Exception {
	}

	public void sessionCreated(IoSession session) throws Exception {
		if (session != null && !session.isClosing()) {
			log.debug("create a new connection : " + session.getRemoteAddress());
		} else {
			log.error("connection failure, cant not create connection!");
			return;
		}
	}

	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
		log.error("exceptionCaught : " + cause.getCause());
	}

	/**
	 * 断线后自动重连
	 */
	public void sessionClosed(IoSession session) throws Exception {
		minaClient.setServerIsRunning(false);
		minaClient.autoConnect();
		log.error("connection close : " + session.getRemoteAddress());
	}

	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		log.debug("connection idle : " + session.getRemoteAddress());
	}
}
