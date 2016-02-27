package com.studioirregular.bonnie.timingsystem;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class TimingSystem {

	private static final boolean DO_LOG = false;
	private static final String TAG = "time-system";
	
	public interface Callback {
		public void onTimeup(Object obj);
	}
	
	public void start() {
		running = true;
		elapsedTime = 0;
	}
	
	public void stop() {
		running = false;
	}
	
	public long getElapsedTime() {
		return elapsedTime;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public synchronized void schedule(Callback callback, Object obj, final long time) {
		if (DO_LOG) Log.d(TAG, "schedule callback:" + callback + ", time:" + time + ", elapsedTime:" + elapsedTime);
		
		ClientRequest request = new ClientRequest();
		request.callback = callback;
		request.obj = obj;
		request.time = time;
		request.startTime = elapsedTime;
		
		pendingAdd.add(request);
	}
	
	public synchronized void update(long timeDiff) {
		for (ClientRequest victom : pendingRemove) {
			requests.remove(victom);
		}
		pendingRemove.clear();
		
		for (ClientRequest newRequest : pendingAdd) {
			requests.add(newRequest);
		}
		pendingAdd.clear();
		
		elapsedTime += timeDiff;
		if (DO_LOG) Log.d(TAG, "update timeDiff:" + timeDiff + ",elapsedTime:" + elapsedTime);
		
		for (ClientRequest request : requests) {
			long requestElapsedTime = elapsedTime - request.startTime;
			if (requestElapsedTime >= request.time) {
				request.callback.onTimeup(request.obj);
				pendingRemove.add(request);
			}
		}
	}
	
	private boolean running = false;
	private long elapsedTime = 0;
	
	private class ClientRequest {
		Callback callback;
		Object obj;
		long time;
		long startTime;
	}
	private List<ClientRequest> requests = new ArrayList<ClientRequest>();
	private List<ClientRequest> pendingAdd = new ArrayList<ClientRequest>();
	private List<ClientRequest> pendingRemove = new ArrayList<ClientRequest>();
}
