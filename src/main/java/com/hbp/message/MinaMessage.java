package com.hbp.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MinaMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private static AtomicLong uniqueId = new AtomicLong(0);
	/**
	 * 是否是同步消息,可不设置,在调用send函数会自动设置
	 */
	private boolean isSync = false;
	/**
	 * 用于同步通知的ID,需要全局唯一,在调用同步send会自动赋值.
	 */
	private Long waitQuenceId;
	/**
	 * 命令类型,用于区分处理的handler
	 */
	private String cn;
	/**
	 * 消息ID,为请求消息的流水号
	 */
	private String qn;

	private Map<String, String> pto = new HashMap<String, String>();

	/**
	 * 构造器
	 * 
	 * @param msg
	 */
	public MinaMessage(String msg) {
		super();
		// TODO build 先随便写写
		if (msg.contains("CN")) {
			cn = msg.substring(msg.indexOf("CN") + 3, msg.indexOf("CN") + 7);
		}
	}

	/**
	 * uniqueId的生成器
	 */
	public static long nextId() {
		return uniqueId.incrementAndGet();
	}

	/**
	 * 输出为String
	 */
	public String toString() {
		// TODO parse to String
		return String.format("Message[cn=%s, qn=%s, waitQuenceId=%s]", cn, qn,
				waitQuenceId);
	}

	public boolean isSync() {
		return isSync;
	}

	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}

	public Long getWaitQuenceId() {
		return waitQuenceId;
	}

	public void setWaitQuenceId(Long waitQuenceId) {
		this.waitQuenceId = waitQuenceId;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getQn() {
		return qn;
	}

	public void setQn(String qn) {
		this.qn = qn;
	}

	public Map<String, String> getPto() {
		return pto;
	}

	public void setPto(Map<String, String> pto) {
		this.pto = pto;
	}
}
