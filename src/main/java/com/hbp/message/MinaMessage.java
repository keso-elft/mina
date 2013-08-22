package com.hbp.message;

import java.io.Serializable;
import java.util.LinkedHashMap;
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
	 * 是否是请求
	 */
	private boolean isRequest = false;
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
		if (getQN() != null)
			setRequest(true);
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

	public void setRequest(boolean isRequest) {
		this.isRequest = isRequest;
	}

	public boolean isRequest() {
		return isRequest;
	}

	public String getValue(String key) {
		return (String) pto.get(key);
	}

	public void setValue(String key, String value) {
		pto.put(key, value);
	}

	public static void main(String[] args) {
		String msg = "QN=20040516010101001;ST=32;CN=1072;PW=123456;MN=8888888880000001;Flag=3;CP=&&PW=654321&&";
		System.out.print(new MinaMessage(msg));
	}
}
