package com.example.rvsm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.Chart;

import org.json.JSONObject;

import java.io.Console;
import java.net.URI;
import java.net.URL;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SignupActivity extends AppCompatActivity {
    private Button signup_btn, check_btn;
    private ImageButton signup_back_btn;
    private EditText et_id, et_pass, et_hcode, et_pcode;
    private Socket socket;
    private String icheck, pcheck;
    private boolean check;
    private String token;
    private IO.Options option;
    //private URI uri= URI.create("http://192.168.219.196:3333");//로컬테스팅용
    private URI uri= URI.create(BuildConfig.SERVER_URL);//실제 서버사용할때 씀
    //private URI uri= URI.create(BuildConfig.LOCAL_URL);//local

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signup_btn=findViewById(R.id.signup_btn);
        signup_back_btn=findViewById(R.id.signup_back_btn);
        et_id = findViewById(R.id.signup_id_et);
        et_pass = findViewById(R.id.signup_pw_et);
        et_hcode = findViewById(R.id.signup_h_code);
        et_pcode = findViewById(R.id.signup_p_code);
        check_btn = findViewById(R.id.bt_check);
        et_hcode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        option = new IO.Options();
        option.transports = new String[]{"websocket"};

        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        signup_back_btn.setOnClickListener(new View.OnClickListener() {//뒤로가기 버튼
            @Override
            public void onClick(View view) {
                socket.disconnect();
                finish();
            }
        });
        init();
    }

    private void init() {
        if(socket != null)
            return;
        try {
//            String host = "172.30.1.57";
//            int port = 3333;
//            URL url = new URL("http", host, port, "/");
//            socket = IO.socket(url.toURI());
            socket = IO.socket(uri,option);
            socket.connect();
            socket.on("check", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONObject data = (JSONObject) args[0];
                                pcheck = data.get("pcheck").toString();
                                System.out.println(pcheck);
                                if(pcheck.equals("true")){
                                    check = true;
                                    Toast.makeText(SignupActivity.this, "환자 정보가 확인되었습니다.", Toast.LENGTH_SHORT).show();
                                    et_hcode.setEnabled(false);
                                    et_hcode.setBackgroundColor(Color.rgb(202,202,202));
                                    et_pcode.setEnabled(false);
                                    et_pcode.setBackgroundColor(Color.rgb(202,202,202));
                                }else if(pcheck.equals("false")){
                                    Toast.makeText(SignupActivity.this, "해당 환자가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
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
            socket.on("SignUp", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject data = (JSONObject) args[0];
                                icheck = data.get("icheck").toString();
                                System.out.println("check" + check);
                                if(icheck.equals("false")){
                                    Toast.makeText(SignupActivity.this, "중복된 ID입니다.", Toast.LENGTH_SHORT).show();
                                }
                            } catch(Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        }
        socket.on("signup_success", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            System.out.println("들어옴");
                            JSONObject data = (JSONObject) args[0];
                            System.out.println(data.get("signup"));
                            if(data.get("signup").toString().equals("1")){
                                Toast.makeText(SignupActivity.this, "회원가입되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(SignupActivity.this, "아이디 또는 비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
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
        check_btn.setOnClickListener(new View.OnClickListener() {   //환자 정보 확인
            @Override
            public void onClick(View view) {
                check = false;
                if (socket != null) {
                    JSONObject data = new JSONObject();
                    try {
                        System.out.println("들어옴");
                        data.put("id", et_id.getText().toString());
                        data.put("h_code", et_hcode.getText().toString());
                        data.put("p_code", et_pcode.getText().toString());

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast
                                .LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    socket.emit("signup_check", data);
                }
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {  //회원가입 버튼 누른 경우
            @Override
            public void onClick(View v) {
                String id=et_id.getText().toString();
                String passwd=et_pass.getText().toString();

                if(!check){ //환자 정보 확인 버튼 false
                    Toast.makeText(SignupActivity.this, "환자 정보 학인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }else if(id.equals("")||passwd.equals("")){
                    Toast.makeText(SignupActivity.this, "아이디, 패스워드를 작성해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    if (socket != null) {
                        JSONObject data = new JSONObject();
                        try {
                            System.out.println("사용자 token = " + token);
                            System.out.println("icheck"+icheck);
                            data.put("id", id);
                            data.put("passwd", passwd);
                            data.put("h_code", et_hcode.getText().toString());
                            data.put("p_code", et_pcode.getText().toString());
                            data.put("token",token);

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast
                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        socket.emit("SignUp", data);
                    }
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.emit("disconnected", null);
        socket.disconnect();
    }
}