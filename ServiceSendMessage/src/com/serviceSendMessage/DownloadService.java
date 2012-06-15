package com.serviceSendMessage;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

/**
 * ����һ���ڲ���DownloadBinder�̳�Binder,��onBind���������ʵ������������activity������activity �Ϳ��Ե���DownloadBinder��·�����
 * 
 * @author wl
 * 
 */

public class DownloadService extends Service {


	private boolean cancelled;
	private static Messenger mMessenger = null;
	private DownloadBinder binder = new DownloadBinder();
	Handler myHandler;
	int rate = 0;


	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cancelled = true;
	}


	private void startDownload() {
		cancelled = false;

		while (!cancelled && rate < 100) {
			try {
				Thread.sleep(500);
				rate = rate + 5;
				Message msg = Message.obtain();
				msg.arg1 = rate;
				// mMessenger.send(msg);
				myHandler.sendMessage(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}




	public class DownloadBinder extends Binder {


		public void start() {
			new Thread() {
				public void run() {
					startDownload();
				};
			}.start();
		}


		public void getHandlerFromActivity(Handler handler) {
			myHandler = handler;
		}

		public void stop() {
			cancelled = true;
		}

		public void goingOn() {
			cancelled = false;
		}
	}
}