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

		log.info("DataReportTask run ...");

		@SuppressWarnings("unchecked")
		List<MsgToSend> msgList = msgToSendDao.getMsgToSend();
		if (msgList != null) {
			for (MsgToSend msg : msgList) {
				MinaClient client = ClientUtil.getClient(msg.getAddress());
				if (client != null && client.send(msg.getMsg())) {
					msgToSendDao.delete(msg);
				}
			}
		}

		log.info("DataReportTask finish ...");
	}

	public void setMsgToSendDao(MsgToSendDao msgToSendDao) {
		this.msgToSendDao = msgToSendDao;
	}

	public MsgToSendDao getMsgToSendDao() {
		return msgToSendDao;
	}

}
