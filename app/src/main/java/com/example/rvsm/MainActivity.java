package com.example.rvsm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private Button main_signup_btn,main_login_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_signup_btn=findViewById(R.id.main_signup_btn);
        main_login_btn=findViewById(R.id.main_login_btn);
        main_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        main_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                            Log.w("TAG","fetching FCM registration token failed",task.getException());
                            return;
                        }
                        String msg=task.getResult();
                        Log.println(Log.INFO,"token!!!:",msg);

                    }
                });
    }
}
