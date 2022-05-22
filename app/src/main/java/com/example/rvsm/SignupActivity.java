package com.example.rvsm;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    private Button signup_btn;
    private ImageButton signup_back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signup_btn=findViewById(R.id.signup_btn);
        signup_back_btn=findViewById(R.id.signup_back_btn);
        signup_back_btn.setOnClickListener(new View.OnClickListener() {//뒤로가기 버튼
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}