package com.smartpos.socketdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_ip, et_port, et_content;
    private Button btn_send;
    private TextView tv_show;
    private String ip;
    private String port;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_ip = findViewById(R.id.et_ip);
        et_port = findViewById(R.id.et_port);
        et_content = findViewById(R.id.et_input);
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        tv_show = findViewById(R.id.tv_show);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    tv_show.setText(msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                Editable et_ipText = et_ip.getText();
                Editable et_portText = et_port.getText();
                Editable et_contentText = et_content.getText();
                if (et_ipText != null && !et_ipText.toString().isEmpty()
                        && et_portText != null && !et_portText.toString().isEmpty()
                        && et_contentText != null && !et_contentText.toString().isEmpty()) {
                    ip = et_ipText.toString();
                    port = et_portText.toString();
                    content = et_contentText.toString();
                } else {
                    Toast.makeText(this, "check input ip,port,content.", Toast.LENGTH_SHORT).show();
                }
                connectServer(ip, port, content);
                break;
        }
    }


    public String longToDate(long lo) {
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        return sd.format(date);
    }

    private void connectServer(String ip, String port, String content) {
        //get socket output

        Thread connServerTh = new Thread(() -> {
            Socket socket = null;
            try {
                socket = new Socket(ip, Integer.parseInt(port));

                OutputStream out = socket.getOutputStream();

                String s1 = longToDate(System.currentTimeMillis());
                out.write(content.getBytes());
//                    out.flush();
                System.out.println("ok," + s1 + "," + content);
                handler.obtainMessage(1, "ok," + s1 + "," + content).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("socket.write() " + e.getMessage());
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    System.out.println("socket.close() " + e.getMessage());
                }
            }
        });
        connServerTh.start();
    }

}