package com.example.project_toilet;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

public class SocketIntentService extends IntentService {
    public SocketIntentService() {
        super("SocketIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //service가 시작되었을 때 호출됨 (Background)

        //몇번 값만 받아온다.
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Log.d("SocketIntentService", "서비스 동작중 : " + i);
        }
    }

}
