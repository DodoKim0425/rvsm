package com.example.rvsm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import io.socket.client.IO;

import org.json.JSONObject;

import java.net.URL;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity {
    private Button login_btn;
    private ImageButton login_back_btn;
    private EditText id_et, pw_et;
    private Socket socket;
    private IO.Options option;
    private String lcheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_btn=findViewById(R.id.login_btn);
        login_back_btn=findViewById(R.id.login_back_btn);
        id_et = findViewById(R.id.login_id_et);
        pw_et = findViewById(R.id.login_pw_et);
        option = new IO.Options();
        option.transports = new String[]{"websocket"};
        if(socket != null)
            return;
        try {
            //String host = "172.30.1.43";
            //int port = 3333;
            //String url = "http://10.0.2.2:8080";
            //URL url = new URL("http", host, port, "/");
            //socket = IO.socket(url.toURI());
            socket = IO.socket("http://172.30.1.2:3333",option);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        login_btn.setOnClickListener(new View.OnClickListener() {   //로그인 버튼 클릭
            @Override
            public void onClick(View view) {
                if (socket != null) {
                    JSONObject data = new JSONObject();
                    try {
                        data.put("id", id_et.getText().toString());
                        data.put("pw", pw_et.getText().toString());
                        socket.emit("login_info", data);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast
                                .LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }socket.on("login_success", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    JSONObject data = (JSONObject) args[0];
                                    lcheck = data.get("login").toString();
                                    System.out.println(data.get("login"));
                                    if(lcheck.equals("1")){
                                        Toast.makeText(LoginActivity.this, "로그인되었습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(getApplicationContext(), ChartActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }catch(Exception e){
                                    Toast.makeText(getApplicationContext(), e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });
        login_back_btn.setOnClickListener(new View.OnClickListener() {//뒤로가기 버튼 클릭
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
