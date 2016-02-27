package com.studioirregular.bonniesbrunch;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.studioirregular.bonniesbrunch.base.ObjectArray;
import com.studioirregular.bonniesbrunch.base.ObjectArray.OutOfCapacityException;

public class SoundSystem {

	private static final String TAG = "sound-system";
	
	// singleton
	public static SoundSystem getInstance() {
		if (sInstance == null) {
			sInstance = new SoundSystem();
		}
		return sInstance;
	}
	
	public static void releaseInstance() {
		if (sInstance != null) {
			sInstance = null;
		}
	}
	
	public static class Sound {
		public int resource;
		public int soundId;	// return from SoundPool
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " resource:" + resource + ",soundId:" + soundId;
		}
		
	}
	
	public static final int INVALID_STREAM_ID = -1;
	
	private SoundSystem() {
		if (Config.DEBUG_LOG) Log.d(TAG, "constructed!");
		
		soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
		sounds = new ObjectArray<Sound>(MAX_SOUND_FILES);
		loopingStreams = new int[MAX_STREAMS];
		for (int i = 0; i < MAX_STREAMS; i++) {
			loopingStreams[i] = INVALID_STREAM_ID;
		}
		musicDisabled = false;
		soundDisabled = false;
	}
	
	public void setup(Context context) {
		if (Config.DEBUG_LOAD_EXTERNAL_RESOURCE_FOR_SOUND) {
			try {
				this.context = context.createPackageContext(Config.EXTERNAL_RESOURCES_PACKAGE, 0);
			} catch (NameNotFoundException e) {
				Log.e(TAG, "setup failed to create context for external resource package, exception:" + e);
			}
		} else {
			this.context = context;
		}
	}
	
	public synchronized void onPause() {
		if (Config.DEBUG_LOG) Log.d(TAG, "onPause");
		
		if (mplayer != null && mplayer.isPlaying()) {
			mplayer.pause();
		}
		
		pauseAllLoopingSounds();
	}
	
	public synchronized void onResume() {
		if (Config.DEBUG_LOG) Log.d(TAG, "onResume");
		
		if (mplayer != null) {
			if (!mplayer.isPlaying() && mplayer.isLooping()) {
				mplayer.start();
			}
		}
	}
	
	public synchronized void stop() {
		if (Config.DEBUG_LOG) Log.d(TAG, "stop");
		
		if (mplayer != null) {
			mplayer.release();
			mplayer = null;
		}
		
		if (soundPool != null) {
			stopAllLoopingSounds();
			
			soundPool.release();
			soundPool = null;
		}
	}
	
	public synchronized boolean playMusic(String resource, boolean looping) {
		if (Config.DEBUG_LOG) Log.d(TAG, "playMusic resource:" + resource + ",looping:" + looping);
		
		if (musicDisabled) {
			return true;
		}
		
		if (mplayer != null && mplayer.isPlaying() && musicResource.equals(resource)) {
			return true;
		}
		
		final int resId = getResourceId(resource);
		if (resId == 0) {
			Log.e(TAG, "cannot find resource:" + resource);
			return false;
		}
		
		if (mplayer != null) {
			mplayer.release();
		}
		
		// create another instance for new song.
		// TODO: check why reset and setDataSource not working.
		mplayer = MediaPlayer.create(context, resId);
		if (mplayer == null) {
			if (Config.DEBUG_LOG) Log.e(TAG, "cannot create media player with resource:" + resource);
			return false;
		}
		
		musicResource = resource;
		
		mplayer.setLooping(looping);
		mplayer.start();
		return true;
	}
	
	public Sound load(String resource) {
		if (Config.DEBUG_LOG) Log.d(TAG, "load resource:" + resource);
		
		Sound result = null;
		
		final int resId = getResourceId(resource);
		if (resId == 0) {
			Log.e(TAG, "cannot find resource:" + resource);
			return null;
		}
		
		result = findSound(resId);
		if (result != null) {
			return result;
		} else {
			result = new Sound();
			result.resource = resId;
			int soundId = 0;
			
			if (Config.DEBUG_LOAD_EXTERNAL_RESOURCE_FOR_SOUND) {
				try {
					Resources externalResources = context.getPackageManager().getResourcesForApplication(Config.EXTERNAL_RESOURCES_PACKAGE);
					AssetFileDescriptor afd = externalResources.openRawResourceFd(resId);
					soundId = soundPool.load(afd, 1);
				} catch (NameNotFoundException e) {
					Log.e(TAG, "cannot find external resource package:" + Config.EXTERNAL_RESOURCES_PACKAGE);
					return null;
				}
			} else {
				soundId = soundPool.load(context, resId, 1);
			}
			
			if (soundId == 0) {
				Log.e(TAG, "failed to load sound:" + resource);
			} else {
				result.soundId = soundId;
				addSound(result);
			}
		}
		
		return result;
	}
	
	public void setListener(SoundPool.OnLoadCompleteListener listener) {
		soundPool.setOnLoadCompleteListener(listener);
	}
	
	// return stream id, client can use stream id to stop looping sound.
	// -1 mean invalid stream id.
	public int playSound(Sound sound, boolean loop) {
		if (sound == null || sound.soundId == 0) {
			Log.e(TAG, "playSound invalid sound:" + sound);
			return INVALID_STREAM_ID;
		}
		
		final float volumn = soundDisabled ? 0.0f : 1.0f;
		int streamId = soundPool.play(sound.soundId, volumn, volumn, 1, loop ? -1 : 0, 1.0f);
		if (streamId == 0) {
			Log.e(TAG, "sound pool play failed, return streamId:" + streamId);
			return INVALID_STREAM_ID;
		}
		
		if (loop) {
			addLoopingSound(streamId);
		}
		return streamId;
	}
	
	public boolean stopSound(int streamId) {
		if (streamId < 0) {
			Log.e(TAG, "stopSound invalid streamId:" + streamId);
			return false;
		}
		soundPool.stop(streamId);
		removeLoopingSound(streamId);
		return true;
	}
	
	public void pauseAllLoopingSounds() {
		if (Config.DEBUG_LOG) Log.d(TAG, "pauseAllLoopingSounds");
		
		for (int i = 0; i < MAX_STREAMS; i++) {
			if (Config.DEBUG_LOG) Log.d(TAG, "loopingStreams[" + i + "]:" + loopingStreams[i]);
			if (loopingStreams[i] != INVALID_STREAM_ID) {
				soundPool.pause(loopingStreams[i]);
			}
		}
	}
	
	public void resumeAllLoopingSounds() {
		if (Config.DEBUG_LOG) Log.d(TAG, "resumeAllLoopingSounds");
		
		for (int i = 0; i < MAX_STREAMS; i++) {
			if (Config.DEBUG_LOG) Log.d(TAG, "loopingStreams[" + i + "]:" + loopingStreams[i]);
			if (loopingStreams[i] != INVALID_STREAM_ID) {
				soundPool.resume(loopingStreams[i]);
			}
		}
	}
	
	public void stopAllLoopingSounds() {
		if (Config.DEBUG_LOG) Log.d(TAG, "stopAllLoopingSounds");
		
		for (int i = 0; i < MAX_STREAMS; i++) {
			if (Config.DEBUG_LOG) Log.d(TAG, "loopingStreams[" + i + "]:" + loopingStreams[i]);
			if (loopingStreams[i] != INVALID_STREAM_ID) {
				soundPool.stop(loopingStreams[i]);
			}
		}
	}
	
	public void disableMusic(boolean disable) {
		if (musicDisabled == disable) {
			return;
		}
		
		musicDisabled = disable;
		
		if (disable) {
			if (mplayer != null && mplayer.isPlaying()) {
				mplayer.stop();
				mplayer.release();
				mplayer = null;
			}
		}
	}
	
	public void disableSound(boolean disable) {
		if (soundDisabled == disable) {
			return;
		}
		
		soundDisabled = disable;
		
		final float newVolume = disable ? 0.0f : 1.0f;
		
		for (int i = 0; i < MAX_STREAMS; i++) {
			if (loopingStreams[i] != INVALID_STREAM_ID) {
				soundPool.setVolume(loopingStreams[i], newVolume, newVolume);
			}
		}
	}
	
	public boolean isMusicDisabled() {
		return musicDisabled;
	}
	
	public boolean isSoundDisabled() {
		return soundDisabled;
	}
	
	private int getResourceId(String resource) {
		int resId = 0;
		
		if (context == null) {
			return resId;
		}
		
		if (Config.DEBUG_LOAD_EXTERNAL_RESOURCE_FOR_SOUND) {
			try {
				Resources externalResources = context.getPackageManager().getResourcesForApplication(Config.EXTERNAL_RESOURCES_PACKAGE);
				resId = externalResources.getIdentifier(Config.EXTERNAL_SOUND_PREFIX + resource, null, null);
			} catch (NameNotFoundException e) {
				Log.e(TAG, "cannot find external resource package:" + Config.EXTERNAL_RESOURCES_PACKAGE);
			}
		} else {
			resId = context.getResources().getIdentifier(context.getPackageName() + ":raw/" + resource, null, null);
		}
		
		return resId;
	}
	
	private Sound findSound(int resId) {
		for (int i = 0; i < sounds.size(); i++) {
			Sound sound = sounds.get(i);
			if (sound.resource == resId) {
				return sound;
			}
		}
		return null;
	}
	
	private void addSound(Sound sound) {
		try {
			sounds.add(sound);
		} catch (OutOfCapacityException e) {
			Log.e(TAG, "error when addSound, exception:" + e);
		}
	}
	
	private void addLoopingSound(int streamId) {
		for (int i = 0; i < MAX_STREAMS; i++) {
			if (loopingStreams[i] < 0) {
				loopingStreams[i] = streamId;
				return;
			}
		}
		Log.e(TAG, "addLoopingSound loopingStreams fulled...");
	}
	
	private void removeLoopingSound(int streamId) {
		for (int i = 0; i < MAX_STREAMS; i++) {
			if (loopingStreams[i] == streamId) {
				loopingStreams[i] = INVALID_STREAM_ID;
				return;
			}
		}
		Log.e(TAG, "removeLoopingSound failed, streamId:" + streamId);
	}
	
	private static final int MAX_STREAMS = 8;
	private static final int MAX_SOUND_FILES = 64;
	
	private static SoundSystem sInstance = null;
	
	private Context context;
	private MediaPlayer mplayer;
	private String musicResource;
	private SoundPool soundPool;
	private ObjectArray<Sound> sounds;
	private int[] loopingStreams;
	
	private boolean soundDisabled;
	private boolean musicDisabled;
}
