package com.scott.notification;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

/**
 * FileMgrActivity��ģʽ��MainActivity��࣬Ҳ��ע������ͬ�Ĺ㲥�����ߣ�����DownloadService��˵�Լ��ǹ㲥��վ��MainActivity��FileMgrActivity�������ڣ�
 * �ź��ܹ�ͬʱ���������ڣ����ڴ�����ԣ���֮ǰ�Ĵ���Ƚ�һ�£����ּ������࣬��Ȼ���ַ�ʽ����һЩ��
 * 
 * @author wl
 * 
 */
public class FileMgrActivity extends Activity {

	private MyReceiver receiver;
	private ProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filemgr);
		progressBar = (ProgressBar) findViewById(R.id.progress);

		if ("yes".equals(getIntent().getStringExtra("completed"))) {
			progressBar.setProgress(100);
		}

		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.MY_RECEIVER");
		// ע��
		registerReceiver(receiver, filter);
	}

	public void cancel(View view) {
		Intent intent = new Intent(this, DownloadService.class);
		stopService(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ��Ҫ������һ��
		unregisterReceiver(receiver);
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
			progressBar.setProgress(progress);
		}
	}

}