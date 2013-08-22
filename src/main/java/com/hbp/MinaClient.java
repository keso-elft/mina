package com.hbp;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hbp.handler.MinaHandler;
import com.hbp.message.MinaMessage;

/**
 * 客户端连接类
 */
public class MinaClient {
//
	protected Logger log = LoggerFactory.getLogger(MinaClient.class);

	private NioSocketConnector socketConnector = null;
	private IoSession ioSession = null;

	/**
	 * 服务器地址,格式为[ip:port]
	 */
	private String server;

	/**
	 * 服务器运行监测
	 */
	private boolean serverIsRunning = false;

	/**
	 * 上次重连时间
	 */
	private long checkServerTime;

	/**
	 * 是否创建一个可根据需要创建新线程的线程池
	 */
	private boolean isNewCachedThreadPool = false;

	/**
	 * 消息队列
	 */
	Map<Long, MinaMessage> waitQuence = new HashMap<Long, MinaMessage>();
	Map<Long, Long> waitLock = new HashMap<Long, Long>();

	/**
	 * 所有处理器
	 */
	Map<String, MinaHandler> handlerMap = new HashMap<String, MinaHandler>();

	/**
	 * 重连等待时间,1分钟
	 */
	private long connectPeriod = 60 * 1000;

	/**
	 * 打开连接的超时设置,10s
	 */
	private long connectTimeout = 10000; // 单位毫秒

	/**
	 * 同步发送时,超时设置,10s
	 */
	private long syncTimeOut = 10000;

	/**
	 * 其他各项配置
	 */
	String codec = "UTF-8";
	int corePoolSize = 10;
	int maximumPoolSize = 100;
	int blockQueueCapacity = 65535;
	long keepAliveTime = 60;

	/**
	 * 自动重连
	 */
	public void autoConnect() {
		Thread.currentThread().setName("MinaSession@" + server);
		log.info(Thread.currentThread().getName() + " start");

		while (true) {
			if (serverIsRunning)
				break;
			try {
				initConnect();
			} catch (Exception e) {
				log.error("reconnect failure!");
			}
		}
	}

	/**
	 * 建立连接,确保一段时间只重连一次(默认为1分钟)
	 */
	public void initConnect() {
		close();
		long now = System.currentTimeMillis();
		// 一分钟内，只重试一次连接
		if (now - checkServerTime > connectPeriod) {
			checkServerTime = now;
			if (connect(server)) {
				serverIsRunning = true;
			}
		}
	}

	/**
	 * 断开连接
	 */
	public void close() {
		if (socketConnector != null)
			socketConnector.dispose();
		socketConnector = null;
	}

	/**
	 * 建立连接, private防止直接调用
	 */
	private boolean connect(String server) {

		String[] hostport = server.replaceAll(" ", "").split(":");

		// 创建连接器
		socketConnector = new NioSocketConnector();
		socketConnector.setConnectTimeoutMillis(connectTimeout);
		InetSocketAddress isa = new InetSocketAddress(hostport[0],
				Integer.valueOf(hostport[1]));

		// 添加过滤器
		socketConnector.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(new TextLineCodecFactory(Charset
						.forName(codec), LineDelimiter.WINDOWS.getValue(),
						LineDelimiter.WINDOWS.getValue())));
		// socketConnector.getFilterChain().addLast("codec", new
		// ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		Executor executor = null;
		if (isNewCachedThreadPool == true) {
			executor = Executors.newCachedThreadPool();
		} else {
			executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
					keepAliveTime, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(blockQueueCapacity));
		}
		socketConnector.getFilterChain().addLast("threadPool-client",
				new ExecutorFilter(executor));

		// 添加业务处理适配器
		IoHandler handler = new MinaClientHandlerAdapter(this);
		socketConnector.setHandler(handler);

		// 连接
		ConnectFuture future = socketConnector.connect(isa);
		future.awaitUninterruptibly();
		if (!future.isConnected()) {
			socketConnector.dispose();
			socketConnector = null;
			log.error("NOT connect @ " + server);
			return false;
		}

		ioSession = future.getSession();
		log.debug("success connect @ " + server + ", session:"
				+ ioSession.getId());
		return true;
	}

	/**
	 * 异步发送
	 * 
	 * @param message
	 * @return
	 */
	public boolean send(MinaMessage message) {
		if (message == null)
			return false;

		// 如果session已关闭, 进行一次重连,为避免递归等待,只重试一次.
		if (ioSession == null || ioSession.isClosing()) {
			initConnect();
		}
		if (ioSession != null) {
			ioSession.write(message);
			return true;
		} else {
			log.error("--->!!! reconnect server fail@");
			return false;
		}
	}

	/**
	 * 同步发送
	 * 
	 * @param message
	 * @return
	 */
	public MinaMessage syncSend(MinaMessage message) {
		if (message == null)
			return null;
		MinaMessage ret = null;

		// 设定消息为同步类型消息
		message.setSync(true);

		// 设置同步发送时的waitQuenceId
		// 设置wait消息ID
		Long waitQuenceId = MinaMessage.nextId();
		message.setWaitQuenceId(waitQuenceId);
		synchronized (waitQuenceId) {
			try {
				waitLock.put(waitQuenceId, waitQuenceId);
				send(message);
				waitQuenceId.wait(syncTimeOut);// 等待返回消息
				// 解除同步发送时的waitQuenceId
				ret = waitQuence.get(waitQuenceId);
				waitQuence.remove(waitQuenceId);
				waitLock.remove(waitQuenceId);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public NioSocketConnector getSocketConnector() {
		return socketConnector;
	}

	public void setSocketConnector(NioSocketConnector socketConnector) {
		this.socketConnector = socketConnector;
	}

	public IoSession getIoSession() {
		return ioSession;
	}

	public void setIoSession(IoSession ioSession) {
		this.ioSession = ioSession;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public boolean isServerIsRunning() {
		return serverIsRunning;
	}

	public void setServerIsRunning(boolean serverIsRunning) {
		this.serverIsRunning = serverIsRunning;
	}

	public long getCheckServerTime() {
		return checkServerTime;
	}

	public void setCheckServerTime(long checkServerTime) {
		this.checkServerTime = checkServerTime;
	}

	public boolean isNewCachedThreadPool() {
		return isNewCachedThreadPool;
	}

	public void setNewCachedThreadPool(boolean isNewCachedThreadPool) {
		this.isNewCachedThreadPool = isNewCachedThreadPool;
	}

	public Map<Long, MinaMessage> getWaitQuence() {
		return waitQuence;
	}

	public void setWaitQuence(Map<Long, MinaMessage> waitQuence) {
		this.waitQuence = waitQuence;
	}

	public Map<Long, Long> getWaitLock() {
		return waitLock;
	}

	public void setWaitLock(Map<Long, Long> waitLock) {
		this.waitLock = waitLock;
	}

	public void addHandler(String cn, MinaHandler handler) {
		handlerMap.put(cn, handler);
	}

	public long getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public long getSyncTimeOut() {
		return syncTimeOut;
	}

	public void setSyncTimeOut(long syncTimeOut) {
		this.syncTimeOut = syncTimeOut;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public int getBlockQueueCapacity() {
		return blockQueueCapacity;
	}

	public void setBlockQueueCapacity(int blockQueueCapacity) {
		this.blockQueueCapacity = blockQueueCapacity;
	}

	public long getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public String getCodec() {
		return codec;
	}

	public void setCodec(String codec) {
		this.codec = codec;
	}

	public Map<String, MinaHandler> getHandlerMap() {
		return handlerMap;
	}

	public void setHandlerMap(Map<String, MinaHandler> handlerMap) {
		this.handlerMap = handlerMap;
	}

	public void setConnectPeriod(long connectPeriod) {
		this.connectPeriod = connectPeriod;
	}

	public long getConnectPeriod() {
		return connectPeriod;
	}
}
