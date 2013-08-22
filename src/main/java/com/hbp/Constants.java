package com.hbp;

public class Constants {

	// 接收结果状态码
	public final static int QN_SUCCESS = 1;
	public final static int QN_DENY = 2;
	public final static int QN_WRONG_PASS = 3;

	// 执行结果状态码
	public final static int EXE_SUCCESS = 1;
	public final static int EXE_FAIL = 2;
	public final static int EXE_NO_DATA = 3;

	public final static String[] NOTIFICATION_CN = { "2012", "2022" };

	// 收到立即回复消息格式
	public final static String REPLY_REQUEST_PATTERN = "ST=91;CN=9011;PW=%s;MN=%s;Flag=0;CP=&&QN=%s;QnRtn=%d&&";
	public final static String REPLY_NOTIFICATION_PATTERN = "ST=91;CN=9013;PW=%s;MN=%s;CP=&&QN=%s&&";
	public final static String REPLY_DATA_REPLY_PATTERN = "ST=91;CN=9012;PW=%s;MN=%s;CP=&&QN=%s;ExeRtn=%d&&";

	// 收到处理后回复消息格式
	public final static String UPLOAD_RTN_PATTERN = "ST=32;CN=%s;PW=%s;MN=%s;CP=&&%s&&";
	public final static String EXE_RTN_PATTERN = "ST=91;CN=9012;PW=%s;MN=%s;CP=&&%s&&";

}
