package com.activitysendmessage;



import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;

public class MainActivity extends Activity {  

	/**
	 *  向Service发送Message的Messenger对象 
	 */
   Messenger mService = null;  

 
 /** 判断有没有绑定Service */  
   boolean mBound;  

	private ServiceConnection conn = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// Activity已经绑定了Service
			// 通过参数service来创建Messenger对象，这个对象可以向Service发送Message，与Service进行通信
			mService = new Messenger(service);
			mBound = true;
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
			mBound = false;
		}
	};

   
   public void sayHello(View v) {  
	      if (!mBound)
	    	  return;  
	         // 向Service发送一个Message  
	      Message msg = Message.obtain(null, MessengerService.MSG_SAY_HELLO, 0, 0);  
	      try {  
	           mService.send(msg);  
	       } catch (RemoteException e) {  
	         e.printStackTrace();  
	        }  
	   }  
   
   
   @Override  
     protected void onCreate(Bundle savedInstanceState) {  
         super.onCreate(savedInstanceState);  
        setContentView(R.layout.main);  
      }  
   
   @Override  
    protected void onStart() {  
       super.onStart();  
         // 绑定Service  
        bindService(new Intent(this, MessengerService.class), conn,  
            Context.BIND_AUTO_CREATE);  
      }  

	@Override
	protected void onStop() {
		super.onStop();
		// 解绑
		if (mBound) {
			unbindService(conn);
			mBound = false;
		}
	}




}

