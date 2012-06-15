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
 * ���ǵ�MyReceiver���Ǽ̳���BroadcastReceiver����onReceive�����У��������յ�������Ϣ������UI���߼�����onCreate�У�����ע������������ߣ���ָ��actionΪandroid.intent.
 * action.MY_RECEIVER�����һ�������������������ָ����action������Ϣ�����Ǿ��ܹ����յ�������Ҫע����ǣ���Ҫ������Activity���ݻٵ�ʱ�����unregisterReceiverȡ��ע�ᡣ
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
		// ע��
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ��Ҫ������һ��
		unregisterReceiver(receiver);
	}

	public void start(View view) {
		Intent intent = new Intent(this, DownloadService.class);
		// ���ﲻ��ʹ��bindService,��ʹ��startService
		startService(intent);
	}

	/**
	 * �㲥������
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