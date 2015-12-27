package com.hnyer.bean;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/11/10.
 */
public class Constants {

    private static Toast mToastShort;
    private static Toast mToastLong;

    public static final String SINGLE_SONG_TYPE = "single_song";
    public static final String FIRST_FREQUENCY_FLAG ="first_freq_flag";
    public static final String SECOND_FREQUENCY_FLAG ="second_freq_flag";

    public static final int CMD_PLAY_OR_PAUSE = 0;
    public static final int CMD_PREVIOUS = 1;
    public static final int CMD_NEXT = 2;

    public static void showToast(Context context, String info) {
        if(context == null){
            return;
        }
        if (mToastShort != null) {
            mToastShort.setText(info);
        } else {
            mToastShort = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        }
        mToastShort.show();
    }

    public static void showToast(Context context, int info) {
        showToast(context, context.getResources().getString(info));
    }

    public static void showToastLong(Context context, String msg) {
        if(context == null){
            return;
        }
        if (mToastLong != null) {
            mToastLong.setText(msg);
        } else {
            mToastLong = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        }
        mToastLong.show();
    }

    public static void showToastLong(Context context, int msg) {
        showToastLong(context, context.getResources().getString(msg));
    }

}
