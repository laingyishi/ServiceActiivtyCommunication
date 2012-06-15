package com.scott.notification;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 我们的MyReceiver类是继承了BroadcastReceiver，在onReceive方法中，定义了收到进度信息并更新UI的逻辑，在onCreate中，我们注册了这个接受者，并指定action为android.intent.
 * action.MY_RECEIVER，如此一来，如果其他组件向这个指定的action发送消息，我们就能够接收到；另外要注意的是，不要忘了在Activity被摧毁的时候调用unregisterReceiver取消注册。
 * 
 * @author wl
 * 
 */
public class MainActivity extends Activity {

	private MyReceiver receiver;
	private TextView text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		text = (TextView) findViewById(R.id.text);

		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.MY_RECEIVER");
		// 注册
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 不要忘了这一步
		unregisterReceiver(receiver);
	}

	public void start(View view) {
		Intent intent = new Intent(this, DownloadService.class);
		// 这里不再使用bindService,而使用startService
		startService(intent);
	}

	/**
	 * 广播接收器
	 * 
	 * @author user
	 * 
	 */
	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			int progress = bundle.getInt("progress");
			text.setText("downloading..." + progress + "%");
		}
	}
}