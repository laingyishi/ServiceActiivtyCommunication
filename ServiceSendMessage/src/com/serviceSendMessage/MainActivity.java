package com.serviceSendMessage;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

/**
 * 通过绑定DownloadService来获得DownloadBinder的实例，通过这个内部类里面的方法传递handler给service，service通过handler给activity发送消息。
 * activity是通过调用DownloadBinder里的方法来向service传递数据。
 * 
 * @author wl
 * 
 */
public class MainActivity extends Activity {

	boolean mBound;
	private DownloadService.DownloadBinder binder;
	Messenger mService = null;
	private boolean binded;
	ProgressBar progressBar;
	Button controlButton;
	boolean start = true;

	// 处理service发送来的消息
	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			int rate = msg.arg1;
			if (rate == 100) {
				controlButton.setEnabled(false);
			}
			progressBar.setProgress(rate);
			super.handleMessage(msg);
		}
	};


	public void controlButton(View view) {
		// 取消下载

		if (start) {
			controlButton.setText("开始下载");
			binder.stop();
			start = false;
		} else {
			controlButton.setText("暂停下载");
			binder.goingOn();
			binder.start();
			start = true;
		}
	}

	// 绑定service中获得DownloadBinder的实例
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder = (DownloadService.DownloadBinder) service;
			binded = true;
			// 把myHandler传递给service
			binder.getHandlerFromActivity(myHandler);
			binder.start();
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		controlButton = (Button) findViewById(R.id.controlButton);
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, DownloadService.class), conn, Context.BIND_AUTO_CREATE);
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (binded) {
			unbindService(conn);
			mBound = true;
		}
	}
}