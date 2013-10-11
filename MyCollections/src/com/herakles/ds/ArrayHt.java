package com.herakles.ds;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ArrayHt<K,V> implements Ht{

	private class NodeObject<X, Y> {
		private X key;
		private Y value;
		NodeObject<X, Y> next = null;

		NodeObject (X k, Y v) {
			this.key = k;
			this.value = v;
		}
		public X getKey() {
			return this.key;
		}
		public Y getValue() {
			return this.value;
		}
	}
	//--------------------

	private NodeObject<K,V>[] bucket;
	private int size, resizeLimit;
	private double resizeFactor;
	private AtomicInteger numElements = new AtomicInteger(0);
	private Object lock = "Lock";
	private Object resizingLock = "resizingLock";
	private boolean messingInternals = false;

	ArrayHt() {
		this(10, 0.75);
	}
	
	ArrayHt(int sz) {
		this(sz, 0.75);
	}
	
	ArrayHt(int sz, double limit) {
		this.size = sz;
		this.resizeFactor=limit;
		setResizeLimit(limit);
		this.bucket = (NodeObject<K, V>[]) new NodeObject[sz];
		//NodeObject<K,V> t = null;
		//this.bucket = (NodeObject<K, V>[]) Array.newInstance(t.getClass().getComponentType(), sz);
	}
	
	private int locateBucket(int hashcode) {
		return hashcode % bucket.length;
	}

	private void setResizeLimit(double v) {
		resizeLimit = (int)(v*size);
		System.out.println("reziseLimit:"+resizeLimit);
	}

	public <K, V> V get (K key) {
		int i = locateBucket(key.hashCode());

		for (NodeObject<K,V> tmp =  (NodeObject<K, V>) bucket[i]; tmp!=null; tmp = tmp.next) {
			if (tmp.getKey().equals(key)) return tmp.getValue();
		}
		return null;
	}

	public <K, V> void put(K key, V value) {
		if (messingInternals) {
			synchronized(resizingLock) { // wait till we are done messing the internal structure
				messingInternals = false;// noop;
			}
		}
		boolean populated = false;
		int i = locateBucket(key.hashCode());
		if (bucket[i]==null){
			synchronized (lock) {
				if (bucket[i]==null) {
					bucket[i] = new NodeObject(key, value);
					populated = true;
				}
			}
		} 
		if ((!populated)&&(bucket[i]!=null)) {
			synchronized (bucket[i]) {
				NodeObject<K,V> tmp = (NodeObject<K, V>) bucket[i];
				while (tmp.next!=null) tmp = tmp.next;
				tmp = new NodeObject(key, value);
			}
		}
		numElements.incrementAndGet();
		if (numElements.intValue()>resizeLimit) resizeHt();
	}

	public <K> void remove(K key) {
		if (messingInternals) {
			synchronized(resizingLock) { // wait till we are done messing the internal structure
				messingInternals = false;// noop;
			}
		}
		
		int i = locateBucket(key.hashCode());
		NodeObject<K, V> prev = null;
		NodeObject<K, V> tmp = (NodeObject<K, V>) bucket[i];
		for (; tmp!=null; tmp = tmp.next) {
			synchronized (bucket[i]) {
				if (tmp.getKey().equals(key)) {
					if (prev != null) {
						prev.next = tmp.next;
					}
					tmp = null;
					numElements.decrementAndGet();
					return;
				}
				prev = tmp;
			}
		}
	}
	
	private void resizeHt () {
		synchronized(resizingLock) {
			messingInternals = true;
		System.out.println("Resizing...");
		ArrayHt<K, V> newHt = new ArrayHt<K, V>(size*2, resizeFactor);
		for(int i=0; i<bucket.length;i++){
			NodeObject<K, V> tmp = bucket[i];
			while (tmp!=null) {
				newHt.put(tmp.getKey(), tmp.getValue());
				tmp=tmp.next;
			}
		}
		this.bucket = newHt.bucket;
		this.size =  newHt.size;
		this.resizeLimit = newHt.resizeLimit;
		this.numElements = newHt.numElements;
//		this.resizeFactor = newHt.resizeFactor;
		}
		messingInternals = false;
	}

	public int getSize() {
		return numElements.get();
	}
}