package com.scott.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

/**
 * ������onBind�����ﲻ�ڷ����Զ����Binderʵ���ˣ���Ϊ���ڵ�Service��Activitys֮�䲢û�а󶨹�ϵ�ˣ������Ƕ����ģ������ع����У����ǻ����sendBroadcast������
 * ��ָ����action����һ�������н�����Ϣ��intent�������Ļ�������ע���actionΪandroid.intent.action.MY_RECEIVER��Activity�����յ�����������Ϣ�ˡ�
 * 
 * @author wl
 * 
 */
public class DownloadService extends Service {

	private static final int NOTIFY_ID = 0;
	private boolean cancelled;

	private Context mContext = this;
	private NotificationManager mNotificationManager;
	private Notification mNotification;

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
				}

				// ��������֪ͨһ��,���򲻻����
				mNotificationManager.notify(NOTIFY_ID, mNotification);

				if (rate >= 100) {
					stopSelf(); // ֹͣ����
				}

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
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

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

		Intent intnt = new Intent(this, FileMgrActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intnt, PendingIntent.FLAG_UPDATE_CURRENT);
		// ָ��������ͼ
		mNotification.contentIntent = contentIntent;

		mNotificationManager.notify(NOTIFY_ID, mNotification);

		new Thread() {
			public void run() {
				startDownload();
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cancelled = true; // ֹͣ�����߳�
	}

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

			// �����ض�action�Ĺ㲥
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MY_RECEIVER");
			intent.putExtra("progress", rate);
			sendBroadcast(intent);
		}
		if (cancelled) {
			Message msg = handler.obtainMessage();
			msg.what = 0;
			handler.sendMessage(msg);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}