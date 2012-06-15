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
	 *  ��Service����Message��Messenger���� 
	 */
   Messenger mService = null;  

 
 /** �ж���û�а�Service */  
   boolean mBound;  

	private ServiceConnection conn = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// Activity�Ѿ�����Service
			// ͨ������service������Messenger����������������Service����Message����Service����ͨ��
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
	         // ��Service����һ��Message  
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
         // ��Service  
        bindService(new Intent(this, MessengerService.class), conn,  
            Context.BIND_AUTO_CREATE);  
      }  

	@Override
	protected void onStop() {
		super.onStop();
		// ���
		if (mBound) {
			unbindService(conn);
			mBound = false;
		}
	}




}

