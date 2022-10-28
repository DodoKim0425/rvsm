package com.example.rvsm;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChartActivity extends AppCompatActivity {
    private String patient_name;//환자이름
    private String admin_name;//담당자이름
    private String patient_id;//환자 id
    private TextView tv_patient_name,tv_admin;
    private Socket mSocket;//소켓통신을 위한 소켓 객체
    private LineChart lineChart;//차트 전체
    private List<Entry> entryList1;//data1을 나타내는 데이터들
    private List<Entry> entryList2;//data2을 나타내는 데이터들
    private List<Entry> entryList3;//data2을 나타내는 데이터들
    private LineDataSet dataSet1,dataSet2,dataSet3;//data1,data2의 선
    private Thread thread;
    private int time=0;
    private String last_time;
    private ArrayList<String> xVals;//x축 날짜, 시간
    private ImageButton chart_back_btn;
    private URI uri= URI.create("http://10.0.2.2:3333");
    private IO.Options options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_chart);



        chart_back_btn=findViewById(R.id.chart_back_btn);
        tv_admin=findViewById(R.id.tv_admin);
        tv_patient_name=findViewById(R.id.tv_patient_name);
        patient_id="test1";
        lineChart=findViewById(R.id.lineChart);
        entryList1=new ArrayList<Entry>();
        entryList2=new ArrayList<Entry>();
        entryList3=new ArrayList<Entry>();
        xVals=new ArrayList<String>();
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.SECOND,-5);
        Date date=new Date(cal.getTimeInMillis());
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String getTime=dateFormat.format(date);
        //last_time=getTime;//최초 연결시 현재 시간 기준으로 5초 전 데이터까지 가져온다 실제 테스트시 이 부분 사용함
        last_time="2022-06-11 12:30:26";//aws 서버 db연결 테스트를 위한 임시코드입니다
        System.out.println("현재시간-5: "+last_time);

        options=new IO.Options();
        options.transports=new String[]{"websocket"};
        chart_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        connect();//연결

        feedMultiple();

    }
    void connect(){//연결함수
        System.out.println("연결함수에 들어옴");
        try {
            System.out.println("연결시도");
            //mSocket = IO.socket("https://13.125.227.19:3000");//서버 주소, 포트는 3005, 서버코드 합친 후 포트변경필요
            //mSocket=IO.socket("https://10.0.2.2:3333");//로컬호스트에서 테스팅시 사용
            mSocket=IO.socket(uri,options);
            mSocket.connect();
            System.out.println("연결성공");
            Log.d("SOCKET", "Connection success : " + mSocket.id());

            mSocket.on("response_bio_data", new Emitter.Listener() {//데이터 받는 이벤트
                @Override
                public void call(Object... args) {
                    try{
                        JSONArray data=(JSONArray) args[0];
                        doJSONParser(data);
                        System.out.println(data.length());
                    }catch (Exception e){
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(getApplicationContext(), e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();

                            }
                        }, 0);

                    }
                }
            });
            mSocket.on("response_user_info", new Emitter.Listener() {//데이터 받는 이벤트
                @Override
                public void call(Object... args) {
                    try{
                        JSONObject data=(JSONObject) args[0];
                        admin_name=(data.getString("admin_name"));
                        patient_name=(data.getString("patient_name"));
                        Log.d("name","name!!!: "+patient_name);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_admin.setText(admin_name);
                                tv_patient_name.setText(patient_name);
                            }
                        });
                    }catch (Exception e){
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(getApplicationContext(), e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();

                            }
                        }, 0);

                    }
                }
            });
            mSocket.emit("request_user_info",patient_id);
        } catch (Exception e) {
            System.out.println("연결실패");
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        }

    }
    void get_data(){
        JSONObject data=new JSONObject();
        System.out.println("보낼시간: "+last_time);
        try {
            data.put("time",last_time);
            data.put("user_name","PATIENT_STATE");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("request_bio_data",data);//서버에 데이터 요청

    }
    void doJSONParser(JSONArray index){//서버로 부터 받은 jsonobject에서 파싱

        try{

                last_time=index.getJSONObject(index.length()-1).getString("TIME");
                for (int i = 0; i < index.length(); i++) {
                    JSONObject jsonObject_data = index.getJSONObject(i);
                    entryList1.add(new Entry(time,Integer.parseInt(jsonObject_data.getString("HeartRate"))));
                    //entryList2.add(new Entry(time,Float.parseFloat(jsonObject_data.getString("Temperature"))));
                    entryList3.add(new Entry(time,Integer.parseInt(jsonObject_data.getString("Spo2"))));
                    time++;
                    String[]arr=jsonObject_data.getString("TIME").split(" ");
                    xVals.add(arr[1]);
                /*if(i==(index.length()-1)){
                    last_time=jsonObject_data.getString("TIME");
                }*/
                }


            drawGraph();//그래프 그리기
        }
        catch (JSONException e){ System.out.println("파서에러");}


    }
    private void drawGraph(){//그래프를 그린다
        try{
            XAxis xAxis=lineChart.getXAxis();
            xAxis.setDrawLabels(true);
            xAxis.setPosition(XAxis.XAxisPosition.TOP);//x레이블 위치
            xAxis.setTextColor(Color.rgb(118,118,118));//x레이블 글자 색

            xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));//x레이블 표시될값을 넣는다
            xAxis.enableGridDashedLine(10,24,0);//x레이블 점선 설정
            xAxis.setGranularity(1f);

            xAxis.setLabelRotationAngle(315f);//x레이블 기울기

            YAxis yAxis=lineChart.getAxisLeft();
            yAxis.setAxisMinimum(0);
            yAxis.setAxisMaximum(110);
            dataSet1=new LineDataSet(entryList1,"HeartRate");//선모양을 정해주는 데이터를 넣음

            //dataSet2=new LineDataSet(entryList2,"Temperature");

            dataSet3=new LineDataSet(entryList3,"Spo2");

            //dataset1선 모양을 정함
            dataSet1.setDrawValues(false);
            dataSet1.setColor(Color.parseColor("#ff6384"));
            dataSet1.setDrawCircles(false);
            dataSet1.setLineWidth(3);

            //dataset2선 모양 정함
            //dataSet2.setDrawValues(false);
           // dataSet2.setColor(Color.parseColor("#F7C69A"));
           // dataSet2.setDrawCircles(false);
            //dataSet2.setLineWidth(3);

           // dataset3선 모양 정함
            dataSet3.setDrawValues(false);
            dataSet3.setColor(Color.parseColor("#5762ff"));
            dataSet3.setDrawCircles(false);
            dataSet3.setLineWidth(3);

            LineData chartData=new LineData();//선들여러개를 묶는객체임
            chartData.addDataSet(dataSet1);
           // chartData.addDataSet(dataSet2);
            chartData.addDataSet(dataSet3);

            lineChart.setData(chartData);//그래프위에 선들을 나타냄

            lineChart.setVisibleXRangeMaximum(6);//한 그래프위에 6개의 데이터만 보이도록함

            lineChart.moveViewToX(dataSet1.getEntryCount());//x축을 가장 최근값이 보이도록 옮김, 가장 최근의 20개만 보이게됨

        }catch (Exception e){
            System.out.println("그리는데 문제가 생김");
            e.printStackTrace();
        }

    }
    private void feedMultiple() {
        if (thread != null) thread.interrupt();
        final Runnable runnable = new Runnable() {
            @Override public void run() {
                get_data();
            }
        };
        thread = new Thread(new Runnable() {
            @Override public void run() {
                while (true) {
                    if(mSocket!=null){
                        runOnUiThread(runnable);
                    }
                    try {
                        Thread.sleep(3000);//3초에 한번씩 데이터 불러옴
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    @Override protected void onPause() {
        super.onPause();
        if (thread != null) thread.interrupt();
    }

    protected void onDestroy(){
        super.onDestroy();
        // mSocket.emit("disconnect",null);
        mSocket.disconnect();
    }
}

