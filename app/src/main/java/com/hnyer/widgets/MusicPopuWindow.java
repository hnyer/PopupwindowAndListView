package com.hnyer.widgets;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hnyer.interf.MyConfirmListener;
import com.hnyer.ultrasonicnew.R;


public class MusicPopuWindow extends PopupWindow   {
	private View popupRootView;
	Context mCtx;
	MyConfirmListener myChooseListener;
	ListView musicListView ;


	public MusicPopuWindow(Context context  ,List<String> listStr , final  List<Integer> musicId  ) {
		super(context);
		this.mCtx = context;
		initView();
		this.setContentView(popupRootView);
		this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(false);
		this.setOutsideTouchable(true);// 弹出窗体可点击
		//this.setAnimationStyle(R.style.AnimRight);// 出窗体动画效果
		ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
		this.setBackgroundDrawable(dw);

		MusicAdapter adapter = new MusicAdapter(mCtx, listStr, musicId) ;
		musicListView.setAdapter(adapter) ;
		musicListView.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				myChooseListener.setMyChooseResult(musicId.get(arg2)) ;

			}
		}) ;

	}

	public void initView() {
		LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popupRootView = inflater.inflate(R.layout.popupwindowxml, null);
		musicListView = (ListView) popupRootView.findViewById(R.id.musicListView);

	}



	public class MusicAdapter extends BaseAdapter {
		List<String> listStr  ;
		List<Integer> musicId;
		Context context = null;
		LayoutInflater inflater = null;

		public MusicAdapter(Context context, 	List<String> listStr  ,List<Integer> musicId) {
			this.context = context;
			inflater = LayoutInflater.from(context);
			this.listStr  = listStr;
			this.musicId =musicId;
		}

		@Override
		public int getCount() {
			return listStr.size();
		}

		@Override
		public Object getItem(int position) {
			return listStr.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			final String tempProduct = listStr.get(position);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.sysmsg_list_item, null);
			}

			// 产品类型
			TextView txt =(TextView) convertView.findViewById(R.id.sysmsg_title_content_txt) ;
			txt.setText(tempProduct) ;
			return convertView;
		}
	}








	public MyConfirmListener getMyConfirmListener() {
		return myChooseListener;
	}

	public void setMyConfirmListener(MyConfirmListener myConfirmListener) {
		this.myChooseListener = myConfirmListener;
	}




}
