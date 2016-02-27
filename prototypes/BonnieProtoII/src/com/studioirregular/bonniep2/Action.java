package com.studioirregular.bonniep2;

public class Action {

	public String className;
	public String methodName;
	
	public Action(String clazz, String method) {
		className = clazz;
		methodName = method;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + " className:" + className + ",methodName:" + methodName;
	}
	
}
