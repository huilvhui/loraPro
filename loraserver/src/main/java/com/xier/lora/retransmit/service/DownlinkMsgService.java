package com.xier.lora.retransmit.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xier.lora.server.source.LoraServerSource;
import com.xier.lora.sys.util.StringUtils;

/**
 * 下行消息处理
 * <p>
 * 维护网关下行消息ack(并非节点的ack)
 * 定时扫描过期 下行消息重发
 * </p>
 * @author lvhui5 2018年2月27日 上午11:28:54
 * @version V1.0
 */
@Service("downlinkMsgService")
public class DownlinkMsgService{
	/**
	 * 最大维护ack窗口数量
	 */
	private static final int maximumSize = 50;
	
	private static final ReentrantLock lock = new ReentrantLock();
	
	private static final Logger logger = LoggerFactory.getLogger(DownlinkMsgService.class);

	@Autowired
	private TaskExecutor taskExecutor; // 异步线程池
	/**
	 * gatewayid_token:window
	 */
	private static final Map<String, ReceiveWindow> token_window = new  LinkedHashMap<String, ReceiveWindow>(){
		/**
         * 序列化ID
         */
        private static final long serialVersionUID = 5867353004958753969L;
		@Override
        protected boolean removeEldestEntry(Entry<String, ReceiveWindow> eldest) {
			return size() > maximumSize;
        }    
    };
	/**
	 * 重发任务执行
	 * 防止因为一次下发没收到ack而出现部分下发消息延时
	 * @author lvhui5 2018年2月27日 上午11:43:25
	 */
    @Scheduled(fixedRate = 200)
	public synchronized final void task(){
    	//logger.debug("============================================" + token_window.size());
    	//假如当前没有窗口停止下行端口服务监听
    	lock.lock();
		try{
	    	if(token_window.isEmpty()){
	    		//可能存在关闭阻塞执行完 才开启阻塞的情况
	    		LoraServerSource.closeCilentUDPServer();
				return;
			}
		}finally{
			lock.unlock();	
		}
		for(String key : token_window.keySet()){
			ReceiveWindow receiveWindow = token_window.get(key);
			if(receiveWindow == null){
				continue;
			}
			if(receiveWindow.needRetransmit()){
				//重发不关心结果
				taskExecutor.execute(new Runnable() {
					@Override
					public void run() {
						if (logger.isDebugEnabled()) {
							logger.debug("retransmit token :[{}],gatewayId:[{}]",new Object[]{receiveWindow.getToken(),receiveWindow.getGatewayId()});
						}
						//if(token_window.containsKey(key)){
							//可能刚收到就重发了
							LoraServerSource.sendMsg(receiveWindow.getMsg(), receiveWindow.getAddress(), receiveWindow.getPort());	
						//}
					}
				});
				if(receiveWindow.windowEnd()){
					lock.lock();
					try{
						if(!token_window.containsKey(key)){
							continue;
						}
						//重发完成后移除
						token_window.remove(key);
						if(!token_window.isEmpty()){
							continue;
						}
					}finally{
						lock.unlock();	
					}
					LoraServerSource.closeCilentUDPServer();
					return;
				}
			}
		}
	}
	
    private static ExecutorService  addDownLinkWindowExecutor = Executors.newSingleThreadExecutor();
	public static void addDownLinkWindow(byte[] msg, String address, int port, String token,String gatewayId){
		if(StringUtils.isMoreBlank(address,token,gatewayId)){
			return;
		}
		lock.lock();
		try{
			token_window.put(gatewayId+ "_" +token, new ReceiveWindow(msg, address, port, token,gatewayId));
			if(token_window.size() != 1){
				return;
			}
		}finally{
			lock.unlock();	
		}
		//改成异步 openCilentUDPServer方法内部阻塞
		addDownLinkWindowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				LoraServerSource.openCilentUDPServer();
			}
		});
	}
	
	/**
	 * 接收下行ack处理
	 * @author lvhui5 2018年2月27日 上午11:44:57
	 * @param token
	 */
	public static void receiveDownLinkAck(String gatewayId,String token){
		String key = gatewayId+ "_" +token;
		if(!token_window.containsKey(key)){
			return;
		}
		lock.lock();
		try{
			token_window.remove(key);
			//假如当前没有窗口 停止下行端口监听
			if(!token_window.isEmpty()){
				return;
			}
		}finally{
			lock.unlock();	
		}
		//正常情况下当前为阻塞状态
		LoraServerSource.closeCilentUDPServer();
	}
	

}
