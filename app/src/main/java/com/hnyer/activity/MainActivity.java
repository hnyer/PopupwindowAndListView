package com.hnyer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hnyer.bean.Constants;
import com.hnyer.bean.MyConfig;
import com.hnyer.interf.MyConfirmListener;
import com.hnyer.server.MusicService;
import com.hnyer.ultrasonicnew.R;
import com.hnyer.widgets.CustomProgressDialog;
import com.hnyer.widgets.MusicPopuWindow;
import com.jieli.ultransoniccontrol.FrequencyManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    /*****************************************************/
    /**底部版本提示信息*/
    private TextView showmsg;
    private Intent intent;
    private Context mCtx;
    /*****************************************************/
    private FrequencyManager mFrequencyManager; //assembly data
    private ArrayList<String> firstFreqList =new ArrayList<String>();;
    private ArrayList<String> secondFreqList=new ArrayList<String>();;
    /*****************************************************/

    /**音效素材id*/
    List<Integer> musicId = new ArrayList<Integer>() ;
    /**音效素材文字*/
    List<String> listStr=new ArrayList<String>()  ;
    /**当前选中的背景音乐*/
    private int currentMusicId  ;
    /*****************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycontrollay_xml);
        mCtx = this;
        mFrequencyManager = FrequencyManager.getInstance(MainActivity.this);
        initViewUI();
        listStr.add(this.getString(R.string.music1Str1)) ;
        listStr.add(this.getString(R.string.music1Str2)) ;
        listStr.add(this.getString(R.string.music1Str3)) ;
        listStr.add(this.getString(R.string.music1Str4)) ;
        listStr.add(this.getString(R.string.music1Str5)) ;
        listStr.add(this.getString(R.string.music1Str6)) ;
        listStr.add(this.getString(R.string.music1Str7) ) ;

        musicId.add(R.raw.n1);
        musicId.add(R.raw.n2);
        musicId.add(R.raw.n3);
        musicId.add(R.raw.n4);
        musicId.add(R.raw.n5);
        musicId.add(R.raw.n6);
        musicId.add(R.raw.n7);

        //默认背景音乐
        currentMusicId=musicId.get(0) ;
    }

    /**
     * 初始化控件
     */
    private void initViewUI() {

        this.findViewById(R.id.shutdonw_img).setOnClickListener(btnControlClickListener);
        this.findViewById(R.id.shakehead_img).setOnClickListener(btnControlClickListener);
        this.findViewById(R.id.windType_img).setOnClickListener(btnControlClickListener);
        this.findViewById(R.id.time_img).setOnClickListener(btnControlClickListener);
        this.findViewById(R.id.light_img).setOnClickListener(btnControlClickListener);
        this.findViewById(R.id.windspeed_img).setOnClickListener(btnControlClickListener);
        this.findViewById(R.id.changeMusic).setOnClickListener(btnControlClickListener);
        showmsg = (TextView) this.findViewById(R.id.showmsg);
        showmsg.setText(mCtx.getString(R.string.versionName));
        this.findViewById(R.id.btnPrevious).setOnClickListener(btnControlClickListener);
        this.findViewById(R.id.btnNext).setOnClickListener(btnControlClickListener);
        this.findViewById(R.id.btnPlayOrPause).setOnClickListener(btnControlClickListener);

    }


    /**
     * 点击控监听器
     */
    private View.OnClickListener btnControlClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.btnPrevious:
                    firstFreqList = mFrequencyManager.getFrequency(Constants.CMD_PREVIOUS);
                    startMusicService(firstFreqList, null);
                    break;
                case R.id.btnNext:
                    firstFreqList = mFrequencyManager.getFrequency(Constants.CMD_NEXT);
                    startMusicService(firstFreqList, null);
                    break;

                case R.id.btnPlayOrPause:
                    //here need to change btnPlayOrPause status
                    firstFreqList = mFrequencyManager.getFrequency(Constants.CMD_PLAY_OR_PAUSE);
                    startMusicService(firstFreqList, null);
                    break;

                case R.id.shutdonw_img: //     关机 关机 2461
                    showmsg.setText("");
                    getFrequenByCode(24, 61);
                    break;
                case R.id.shakehead_img:// 摇头 摇头 5723
                    showmsg.setText("");
                    getFrequenByCode(57, 23);

                    break;
                case R.id.windType_img:// 风类 风类 3554
                    showmsg.setText("");
                    getFrequenByCode(35, 54);

                    break;
                case R.id.time_img:// 定时 定时 4618
                    showmsg.setText("");
                    getFrequenByCode(46, 18);

                    break;
                case R.id.light_img:// 彩灯 6010
                    showmsg.setText("");
                    getFrequenByCode(60, 10);

                    break;
                case R.id.windspeed_img:// 开/风速 开/风速度 1375
                    showmsg.setText("");
                    getFrequenByCode(13, 75);

                    break;

                case R.id.changeMusic:
                    //更换音效
                    showGroupPopuwindow(listStr, musicId) ;
                    break ;

            }
        }
    };


    /**
     * 根据code获得list
     * @param strFirstNum
     * @param strSecondNum
     */
    private void getFrequenByCode(int strFirstNum, int strSecondNum) {
        firstFreqList = mFrequencyManager.getFrequency(strFirstNum);
        secondFreqList = mFrequencyManager.getFrequency(strSecondNum);

        //王总说只要播放第一个数据
        firstFreqList = mFrequencyManager.getFrequency(strFirstNum);
        startMusicService(firstFreqList, null);
        //startMusicService(firstFreqList,secondFreqList);
        playSelectedMusic(currentMusicId);
    }


    /**
     * 开启播放服务
     * @param firstFrequency
     * @param secondFrequency
     */
    MyReceiver receiver;
    private void startMusicService(final ArrayList<String> firstFrequency, final ArrayList<String> secondFrequency) {
        startProgressDialog(this.getString(R.string.waitMsg));

        if (intent == null) {
            intent = new Intent();
        }

        //注册接收器,监听播放完毕
        receiver=new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.intent.action.test");
        mCtx.registerReceiver(receiver, filter);

        intent.setClass(MainActivity.this, MusicService.class);
        intent.putExtra(Constants.SINGLE_SONG_TYPE, false);
        intent.putStringArrayListExtra(Constants.FIRST_FREQUENCY_FLAG, firstFrequency);
        intent.putStringArrayListExtra(Constants.SECOND_FREQUENCY_FLAG, secondFrequency);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFrequencyManager == null) {
            mFrequencyManager = FrequencyManager.getInstance(MainActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        if (firstFreqList.size() > 0) {
            firstFreqList.clear();
        }
        firstFreqList = null;
        if (secondFreqList.size() > 0) {
            secondFreqList.clear();
        }
        secondFreqList = null;
        super.onDestroy();

        //关闭服务。hnyer
        if (intent != null){
            stopService(intent);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitDialog(MainActivity.this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    protected void exitDialog(Context context) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.exit_tip_message));
        builder.setTitle(context.getString(R.string.prompt));
        builder.setPositiveButton(context.getString(R.string.bt_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.this.finish();
                System.exit(0);
            }
        });
        builder.setNegativeButton(context.getString(R.string.bt_cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }



    private CustomProgressDialog progressDialog = null;

    /**
     * 开启等待提示
     * @param mesgstr
     */
    private void startProgressDialog(String mesgstr) {
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.setMessage(mesgstr);

        progressDialog.show();
    }

    private void stopProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    //-------------------------------------------------
    public class MyReceiver extends BroadcastReceiver {
        //自定义一个广播接收器
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            int musiccount=bundle.getInt("musiccount");

            if(musiccount == MyConfig.MUSICCOUNT){
//			if(3 == MyConfig.MUSICCOUNT){
                //处理接收到的内容
                stopProgressDialog() ;
                //结束服务
                stopService(new Intent(mCtx, MusicService.class));
                // 解除注册接收器
                mCtx.unregisterReceiver(receiver);
            }

        }
        public MyReceiver(){
            //构造函数，做一些初始化工作，本例中无任何作用
        }

    }
    /***********************************************/


   private MusicPopuWindow showdatazipinfopopuwindow;
    private   void showGroupPopuwindow(List<String> listStr , List<Integer> musicId ) {
        showdatazipinfopopuwindow = new  MusicPopuWindow(this, listStr, musicId) ;

        if (showdatazipinfopopuwindow != null) {
            // 点击事件回调
            showdatazipinfopopuwindow.setMyConfirmListener( new MyConfirmListener() {

                @Override
                public String setMyChooseResult(int chooseType) {
                    //Toast.makeText(MainActivity.this, ""+chooseType, Toast.LENGTH_SHORT).show();
                    currentMusicId = chooseType;
                    playSelectedMusic(currentMusicId) ;
                    //Log.i("hnyer" ," #########"+chooseType ) ;
                    showdatazipinfopopuwindow.dismiss();
                    return null;
                }
            }) ;


            //出现的位置
            LayoutInflater inflater = LayoutInflater.from(this);
            View view2 = inflater.inflate(R.layout.activity_main, null);
            showdatazipinfopopuwindow.showAtLocation(view2, Gravity.CENTER, 0, 0);

        }

    }//end




    /**
     * 播放选中的音乐
     * @param musicId
     */
    private  void playSelectedMusic(int musicId){
        MediaPlayer mediaPlayer = MediaPlayer.create(this,musicId);
        mediaPlayer.start();
        mediaPlayer.setLooping(false);
        mediaPlayer.setVolume(1, 1);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();

            }
        });
    }
}
