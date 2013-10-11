package com.herakles.ds;

public interface Ht {
	<K, V> V get(K key);
	<K,V> void put (K key, V value);
	<K> void remove(K key);
	int getSize();
}
