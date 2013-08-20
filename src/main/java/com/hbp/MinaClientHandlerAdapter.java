package com.hbp;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hbp.handler.MinaHandler;
import com.hbp.message.MinaMessage;

public class MinaClientHandlerAdapter extends IoHandlerAdapter {

	protected Logger log = LoggerFactory
			.getLogger(MinaClientHandlerAdapter.class);

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
				MinaMessage resp = this.dispatchMsg(msg);
				if (resp != null && session.isConnected()) {
					session.write(resp);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("client message Received exception : "
						+ e.getMessage() + e.getCause());
			}
		}
	}

	/**
	 * 分发消息
	 * 
	 * @param msg
	 * @return
	 */
	public MinaMessage dispatchMsg(MinaMessage msg) {
		MinaHandler handler = minaClient.getHandlerMap().get(msg.getCn());
		MinaMessage respMsg = null;
		if (handler != null) {
			respMsg = handler.handleMsg(msg);
		}

		if (msg.isSync() && msg.getWaitQuenceId() != null) {

			Long waitQuenceId = minaClient.waitLock.get(msg.getWaitQuenceId());
			if (waitQuenceId != null) {
				synchronized (waitQuenceId) {
					// 把返回结果放入queue中,供client进行查找
					minaClient.waitQuence.put(waitQuenceId, msg);
					waitQuenceId.notifyAll();
				}
			}
		}

		return respMsg;
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

	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		log.error("exceptionCaught : " + cause.getCause());
	}

	public void sessionClosed(IoSession session) throws Exception {
		minaClient.setServerIsRunning(false);
		minaClient.autoConnect();
		log.info("connection close : " + session.getRemoteAddress());
	}

	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		log.debug("connection idle : " + session.getRemoteAddress());
	}
}
