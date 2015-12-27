/*
package com.hnyer.activity;

*/
/**
 * Created by rayman on 2015/12/16.
 *//*

//public class MainControlActivity {
//}


import android.media.AudioManager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.hnyer.bean.MyConfig;
import com.hnyer.server.MusicService;
import com.hnyer.ultrasonicnew.R;
import com.hnyer.widgets.CustomProgressDialog;


public class MainControlActivity extends Activity implements OnClickListener {
    Intent intent;
    private Context mCtx;

    private TextView showmsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mycontrollay_xml);
        mCtx = this;
//		AbViewUtil.scaleContentView((RelativeLayout) findViewById(R.id.mycontrollay_xml_id));
        initView();

        setMaxSound();
    }

    private CustomProgressDialog progressDialog = null;

    private void startProgressDialog(String mesgstr) {
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
            progressDialog.setMessage(mesgstr);
        }

        progressDialog.show();
    }

    private void stopProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    MyReceiver receiver;
    private long startTime1;
    private long startTime2;
    private long endTime;
    //临时记录变量
    private String strFirstNumAll;
    private String strSecondNumAll;

    //需要连续发送 2次
    private int MSGCONT = 1;
    private int MSGALLCONT = 2;
    //当前是否能发送
    public boolean sendable = true;

    private void startMyService(String strFirstNum, String strSecondNum) {

        strFirstNumAll = strFirstNum;
        strSecondNumAll = strSecondNum;

        Log.i("hnyer", " " + strFirstNumAll + "  " + strSecondNumAll);


        startProgressDialog("正在发送命令  " + MSGCONT + "次" + "  code=" + strFirstNumAll + "   " + strSecondNumAll);
        if (MSGCONT == 1) {
            Log.i("hnyer", "开始第一次");
            startTime1 = System.currentTimeMillis();

        } else {
            Log.i("hnyer", "开始第2次");
            startTime2 = System.currentTimeMillis();

        }
        // 注册接收器
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.test");
        mCtx.registerReceiver(receiver, filter);

        ArrayList<String> firstFrequency = UWManager.getInstance()
                .getFrequencyByNumber(Integer.valueOf(strFirstNum));

        ArrayList<String> secondFrequency = UWManager.getInstance()
                .getFrequencyByNumber(Integer.valueOf(strSecondNum));

        startMusicService(firstFrequency, secondFrequency);
    }

    private void initView() {
        this.findViewById(R.id.shutdonw_img).setOnClickListener(this);
        this.findViewById(R.id.shakehead_img).setOnClickListener(this);
        this.findViewById(R.id.windType_img).setOnClickListener(this);
        this.findViewById(R.id.time_img).setOnClickListener(this);
        this.findViewById(R.id.light_img).setOnClickListener(this);
        this.findViewById(R.id.windspeed_img).setOnClickListener(this);

        showmsg = (TextView) this.findViewById(R.id.showmsg);
        showmsg.setText("版本：" + mCtx.getString(R.string.versionName));

    }

    private void startMusicService(final ArrayList<String> firstFrequency,
                                   final ArrayList<String> secondFrequency) {

        intent = new Intent(this, MusicService.class);
        intent.putExtra("single_song", false);
        intent.putStringArrayListExtra("uw_first_freq", firstFrequency);
        intent.putStringArrayListExtra("uw_second_freq", secondFrequency);
        startService(intent);
    }

    MusicService service11 = new MusicService();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (intent != null)
            stopService(intent);

        //音量还原
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, userVolume, 0);
        }

        Log.i("hnyer", "222---->" + mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shutdonw_img: // 关机 关机 2461
                MSGCONT = 1;
                showmsg.setText("");
                startMyService("24", "61");
                break;
            case R.id.shakehead_img:// 摇头 摇头 5723
                MSGCONT = 1;
                showmsg.setText("");
                startMyService("57", "23");

                break;
            case R.id.windType_img:// 风类 风类 3554
                MSGCONT = 1;
                showmsg.setText("");
                startMyService("35", "54");

                break;
            case R.id.time_img:// 定时 定时 4618
                MSGCONT = 1;
                showmsg.setText("");



                ("46", "18");

                break;
            case R.id.light_img:// 彩灯 6010
                MSGCONT = 1;
                showmsg.setText("");
                startMyService("60", "10");

                break;
            case R.id.windspeed_img:// 开/风速 开/风速度 1375
                MSGCONT = 1;
                showmsg.setText("");
                startMyService("13", "75");

                break;

            default:
                break;
        }
    }

    // -------------------------------------------------
    long time1 = 0;
    long time2 = 0;

    public class MyReceiver extends BroadcastReceiver {
        // 自定义一个广播接收器
        @Override
        public void onReceive(Context context, Intent intent) {
            endTime = System.currentTimeMillis();

            if (MSGCONT == 1) {
                Log.i("hnyer", "第1次结束");
                time1 = endTime - startTime1;
                showmsg.setText("耗时 " + time1 + " =  " + time1 + "  毫秒");
            } else {
                time2 = endTime - startTime2;
                Log.i("hnyer", "第2次结束");
                showmsg.setText("耗时" + time1 + " + " + time2 + " =  " + (time1 + time2) + "  毫秒");

            }
            Bundle bundle = intent.getExtras();
            int musiccount = bundle.getInt("musiccount");

            if (musiccount == MyConfig.MUSICCOUNT) {
                // 处理接收到的内容
                stopProgressDialog();

                stopService(new Intent(mCtx, MusicService.class));
                // 解除注册接收器
                try {
                    mCtx.unregisterReceiver(receiver);

                } catch (Exception e) {
                    Log.i("hnyer", "unregisterReceiver异常");
                }


                if (MSGCONT == MSGALLCONT) {
                    //发送完成
                    // 结束服务
                    Log.i("hnyer", "流程结束");

                } else {
//					Log.i("hnyer", "第1次结束") ;
                    //还只发送了一次
                    MSGCONT++;
                    startMyService(strFirstNumAll, strSecondNumAll);

                }


            }

        }

        public MyReceiver() {
            // 构造函数，做一些初始化工作，本例中无任何作用
        }

    }

    // end MyReceiver-------------------------------------------------
    private int userVolume = 0;
    private AudioManager mAudioManager;

    private void setMaxSound() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
        //用户原先的音量
        userVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        //最大音量
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        Log.i("hnyer", "maxVolume=" + maxVolume);
        Log.i("hnyer", "userVolume=" + userVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, maxVolume, 0);
        Log.i("hnyer", "111---->" + mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
    }


}
*/
