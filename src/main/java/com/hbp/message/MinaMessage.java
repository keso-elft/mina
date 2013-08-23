package com.hbp.message;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.hbp.Constants;

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
	 * 结果码
	 */
	private int resultCode = 0;
	/**
	 * 收到消息类型,1:请求,2:通知,3:数据应答
	 */
	private int type = 0;
	public final static int TYPE_REQUEST = 1;
	public final static int TYPE_NOTIFICATION = 2;
	public final static int TYPE_DATA_REPLY = 3;

	/**
	 * 消息参数
	 */
	private Map<String, String> pto = new LinkedHashMap<String, String>();
	private String CP;
	private Map<String, String> cpParaMap;

	/**
	 * 构造器
	 * 
	 * @param msg
	 */
	public MinaMessage(String msg) {
		super();

		if (msg.matches("^(.*=.*;)+CP=&&.*&&$")) {
			String[] paraAndCp = msg.split("&&");
			String para = paraAndCp[0];
			if (paraAndCp.length > 1)
				this.CP = paraAndCp[1];

			String[] keyAndValuePair = para.split(";");
			for (String keyAndValue : keyAndValuePair) {
				if (keyAndValue.matches("^(QN|ST|CN|PW|MN|Flag)=[0-9]{1,}$")) {
					String key = keyAndValue.substring(0, keyAndValue.indexOf("="));
					String value = keyAndValue.substring(keyAndValue.indexOf("=") + 1);
					pto.put(key, value);
				}
			}
		}
		initMsgType(msg);
	}

	/**
	 * TODO 分析消息类型(简略实现)
	 * 
	 * @param msg
	 */
	private void initMsgType(String msg) {
		if (getQN() != null && getCN() != null) {
			type = TYPE_REQUEST;
			for (String cn : Constants.NOTIFICATION_CN) {
				if (getCN().equals(cn)) {
					type = TYPE_NOTIFICATION;
				}
			}
		} else if (getCN() != null && getCN().equals(Constants.DATA_REPLY_CN)) {
			type = TYPE_DATA_REPLY;
		}
	}

	/**
	 * uniqueId的生成器
	 */
	public static long nextId() {
		return uniqueId.incrementAndGet();
	}

	/**
	 * 获取CP中的参数集
	 * 
	 * @param key
	 * @return
	 */
	public String getCpPara(String paraName) {
		if (cpParaMap == null)
			initCpParaMap();
		return cpParaMap.get(paraName);
	}

	/**
	 * 创建CP参数集
	 */
	private void initCpParaMap() {
		cpParaMap = new LinkedHashMap<String, String>();
		if (CP != null && CP.length() > 0) {
			String[] cpParas = CP.split(";");
			for (String cpPara : cpParas) {
				if (cpPara.matches("^.*=.*$")) {
					String key = cpPara.substring(0, cpPara.indexOf("="));
					String value = cpPara.substring(cpPara.indexOf("=") + 1);
					cpParaMap.put(key, value);
				}
			}
		}
	}

	/**
	 * 输出为String
	 */
	public String toString() {
		StringBuffer output = new StringBuffer();
		for (String key : pto.keySet()) {
			output.append(key + "=" + pto.get(key) + ";");
		}
		output.append("CP=&&" + (CP != null ? CP : "") + "&&");
		return output.toString();
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

	/**
	 * 请求ID,为请求消息的流水号，和后续对应
	 */
	public String getQN() {
		return (String) pto.get("QN");
	}

	/**
	 * 命令类型,用于区分处理的handler
	 */
	public String getCN() {
		return (String) pto.get("CN");
	}

	public String getST() {
		return (String) pto.get("ST");
	}

	public String getPW() {
		return (String) pto.get("PW");
	}

	public String getMN() {
		return (String) pto.get("MN");
	}

	public String getFLAG() {
		return (String) pto.get("FLAG");
	}

	public String getCP() {
		return CP;
	}

	public void setCP(String CP) {
		this.CP = CP;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public boolean isRequest() {
		return type == TYPE_REQUEST;
	}

	public boolean isNotification() {
		return type == TYPE_NOTIFICATION;
	}

	public boolean isDataReply() {
		return type == TYPE_DATA_REPLY;
	}

	public boolean isSuccess() {
		return resultCode == Constants.QN_SUCCESS;
	}

	public String getValue(String key) {
		return (String) pto.get(key);
	}

	public void setValue(String key, String value) {
		pto.put(key, value);
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public int getResultCode() {
		return resultCode;
	}

	public static void main(String[] args) {
		String msg = "QN=20040516010101001;ST=32;CN=1072;PW=123456;MN=8888888880000001;Flag=3;CP=&&PW=654321&&";
		MinaMessage message = new MinaMessage(msg);
		System.out.print(message);
		System.out.print(message.getType());
	}
}
