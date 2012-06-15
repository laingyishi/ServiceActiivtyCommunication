package com.scott.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

/**
 * �ڷ������и�DownloadBinder�࣬���̳���Binder��������һϵ�з�������ȡ����״̬�Լ�������ǰ���񣬸ղ�������MainActivity�л�ȡ�ľ���������ʵ��
 * 
 * @author wl
 * 
 */
public class DownloadService extends Service {

	private static final int NOTIFY_ID = 0;
	private boolean cancelled;
	private int progress;

	private Context mContext = this;

	private NotificationManager mNotificationManager;
	private Notification mNotification;

	private DownloadBinder binder = new DownloadBinder();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				int rate = msg.arg1;
				if (rate < 100) {
					// ���½���
					RemoteViews contentView = mNotification.contentView;
					contentView.setTextViewText(R.id.rate, rate + "%");
					contentView.setProgressBar(R.id.progress, 100, rate, false);
				} else {
					// ������Ϻ�任֪ͨ��ʽ
					mNotification.flags = Notification.FLAG_AUTO_CANCEL;
					mNotification.contentView = null;
					Intent intent = new Intent(mContext, FileMgrActivity.class);
					// ��֪�����
					intent.putExtra("completed", "yes");
					// ���²���,ע��flagsҪʹ��FLAG_UPDATE_CURRENT
					PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					mNotification.setLatestEventInfo(mContext, "�������", "�ļ����������", contentIntent);
					stopSelf();// ͣ����������
				}

				// ��������֪ͨһ��,���򲻻����
				mNotificationManager.notify(NOTIFY_ID, mNotification);
				break;
			case 0:
				// ȡ��֪ͨ
				mNotificationManager.cancel(NOTIFY_ID);
				break;
			}
		};
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// �����Զ����DownloadBinderʵ��
		return binder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cancelled = true; // ȡ�������߳�
	}

	/**
	 * ����֪ͨ
	 */
	private void setUpNotification() {
		int icon = R.drawable.down;
		CharSequence tickerText = "��ʼ����";
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);

		// ������"��������"��Ŀ��
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.download_notification_layout);
		contentView.setTextViewText(R.id.fileName, "AngryBird.apk");
		// ָ�����Ի���ͼ
		mNotification.contentView = contentView;

		Intent intent = new Intent(this, FileMgrActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// ָ��������ͼ
		mNotification.contentIntent = contentIntent;
		mNotificationManager.notify(NOTIFY_ID, mNotification);
	}

	/**
	 * ����ģ��
	 */
	private void startDownload() {
		cancelled = false;
		int rate = 0;
		while (!cancelled && rate < 100) {
			try {
				// ģ�����ؽ���
				Thread.sleep(500);
				rate = rate + 5;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.arg1 = rate;
			handler.sendMessage(msg);

			this.progress = rate;
		}
		if (cancelled) {
			Message msg = handler.obtainMessage();
			msg.what = 0;
			handler.sendMessage(msg);
		}
	}

	/**
	 * DownloadBinder�ж�����һЩʵ�õķ���
	 * 
	 * @author user
	 * 
	 */
	public class DownloadBinder extends Binder {

		/**
		 * ��ʼ����
		 */
		public void start() {
			// �����ȹ���
			progress = 0;
			// ����֪ͨ
			setUpNotification();
			new Thread() {
				public void run() {
					// ����
					startDownload();
				};
			}.start();
		}

		/**
		 * ��ȡ����
		 * 
		 * @return
		 */
		public int getProgress() {
			return progress;
		}

		/**
		 * ȡ������
		 */
		public void cancel() {
			cancelled = true;
		}

		/**
		 * �Ƿ��ѱ�ȡ��
		 * 
		 * @return
		 */
		public boolean isCancelled() {
			return cancelled;
		}
	}
}