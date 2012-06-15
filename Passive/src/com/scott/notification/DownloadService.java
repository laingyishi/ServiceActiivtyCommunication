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
 * 我们在onBind方法里不在返回自定义的Binder实例了，因为现在的Service和Activitys之间并没有绑定关系了，他们是独立的；在下载过程中，我们会调用sendBroadcast方法，
 * 向指定的action发送一个附带有进度信息的intent，这样的话，所有注册过action为android.intent.action.MY_RECEIVER的Activity都能收到这条进度消息了。
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
					// 更新进度
					RemoteViews contentView = mNotification.contentView;
					contentView.setTextViewText(R.id.rate, rate + "%");
					contentView.setProgressBar(R.id.progress, 100, rate, false);
				} else {
					// 下载完毕后变换通知形式
					mNotification.flags = Notification.FLAG_AUTO_CANCEL;
					mNotification.contentView = null;
					Intent intent = new Intent(mContext, FileMgrActivity.class);
					// 告知已完成
					intent.putExtra("completed", "yes");
					// 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
					PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					mNotification.setLatestEventInfo(mContext, "下载完成", "文件已下载完毕", contentIntent);
				}

				// 最后别忘了通知一下,否则不会更新
				mNotificationManager.notify(NOTIFY_ID, mNotification);

				if (rate >= 100) {
					stopSelf(); // 停止服务
				}

				break;
			case 0:
				// 取消通知
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
		CharSequence tickerText = "开始下载";
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);

		// 放置在"正在运行"栏目中
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.download_notification_layout);
		contentView.setTextViewText(R.id.fileName, "AngryBird.apk");
		// 指定个性化视图
		mNotification.contentView = contentView;

		Intent intnt = new Intent(this, FileMgrActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intnt, PendingIntent.FLAG_UPDATE_CURRENT);
		// 指定内容意图
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
		cancelled = true; // 停止下载线程
	}

	private void startDownload() {
		cancelled = false;
		int rate = 0;
		while (!cancelled && rate < 100) {
			try {
				// 模拟下载进度
				Thread.sleep(500);
				rate = rate + 5;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.arg1 = rate;
			handler.sendMessage(msg);

			// 发送特定action的广播
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