package com.example.rvsm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//출처: https://blog.naver.com/PostView.nhn?blogId=phj8498&logNo=221346945899
public class MainActivity extends AppCompatActivity {
    private LineChart lineChart;
    private  List<Entry> entryList1;
    private  List<Entry> entryList2;
    private  List<Entry> entryList3;
    private  List<Entry> entryList4;
    private  List<Entry> entryList5;
    private  List<Entry> entryList6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 위젯에 대한 참조.
        lineChart=(LineChart)findViewById(R.id.lineChart);
        // URL 설정.
        String url = "http://13.125.245.183/andtest.php";

        // AsyncTask를 통해 HttpURLConnection 수행.
        NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute();
        entryList1=new ArrayList<Entry>();
        entryList2=new ArrayList<Entry>();
        entryList3=new ArrayList<Entry>();
        entryList4=new ArrayList<Entry>();
        entryList5=new ArrayList<Entry>();
        entryList6=new ArrayList<Entry>();

    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

            doJSONParser(s);
        }
    }
    void doJSONParser(String str){
        int time=0;//시간
        try{
            String result = "";
            JSONObject order = new JSONObject(str);
            JSONArray index = order.getJSONArray("bio");
            for (int i = 0; i < index.length(); i++) {
                JSONObject tt = index.getJSONObject(i);
                entryList1.add(new Entry(time,Integer.parseInt(tt.getString("data1"))));
                entryList2.add(new Entry(time,Float.parseFloat(tt.getString("data2"))));
                entryList3.add(new Entry(time,Integer.parseInt(tt.getString("data3"))));
                entryList4.add(new Entry(time,Integer.parseInt(tt.getString("data4"))));
                entryList5.add(new Entry(time,Integer.parseInt(tt.getString("data5"))));
                entryList6.add(new Entry(time,Integer.parseInt(tt.getString("data6"))));
                time++;
            }
            LineDataSet dataSet1=new LineDataSet(entryList1,"data1");
            dataSet1.setDrawValues(false);
            dataSet1.setColor(Color.parseColor("#F79AB3"));
            dataSet1.setDrawCircles(false);
            dataSet1.setLineWidth(3);

            LineDataSet dataSet2=new LineDataSet(entryList2,"data2");
            dataSet2.setDrawValues(false);
            dataSet2.setColor(Color.parseColor("#F7C69A"));
            dataSet2.setDrawCircles(false);
            dataSet2.setLineWidth(3);


            LineDataSet dataSet3=new LineDataSet(entryList3,"data3");
            dataSet3.setDrawValues(false);
            dataSet3.setColor(Color.parseColor("#ECF99B"));
            dataSet3.setDrawCircles(false);
            dataSet3.setLineWidth(3);


            LineDataSet dataSet4=new LineDataSet(entryList4,"data4");
            dataSet4.setDrawValues(false);
            dataSet4.setColor(Color.parseColor("#9BF99E"));
            dataSet4.setDrawCircles(false);
            dataSet4.setLineWidth(3);


            LineDataSet dataSet5=new LineDataSet(entryList5,"data5");
            dataSet5.setDrawValues(false);
            dataSet5.setColor(Color.parseColor("#9AD8F7"));
            dataSet5.setDrawCircles(false);
            dataSet5.setLineWidth(3);


            LineDataSet dataSet6=new LineDataSet(entryList6,"data6");
            dataSet6.setDrawValues(false);
            dataSet6.setColor(Color.parseColor("#E89AF7"));
            dataSet6.setDrawCircles(false);
            dataSet6.setLineWidth(3);


            LineData chartData=new LineData();
            chartData.addDataSet(dataSet1);
            chartData.addDataSet(dataSet2);
            chartData.addDataSet(dataSet3);
            chartData.addDataSet(dataSet4);
            chartData.addDataSet(dataSet5);
            chartData.addDataSet(dataSet6);

            lineChart.setData(chartData);
            lineChart.invalidate();
        }
        catch (JSONException e){ ;}


    }



}