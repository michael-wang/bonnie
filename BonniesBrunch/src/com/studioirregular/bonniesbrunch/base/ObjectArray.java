package com.studioirregular.bonniesbrunch.base;

public class ObjectArray<T> {

	public ObjectArray(int capacity) {
		assert capacity > 0;
		this.capacity = capacity;
		this.count = 0;
		objects = (T[])new Object[capacity];
	}
	
	@SuppressWarnings("serial")
	public static class OutOfCapacityException extends Exception {
		public OutOfCapacityException(String msg) {
			super(msg);
		}
	}
	
	public void add(T obj) throws OutOfCapacityException {
		if (count >= capacity) {
			throw new OutOfCapacityException("out of capacity:" + capacity);
		}
		
		objects[count] = obj;
		count++;
	}
	
	public int size() {
		return count;
	}
	
	public T get(int index) throws IndexOutOfBoundsException {
		if (index < 0 || count <= index) {
			throw new IndexOutOfBoundsException("index:" + index + " out of range: 0 - " + (count-1));
		}
		
		return objects[index];
	}
	
	public void clear() {
		count = 0;
	}
	
	public T getLast() {
		assert count > 0;
		return objects[count-1];
	}
	
	public void removeLast() {
		assert count > 0;
		count--;
	}
	
	private T[] objects;
	private int capacity;
	private int count;
}
