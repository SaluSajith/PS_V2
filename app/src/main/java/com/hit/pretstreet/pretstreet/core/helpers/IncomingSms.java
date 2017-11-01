package com.hit.pretstreet.pretstreet.core.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.SmsListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 13/7/2017
 */

public class IncomingSms extends BroadcastReceiver {
    private static SmsListener mListener;
    @Override
    public void onReceive (Context context, Intent intent){
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    try {
                        if(phoneNumber.contains("PretST")){
                            String messageBody = currentMessage.getMessageBody();
                            Pattern pattern = Pattern.compile("(\\d{4})");
                            Matcher matcher = pattern.matcher(messageBody);
                            //Log.e("SmsReceiver", "phoneNumber.contains(PretST)");
                            String val = "";
                            if (matcher.find()) {
                                //System.out.println("SmsReceiver"+matcher.group(1));
                                val = matcher.group(1);
                                if (mListener != null)
                                mListener.messageReceived(val.trim());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}