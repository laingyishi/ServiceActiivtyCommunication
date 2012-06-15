package com.activitysendmessage;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class MessengerService extends Service {  
   /** ����Handler�����Ϣ���� */  
   static final int MSG_SAY_HELLO = 1;  
 
    /** 
    * ��Service����Activity��������Ϣ��Handler 
    */  
   class IncomingHandler extends Handler {  
      @Override  
       public void handleMessage(Message msg) {  
          switch (msg.what) {  
              case MSG_SAY_HELLO:  
                  Toast.makeText(getApplicationContext(), "hello!", Toast.LENGTH_SHORT).show();  
                  break;  
              default:  
                   super.handleMessage(msg);  
          }  
       }  
  } 
   /** 
    * ���Messenger���Թ�����Service���Handler��Activity�����������Message��Service��Serviceͨ��Handler���д��� 
    */  
     final Messenger mMessenger = new Messenger(new IncomingHandler());  
  
       /** 
       * ��Activity��Service��ʱ��ͨ�������������һ��IBinder��Activity�����IBinder��������Messenger���Ϳ�����Service��Handler����ͨ���� 
      */  
   public IBinder onBind(Intent intent) {  
       Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();  
       return mMessenger.getBinder();  
   }  
  }  
