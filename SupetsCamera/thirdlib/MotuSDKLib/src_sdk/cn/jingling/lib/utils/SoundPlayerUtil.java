/**
 * This Class is used to play music.
 *
 * @author sunzhibin
 * @version 1.0
 */

package cn.jingling.lib.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.jingling.lib.utils.LogUtils;

public class SoundPlayerUtil implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private static final String TAG = "SoundPlayerUtil";

    private static SoundPlayerUtil mSingleton;

    private MediaPlayer mSoundPlayer;

    private Context mContext;

    private SoundPlayerUtil(Context context) {
        mContext = context.getApplicationContext();
        // mSoundPlayer = new MediaPlayer();
    }

    public synchronized static SoundPlayerUtil getSingleton(Context context) {
        if (mSingleton == null) {
            mSingleton = new SoundPlayerUtil(context);
        }
        return mSingleton;
    }

    public void stop() {
        try {
            if (mSoundPlayer != null) {
                mSoundPlayer.stop();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void play(int resId) {
        try {
            if (mSoundPlayer != null) {
                if (mSoundPlayer.isPlaying()) {
                    return;
                }
                mSoundPlayer.stop();
            }
            mSoundPlayer = MediaPlayer.create(mContext, resId);
            play();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    
    public void play(String assetsPath) {
        try {
            if (mSoundPlayer != null) {
                mSoundPlayer.stop();
            }
            mSoundPlayer = new MediaPlayer();
			try {
				AssetFileDescriptor file = mContext.getResources().getAssets().openFd(assetsPath);
				mSoundPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mSoundPlayer.prepare();
			} catch (IOException e) {
				mSoundPlayer = null;
			}
            play();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 适用于连续多次播放同一个音频
     * 
     * @param resId	-	The id of audio resource
     * @param first	-	Create a MediaPlayer object from resId if first is true
     */
    public void playOneMore(int resId, boolean first) {
        try {
			if (mSoundPlayer != null) {
				 if (mSoundPlayer.isPlaying()) {
	                    return;
				 }
	             mSoundPlayer.stop();
            } else if (first) {
            	mSoundPlayer = MediaPlayer.create(mContext, resId);
            }
			
			mSoundPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void play(Uri uri, int duration) throws IOException {
        try {
            if (mSoundPlayer != null) {
                if (mSoundPlayer.isPlaying()) {
                    return;
                }
                mSoundPlayer.stop();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            mSoundPlayer.release();
        }
        mSoundPlayer = MediaPlayer.create(mContext, uri);
        if (mSoundPlayer == null) {
            throw new IOException();
        }
        init();
        play();
        if (duration > 0) {
            new Timer().schedule(new StopTask(), duration);
        }
    }

    private void init() throws IOException {
        if (mSoundPlayer == null)
            return;
        try {
            mSoundPlayer.setOnCompletionListener(this);
            mSoundPlayer.setOnErrorListener(this);
            if (mSoundPlayer.isPlaying()) {
                mSoundPlayer.stop();
            }
            mSoundPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mSoundPlayer.prepare();
        } catch (IllegalStateException e) {
        	LogUtils.e(TAG, "IllegalStateException happened.");
            e.printStackTrace();
        }
    }

    private void play() {
        if (mSoundPlayer == null) {
            return;
        }
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        try {
            if (AudioManager.RINGER_MODE_NORMAL == ringerMode) {
                mSoundPlayer.start();
            } else {
                mSoundPlayer.release();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mSoundPlayer != null)
            mSoundPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mSoundPlayer != null)
            mSoundPlayer.release();
    }

    private class StopTask extends TimerTask {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            stop();
        }

    }

}
