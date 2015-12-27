package com.hnyer.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.hnyer.bean.Constants;
import com.hnyer.bean.MyConfig;
import com.hnyer.ultrasonicnew.R;
import com.hnyer.utils.Wlog;
import com.jieli.ultransoniccontrol.FrequencyManager;

public class MusicService extends Service {

	private final String TAG = getClass().getSimpleName();
	private MediaPlayer mMediaPlayer;
	private ArrayList<String> mFrequency = new ArrayList<String>();
	private ArrayList<String> mTempFreq = new ArrayList<String>();
	private static HashMap<String, Integer> mUWNameIds = new HashMap<String, Integer>();
	private Integer[] frequencyIds = null;
	private boolean mOnly = false;
	private FrequencyManager frequencyManager;
	private AudioManager am;
	private String outputPath = "";

	private String model;

	@Override
	public void onCreate() {
		super.onCreate();
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, max,
				AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

		frequencyManager = FrequencyManager.getInstance(getApplicationContext());
		mUWNameIds = frequencyManager.getResourceHashMap();
		outputPath = Environment.getExternalStorageDirectory().getPath() + "/"+ Environment.DIRECTORY_DOWNLOADS;

		if (am.isSpeakerphoneOn()) {
			am.setSpeakerphoneOn(false);
		}
		am.setMode(AudioManager.MODE_IN_CALL);

		model = android.os.Build.MODEL;
		Log.e("Nike", android.os.Build.MODEL);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			return START_STICKY;
		}
		boolean singleSong = intent.getBooleanExtra(Constants.SINGLE_SONG_TYPE, false);
		mFrequency = intent.getStringArrayListExtra(Constants.FIRST_FREQUENCY_FLAG);
		mTempFreq = intent.getStringArrayListExtra(Constants.SECOND_FREQUENCY_FLAG);

		if (singleSong) {
			MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.er_ge1);
			mediaPlayer.start();
			mediaPlayer.setLooping(false);
			mediaPlayer.setVolume(1, 1);
		} else {
			if (mFrequency != null && mTempFreq != null) {
				mOnly = false;
				Wlog.w(TAG, "=========mOnly = false;==========");
				//Audio object
				frequencyIds = new Integer[mFrequency.size() + mTempFreq.size() + 1];

				//Merge the first command audio
				for (int i = 0; i < mFrequency.size(); i++) {
					frequencyIds[i] = mUWNameIds.get(mFrequency.get(i));
					Wlog.d(TAG, "frequencyIds[" + i + "] = " + mFrequency.get(i));
				}

				//Two audio intervals
				frequencyIds[mFrequency.size()] = mUWNameIds.get(FrequencyManager.DELAY_LONG_FREQUENCY);
				Wlog.d(TAG, "frequencyIds["+ mFrequency.size() +"] = delay_long");

				//Merge second command audio
				for (int i = mFrequency.size() + 1; i < frequencyIds.length; i++) {
					int j = i - (mFrequency.size() + 1);
					frequencyIds[i] = mUWNameIds.get(mTempFreq.get(j));
					Wlog.d(TAG, "frequencyIds["+ i +"] = " + mTempFreq.get(j));
				}

				//play the audio
				if(frequencyManager.uniteWAVFiles(frequencyIds, outputPath)){
					Wlog.d(TAG, "uniteWAVFiles success!");
					playAudio(outputPath + "/" + FrequencyManager.MERGED_FILE_NAME);
				}else{
					if(getApplicationContext() != null) {
						Toast.makeText(getApplicationContext(), "uniteWAVFiles failed!", Toast.LENGTH_LONG).show();
					}
				}
			} else if (mFrequency != null) {
				if(mFrequency.size() == 0 ){
					return START_STICKY;
				}
				mOnly = true;

				Wlog.w(TAG,"=========mOnly = true;==========");
				//Audio object
				frequencyIds = new Integer[mFrequency.size()];

				//Merge the first command audio
				for (int i = 0; i < mFrequency.size(); i++) {
					frequencyIds[i] = mUWNameIds.get(mFrequency.get(i));
					Wlog.d(TAG, "frequencyIds["+ i +"] = " + mFrequency.get(i));
				}

				//play the audio
				if(frequencyManager.uniteWAVFiles(frequencyIds, outputPath)){
					Wlog.d(TAG, "uniteWAVFiles success!");
					playAudio(outputPath + "/" + FrequencyManager.MERGED_FILE_NAME);
				}else{
					if(getApplicationContext() != null) {
						Toast.makeText(getApplicationContext(), "uniteWAVFiles failed!", Toast.LENGTH_LONG).show();
					}
				}
			}
		}

		return START_STICKY;
	}

	private void playAudio(final String path){
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mediaPlayer) {
					mediaPlayer.release();
					File playFile = new File(path);
					if(playFile != null){
						if(playFile.exists()){
							if(playFile.isFile()){
								playFile.delete();
							}
						}
					}

					playEndReformActivity();

				}
			});
		} catch (IllegalArgumentException e) {
			Wlog.e(TAG, "IllegalArgumentException = " + e.getMessage());
			e.printStackTrace();
		} catch (SecurityException e) {
			Wlog.e(TAG, "SecurityException = " + e.getMessage());
			e.printStackTrace();
		} catch (IllegalStateException e) {
			Wlog.e(TAG, "IllegalStateException = " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Wlog.e(TAG, "IOException = " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mMediaPlayer != null){
			mMediaPlayer.release();// release mediaPlay source
			mMediaPlayer = null;
		}
		am.setMode(AudioManager.MODE_NORMAL);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	/**
	 * 播放完毕，通知activity
	 */
	private void playEndReformActivity(){
		Intent intent=new Intent();
		intent.putExtra("musiccount", MyConfig.MUSICCOUNT);
		intent.setAction("android.intent.action.test");//action与接收器相同
		sendBroadcast(intent);
	}
}