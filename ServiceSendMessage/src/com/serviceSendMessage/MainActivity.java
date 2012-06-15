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
 * ͨ����DownloadService�����DownloadBinder��ʵ����ͨ������ڲ�������ķ�������handler��service��serviceͨ��handler��activity������Ϣ��
 * activity��ͨ������DownloadBinder��ķ�������service�������ݡ�
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

	// ����service����������Ϣ
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
		// ȡ������

		if (start) {
			controlButton.setText("��ʼ����");
			binder.stop();
			start = false;
		} else {
			controlButton.setText("��ͣ����");
			binder.goingOn();
			binder.start();
			start = true;
		}
	}

	// ��service�л��DownloadBinder��ʵ��
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder = (DownloadService.DownloadBinder) service;
			binded = true;
			// ��myHandler���ݸ�service
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