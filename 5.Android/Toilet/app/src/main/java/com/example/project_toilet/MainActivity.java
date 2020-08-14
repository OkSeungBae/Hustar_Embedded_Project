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

public class MainActivity extends AppCompatActivity {
    Button send_button;
    EditText send_editText;
    TextView send_textView;
    TextView read_textView;
    private Socket client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send_button = findViewById(R.id.send_button);
        send_editText = findViewById(R.id.send_editText);
        send_textView = findViewById(R.id.send_textView);
        read_textView = findViewById(R.id.read_textView);


        Log.d("send","go send!!!");
        Connect connect = new Connect();
        connect.execute();
    }

    private class Connect extends AsyncTask<Void ,String ,Void > {
        private String SERVER_IP = "10.1.4.111";
        private DataInputStream dataInput;
        private String inputMessage;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                client = new Socket(SERVER_IP, 8080);
                Log.w("cnt","connect");
            } catch (UnknownHostException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 1");
            } catch (IOException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 2");
            }

            /*
            boolean isStop;
            try {
                while (true) {
                    dataInput = new DataInputStream(client.getInputStream());
                    inputMessage = dataInput.readUTF();
                    publishProgress(inputMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            send_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String text = send_editText.getText().toString();
                    Sender sender = new Sender();
                    sender.execute(text);
                }
            });
            if(client != null){
                Receiver receiver = new Receiver();
                receiver.execute();
            }


        }
    }

    private class Sender extends AsyncTask<String, String, Void> {
        private String message;
        private DataOutputStream dataOutput;
        @Override
        protected Void doInBackground(String... strings) {
            try {
                message = strings[0];
                dataOutput = new DataOutputStream(client.getOutputStream());
                dataOutput.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            send_textView.setText(""); // Clear the chat box
            send_textView.append("보낸 메세지: " + message + "\n");
        }
    }


    private class Receiver extends AsyncTask<Void, Void, Void> {

        private String message;
        private DataInputStream dataInput;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                dataInput = new DataInputStream(client.getInputStream());
                message = dataInput.readUTF();
                if(! message.isEmpty()){
                    publishProgress(null);
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            read_textView.setText(""); // Clear the chat box
            read_textView.append("Server: " + message + "\n");
        }
    }

}