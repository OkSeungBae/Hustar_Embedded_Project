package com.example.project_toilet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ScrollView scrollView;

    ArrayList<TotiInfo> totiList;

    //리사이클 뷰 관련

    RecyclerView recyclerView;
    TotiItemAdapter totiItemAdapter;
    RecyclerView.LayoutManager layoutManager;

    private Socket client;
    private DataOutputStream dataOutput;
    private DataInputStream dataInput;
    private static String SERVER_IP = "10.1.4.105";
    private static String CONNECT_MSG = "connect";
    private static String STOP_MSG = "stop";

    private static int BUF_SIZE = 100;

    private int a = 10;
    TextView textViewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.scrollView_main);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_main);
        //recyclerView의 layout 크기고정
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        textViewName = findViewById(R.id.textViewName);

        /*Connect connect = new Connect();
        connect.execute(CONNECT_MSG);*/

    }

    @Override
    protected void onStart() {
        //recylceview에 모든 정보 담아오기
        totiList = new ArrayList<TotiInfo>();
        int[] testRemain = {80, 50, 20};
        totiList.add(new TotiInfo("1층 남자화장실", 3, 0,testRemain));
        totiList.add(new TotiInfo("1층 여자화장실", 3, 1,testRemain));
        totiList.add(new TotiInfo("2층 남자화장실", 3, 0,testRemain));
        totiList.add(new TotiInfo("2층 여자화장실", 3, 1,testRemain));
        totiList.add(new TotiInfo("3층 남자화장실", 3, 0,testRemain));
        totiList.add(new TotiInfo("3층 여자화장실", 3, 1,testRemain));

        totiItemAdapter = new TotiItemAdapter();
        totiItemAdapter.setItems(totiList);

        recyclerView.setAdapter(totiItemAdapter);

        Toast.makeText(this, totiItemAdapter.getItemCount() + "개의 아이템이 있습니다", Toast.LENGTH_SHORT).show();

        textViewName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("toti", "메인 터치리스너 발생 : " + a);
                totiItemAdapter.updateItem(a++);

                totiItemAdapter.notifyItemChanged(0);
                return false;
            }
        });
        super.onStart();
    }




    private class Connect extends AsyncTask< String , String,Void > {
        private String output_message;
        private String input_message;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                client = new Socket(SERVER_IP, 8888);
                dataOutput = new DataOutputStream(client.getOutputStream());
                dataInput = new DataInputStream(client.getInputStream());
                output_message = strings[0];
                dataOutput.writeUTF(output_message);
            } catch (UnknownHostException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 1");
            } catch (IOException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 2");
            }

            while (true){
                try {
                    byte[] buf = new byte[BUF_SIZE];
                    int read_Byte  = dataInput.read(buf);
                    input_message = new String(buf, 0, read_Byte);
                    if (!input_message.equals(STOP_MSG)){
                        publishProgress(input_message);
                    }
                    else{
                        break;
                    }
                    Thread.sleep(5);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... params){
            //Adapter에 1층 남자화장실의 데이터만 실시간으로 변경


          /*  send_textView.setText(""); // Clear the chat box
            send_textView.append("보낸 메세지: " + output_message );
            read_textView.setText(""); // Clear the chat box
            read_textView.append("받은 메세지: " + params[0]);*/
        }
    }
}