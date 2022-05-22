package com.example.rvsm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private Button login_btn;
    private ImageButton login_back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_btn=findViewById(R.id.login_btn);
        login_back_btn=findViewById(R.id.login_back_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), ChartActivity.class);
                startActivity(intent);
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
