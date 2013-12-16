package com.hbp.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hbp.MinaClient;
import com.hbp.dao.MsgToSendDao;
import com.hbp.model.MsgToSend;
import com.hbp.util.ClientUtil;

public class DataReportTask {

	protected Logger log = LoggerFactory.getLogger(DataReportTask.class);

	private MsgToSendDao msgToSendDao;

	public void run() {

		@SuppressWarnings("unchecked")
		List<MsgToSend> msgList = msgToSendDao.getMsgToSend();
		if (msgList != null) {

			log.info("new msg count: " + msgList.size());
			int count = 0;

			for (MsgToSend msg : msgList) {
				MinaClient client = ClientUtil.getClient(msg.getAddress());
				if (client != null) {
					if (client.send(msg.getMsg())) {
						msgToSendDao.delete(msg);
						count++;
						log.debug("send msg [" + msg.getMsg() + "] success @ " + msg.getAddress());
					}
				} else {
					log.error("address not found @" + msg.getAddress());
				}
			}
			log.info("send msg count: " + count);
		}
	}

	public void setMsgToSendDao(MsgToSendDao msgToSendDao) {
		this.msgToSendDao = msgToSendDao;
	}

	public MsgToSendDao getMsgToSendDao() {
		return msgToSendDao;
	}

}
