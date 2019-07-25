package com.xier.lora.server.source;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import com.xier.lora.gateway.dto.GatewayData;
import com.xier.lora.pub.service.impl.LoraPubServiceImpl;
import com.xier.lora.util.PropertiesUtil;
import com.xier.lora.util.StringUtils;
import com.xier.lorawan.util.HexUtil;

@Repository("loraServerSource")
public class LoraServerSource{
	private static final Logger log = LoggerFactory.getLogger(LoraServerSource.class);
	/**
	 * UDP服务开放端口
	 */
	private static final int SERVER_PORT = 8888;
	/**
	 * 接收消息socket
	 */
	private static DatagramSocket server;
	/**
	 * 下行消息socket
	 */
	private static DatagramSocket clientServer;

	/**
	 * 下行消息开放端口   发送的同时需要接收下行的ack
	 */
	private static final int CLIENT_PORT = 888;
	/**
	 * 上行消息的ack开放端口 专门用来做上行的ack
	 */
	private static final int ACK_CLIENT_PORT = 889;
	
	/**
	 * 上行消息的ack socket
	 */
	private static DatagramSocket ackClientServer;
	
	private static ExecutorService   udpServerExecutor = Executors.newSingleThreadExecutor();
	
	private static Properties properties;
	private static MqttClient mqttClient;
	public static final String TOPIC = "com/hikvision/loraServer";
	static{
		if(ackClientServer == null){
			   try {
				   ackClientServer = new DatagramSocket(ACK_CLIENT_PORT);
            } catch (SocketException e) {
            	log.error("create ackClientServer socket [" + ACK_CLIENT_PORT + "] error",e);
            }
		}
	}
	
	@Scheduled(fixedRate = 3 * 60 * 1000)
	public synchronized void subscription() {
		//udp服务重连
		if (server == null || server.isClosed()) {
			initUDPServer();
		}
		//udp客户端重连
		if (ackClientServer == null || ackClientServer.isClosed()) {
			try {
				ackClientServer = new DatagramSocket(ACK_CLIENT_PORT);
			} catch (SocketException e) {
				log.error("create ackClientServer socket [" + ACK_CLIENT_PORT + "] error", e);
			}
		}
		//mqtt客户端重连
		if (mqttClient == null || !mqttClient.isConnected()) {
			connectMqttBroker();
		}
	}


	@PostConstruct
	public void initUDPServer(){
		udpServerExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
			        server = new DatagramSocket(SERVER_PORT);
			        byte[] container=new byte[1024];
			        DatagramPacket dp=new DatagramPacket(container, container.length);
			        while(true){
			        	System.out.println("连接成功 开始接收消息 : ");
			            server.receive(dp);
			            //分析数据
			             byte[] data=dp.getData();
			             int len=dp.getLength();
			             //LoraPubServiceImpl.notifyQueue.put(new GatewayData(dp.getAddress().getHostAddress(),dp.getPort(),new String(data).substring(0,len).getBytes("utf-8")));      
			             LoraPubServiceImpl.notifyQueue.put(new GatewayData(dp.getAddress().getHostAddress(),dp.getPort(),HexUtil.hexStringToBytes(HexUtil.bytesToHexString(data).substring(0,len*2)))); 
			        }
		        } catch (SocketException e) {
		        	log.error("create socket error :",e);
		        } catch (IOException e) {
		        	log.error("connect error :",e);
		        } catch (InterruptedException e) {
		        	log.warn("notifyQueue is full ,is waitting");
		        }
			}
		});
		connectMqttBroker();
	}

	/**
	 * 不考虑ack的消息下发
	 * @author lvhui5 2018年3月13日 上午11:17:02
	 * @param msg
	 * @param address
	 * @param port
	 * @return
	 */
	public static boolean sendDirectMsg(byte[] msg,String address,int port){
		if (ackClientServer == null || ackClientServer.isClosed()) {
			try {
				ackClientServer = new DatagramSocket(ACK_CLIENT_PORT);
			} catch (SocketException e) {
				log.error("create ackClientServer socket [" + ACK_CLIENT_PORT + "] error", e);
				return false;
			}
		}
		DatagramPacket dp=new DatagramPacket(msg, msg.length,new InetSocketAddress(address, port));
		if (log.isDebugEnabled()) {
			try {
	            log.debug("port :" + port + " sendAckMsg :" + new String(msg,"utf-8"));
            } catch (UnsupportedEncodingException e) {
            	log.error("error", e);
            }
		}
		try {
			ackClientServer.send(dp);
        } catch (IOException e) {
			log.error("sendAckMsg error", e);
			return false;
        }
		return true;
	}
	
	
	private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	private static WriteLock writeLock = lock.writeLock();
	
	private static ReadLock readLock = lock.readLock();
	
	/**
	 * clientServer阻塞
	 */
	private static boolean clientServerBlock = false;
	/**
	 * 打开下行端口ack数据接收服务(主要用于下行消息发布端口与下行ack接收端口相同的情况)
	 * 服务启动期间  消息下发请求等待
	 * 需要考虑动态分配下行端口
	 * @author lvhui5 2018年2月27日 下午12:00:49
	 */
	public static void openCilentUDPServer(){	
		writeLock.lock();
		clientServerBlock= true;
		try {
			/*if(clientServer!= null && !clientServer.isClosed()){
				return;
			}*/
			try {
				if (clientServer == null || clientServer.isClosed()) {
					clientServer = new DatagramSocket(CLIENT_PORT);
				}	
				//clientServer = new DatagramSocket(CLIENT_PORT);
		        byte[] container=new byte[1024];
		        DatagramPacket dp=new DatagramPacket(container, container.length);
                for(;;){
                	System.out.println("CLIENT_PORT 连接成功 开始接收消息 : ");
                	try {
                        clientServer.receive(dp);
                    } catch (SocketException e) {
                    	if (log.isDebugEnabled()) {
                			log.debug("clientServer socket close :" + e.getMessage());
                		}
                    	break;
                    } catch (IOException e) {
                    	log.error("connect error :",e);
                    	break;
                    }
                    //分析数据
	                byte[] data=dp.getData();
	                int len=dp.getLength();
	                try {
	                    LoraPubServiceImpl.notifyQueue.put(new GatewayData(dp.getAddress().getHostAddress(),dp.getPort(),HexUtil.hexStringToBytes(HexUtil.bytesToHexString(data).substring(0,len*2))));
                    } catch (InterruptedException e) {
                    	log.warn("notifyQueue is full ,is waitting");
                    } 
                }
	        } catch (IOException e) {
	        	log.error("connect error :",e);	
	        }
		} finally{
			clientServerBlock = false;
			writeLock.unlock();
        }
	}

	/**
	 * 关闭下行端口ack数据接收服务(主要用于下行消息发布端口与下行ack接收端口相同的情况)
	 * @author lvhui5 2018年2月27日 下午12:01:14
	 */
	public static void closeCilentUDPServer(){
		if(clientServerBlock && clientServer != null && !clientServer.isClosed())
			clientServer.close();
	}
	
	private void connectMqttBroker(){
		properties = PropertiesUtil.getProperties("application.properties");
		String proxyHost = properties.getProperty("mqtt.proxy.host");
		String proxyUserName = properties.getProperty("mqtt.userName");
		String decodePassword = properties.getProperty("mqtt.password");
		if(StringUtils.isMoreBlank(proxyHost,proxyUserName,decodePassword)){
        	log.error("mqtt connect param is null");
			return;
		}
		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(false);
		options.setUserName(proxyUserName);
		options.setPassword(decodePassword.toCharArray());
		// 设置超时时间
		options.setConnectionTimeout(10);
		// 设置会话心跳时间
		options.setKeepAliveInterval(5);
		try {
			mqttClient = new MqttClient(proxyHost, "loraServer", new MemoryPersistence());
			mqttClient.setCallback(new PushCallback());
			mqttClient.connect(options);
			if (log.isDebugEnabled()) {
				log.debug("mqtt client connect success");
			}
		} catch (Exception e) {
        	log.error("mqttClient connect [" + proxyHost + "] error :",e);
		}
	}

	/**
	 * 下行消息发布(包括重发)
	 * 用于发送端口接收ack的情况
	 * @author lvhui5 2018年3月5日 上午11:02:33
	 * @param msg
	 * @param address
	 * @param port
	 * @return
	 */
	public static boolean sendMsg(byte[] msg,String address,int port){
		readLock.lock();
		try{
			synchronized(lock){
				if (clientServer == null || clientServer.isClosed()) {
					try {
						clientServer = new DatagramSocket(CLIENT_PORT);
					} catch (SocketException e) {
						log.error("create clien socket [" + CLIENT_PORT + "] error", e);
						return false;
					}
				}
			}
			DatagramPacket dp=new DatagramPacket(msg, msg.length,new InetSocketAddress(address, port));
			if (log.isDebugEnabled()) {
				try {
		            log.debug("port :" + port + " sendMsg :" + new String(msg,"utf-8"));
	            } catch (UnsupportedEncodingException e) {
	            	log.error("error", e);
	            }
			}
			try {
				clientServer.send(dp);
	        } catch (IOException e) {
				log.error("sendMsg error", e);
				return false;
	        }
		}finally{
			readLock.unlock();
			//clientServer.close();
		}
		return true;
	}
	
	public static void sendMsgToMqttBroker(byte[] msg){
		if(mqttClient == null){
			log.warn("mqttClient is null");
			return;
		}
		//往mqtt代理发布消息
		MqttMessage mqttMessage = new MqttMessage();
		mqttMessage.setQos(2);
		mqttMessage.setRetained(true);
		mqttMessage.setPayload(msg);
		MqttTopic topic = mqttClient.getTopic(TOPIC);
		try {
	        topic.publish(mqttMessage);
        } catch (MqttPersistenceException e) {
			log.warn("sendMsgToMqttBroker error :"+ e.getMessage());
        } catch (MqttException e) {
			log.warn("sendMsgToMqttBroker error :"+ e.getMessage());
        }
	}

}
