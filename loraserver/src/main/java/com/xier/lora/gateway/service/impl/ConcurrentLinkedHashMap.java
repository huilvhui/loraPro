package com.xier.lora.gateway.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对HashMap做线程控制
 * <p>
 * LinkedHashMap更新添加用hashMap的方法线程不安全
 * 另外增加重复添加场景的线程安全性
 * </p>
 * @author lvhui5 2017年12月20日 下午4:30:03
 * @version V1.0
 */
public class ConcurrentLinkedHashMap<K,V>  extends HashMap<K,V>{

	private Logger log = LoggerFactory.getLogger(ConcurrentLinkedHashMap.class);
	/**
     * 序列化ID
     */
    private static final long serialVersionUID = -522551950118470530L;

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	private final ReadLock readLock = lock.readLock();
	
	private final WriteLock writeLock = lock.writeLock();
    /**
     * 新生代阈值
     */
	private final int maxCapacity;
	/**
	 * 老年代
	 */
	private final Map<K, V> longterm = new WeakHashMap<K, V>();
	/**
	 * 用于索引map值
	 */
	private final List<K> index = new ArrayList<K>();
	
	public ConcurrentLinkedHashMap(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
		V v;
		writeLock.lock();
		try{
			v = super.get(key);
			if (v == null) {	
				v = this.longterm.get(key);
				if (v != null) {
					super.put((K)key, v);
					longterm.remove(key);
				}	
			} 
		}finally{
			writeLock.unlock();	
		}
		if(log.isDebugEnabled()){
			log.debug("get key [{}],value is:{}",new Object[]{key,String.valueOf(v)});
		}
		return v;
    }

	@Override
    public Collection<V> values() {
		Set<V> values = new HashSet<V>();
		readLock.lock();
		try{
			values.addAll(super.values());
			values.addAll(longterm.values());
		}finally{
			readLock.unlock();	
		}
		return values;
    }

	@Override
    public boolean isEmpty() {
		readLock.lock();
		try{
			return super.isEmpty() && this.longterm.isEmpty();
		}finally{
			readLock.unlock();	
		}
    }

	@Override
	public V put(K key, V value) {
		V result;
		writeLock.lock();
		try{
			if(super.get(key) != null){
				return super.get(key);
			}
			if(longterm.get(key) != null){
				return longterm.get(key);
			}
			if (super.size() >= maxCapacity) {
				this.longterm.putAll(this);	
				super.clear();	
			}
			result = super.put(key, value);
			if(!index.contains(key)){
				this.index.add(key);
			}	
		}finally{
			writeLock.unlock();	
		}
		if(log.isDebugEnabled()){
			log.debug("get key [{}],value is:{}",new Object[]{key,String.valueOf(value)});
		}
		return result;
    }
	


	@Override
    public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException(
				"Unsupported putAll method");
    }
	@Override
    public V putIfAbsent(K key, V value) {
		throw new UnsupportedOperationException(
				"Unsupported putIfAbsent method");
    }

	@Override
    public V remove(Object key) {
		if(log.isDebugEnabled()){
			log.debug("remove key [{}]",new Object[]{key});
		}
		writeLock.lock();
		try{
			this.longterm.remove(key);
			V result = super.remove(key);
			index.remove(key);
			return result;
		}finally{
			writeLock.unlock();	
		}
    }
	

	public void update(K key,V value) {
		writeLock.lock();
		try{
			if(super.get(key) != null){
				super.put(key, value);
			}
			if(this.longterm.get(key) != null){
				this.longterm.put(key, value);
			}
		}finally{
			writeLock.unlock();	
		}
    }
	/**
	 * 根据索引查询值
	 * @author lvhui5 2017年12月27日 下午6:44:28
	 * @param i
	 * @return
	 */
	public V get(int i){
		readLock.lock();
		try{
			if(i >= index.size())
				return null;
			K key = index.get(i);
			V v = super.get(key);
			if (v == null) {	
				v = this.longterm.get(key);	
			}
			return v;
		}finally{
			readLock.unlock();	
		}
	}

	@Override
    public void clear() {
		writeLock.lock();
		try{
			super.clear();
			this.longterm.clear();
			this.index.clear();
		}finally{
			writeLock.unlock();	
		}
    }
	
	
	@Override
    public int size() {
	    if(super.size() + this.longterm.size() != index.size()){
	    	 throw new IndexOutOfBoundsException("index out of  mapsize");
	    }
	    return index.size();
    }
	
	public static void main(String[] args){
		List<String> test = new ArrayList<String>();
		System.out.println(test.get(10));
	}
	
}
