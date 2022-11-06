package com.example.rvsm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import io.socket.client.IO;

import org.json.JSONException;
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
    private int lcheck;
   // private URI uri= URI.create("http://192.168.219.196:3333");//로컬테스팅용
    private URI uri= URI.create(BuildConfig.SERVER_URL);//실제 서버사용할때 씀
    //private URI uri= URI.create(BuildConfig.LOCAL_URL);//local

    private String token="";

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

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                            System.out.println("get token fail!");
                            return;
                        }
                        String msg=task.getResult();
                        Log.println(Log.INFO,"token!!!:",msg);
                        token=task.getResult();
                    }
                });

        if(socket != null)
            return;

        connect();//소켓 연결 및 이벤트 등록

        login_btn.setOnClickListener(new View.OnClickListener() {   //로그인 버튼 클릭
            @Override
            public void onClick(View view) {
                login();
            }
        });
        login_back_btn.setOnClickListener(new View.OnClickListener() {//뒤로가기 버튼 클릭
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void connect(){
        try{
            //String host = "172.30.1.43";
            //int port = 3333;
            //String url = "http://10.0.2.2:8080";
            //URL url = new URL("http", host, port, "/");
            //socket = IO.socket(url.toURI());
            socket = IO.socket(uri,option);
            socket.connect();
            socket.on("login_success", new Emitter.Listener() {//로그인 성공여부 이벤트 등록
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONObject data = (JSONObject) args[0];
                                lcheck = (data.getInt("login"));
                                System.out.println(data.get("login"));
                                if(lcheck==1){
                                    Toast.makeText(LoginActivity.this, "로그인되었습니다.", Toast.LENGTH_SHORT).show();
                                    String patientID=(data.getString("patientID"));
                                    Intent intent=new Intent(getApplicationContext(), ChartActivity.class);
                                    intent.putExtra("patientID",patientID);
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

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("연결실패");
        }
    }

    private void login(){
        if (socket != null) {
            JSONObject data = new JSONObject();
            try {
                data.put("id", id_et.getText().toString());
                data.put("pw", pw_et.getText().toString());
                data.put("token",token);
                socket.emit("login_info", data);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast
                        .LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}
