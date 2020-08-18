package com.example.project_toilet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {
    Button send_button;
    EditText send_editText;
    TextView send_textView;
    TextView read_textView;
    private Socket client;
    private DataOutputStream dataOutput;
    private DataInputStream dataInput;
    private String SERVER_IP = "10.1.4.111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send_button = findViewById(R.id.send_button);
        send_editText = findViewById(R.id.send_editText);
        send_textView = findViewById(R.id.send_textView);
        read_textView = findViewById(R.id.read_textView);
        Log.w("cnt",  "go!!");
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("cnt",  "go!!");
                Connect connect = new Connect();
                connect.execute("refresh");
            }
        });
    }

    private class Connect extends AsyncTask< String , String,Void > {
        private String output_message;
        private String input_message;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                client = new Socket(SERVER_IP, 8080);
                dataOutput = new DataOutputStream(client.getOutputStream());
                dataInput = new DataInputStream(client.getInputStream());
                output_message = strings[0];
                dataOutput.writeUTF(output_message);
                //Log.w("cnt","connect");
            } catch (UnknownHostException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 1");
            } catch (IOException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 2");
            }
            while (true){
                try {
                    byte[] buf = new byte[100];
                    int read_Byte  = dataInput.read(buf);
                    input_message = new String(buf, 0, read_Byte);
                    if (!input_message.equals("STOP")){
                        publishProgress(input_message);
                    }
                    else{
                        break;
                    }
                    Thread.sleep(2);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... params){
            send_textView.setText(""); // Clear the chat box
            send_textView.append("보낸 메세지: " + output_message );
            read_textView.setText(""); // Clear the chat box
            read_textView.append("받은 메세지: " + params[0]);
        }
        @Override
        protected void onPostExecute(Void result) {
            send_textView.setText(""); // Clear the chat box
            send_textView.append("보낸 메세지: " + output_message );
            read_textView.setText(""); // Clear the chat box
            read_textView.append("받은 메세지: " + input_message );
        }
    }
}