package com.sadaf.iguardindia.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sadaf.iguardindia.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Viz extends AppCompatActivity {

    // Firebase parameters
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference = firebaseDatabase.getReference();
    private DatabaseReference mUserName = mRootReference.child("Sadaf");
    DatabaseReference mCurrImage = mUserName.child("CurrentImage");

    HashMap<Integer, HashMap<String, Float>> Results= new HashMap<Integer, HashMap<String, Float>>();
    float a,b,c,d,e,f;
    HashMap<String, Float> imagePredictions = new HashMap<String, Float>();
    TextView t1, t2, t3, t4, t5, t6;


    private LineChart lineChart;
    private int CurrentImageNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viz);

        t1 = (TextView)findViewById(R.id.textView11);
        t2 = (TextView)findViewById(R.id.textView12);
        t3 = (TextView)findViewById(R.id.textView13);
        t4 = (TextView)findViewById(R.id.textView14);
        t5 = (TextView)findViewById(R.id.textView15);
        t6 = (TextView)findViewById(R.id.textView16);

        lineChart = (LineChart)findViewById(R.id.line_chart);





    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CurrentImageNumber = Integer.parseInt(dataSnapshot.child("CurrentImage").getValue(String.class));
//                getData(dataSnapshot);

                Toast.makeText(getApplicationContext(), String.valueOf(CurrentImageNumber), Toast.LENGTH_LONG).show();
                for (int i = 1; i <= CurrentImageNumber; i++)
                {
                    a = Float.parseFloat(String.valueOf(dataSnapshot.child(String.valueOf(i)).child("Anger").getValue())) * 100;
                    b = Float.parseFloat(String.valueOf(dataSnapshot.child(String.valueOf(i)).child("Disgust").getValue())) * 100;
                    c = Float.parseFloat(String.valueOf(dataSnapshot.child(String.valueOf(i)).child("Fear").getValue())) * 100;
                    d = Float.parseFloat(String.valueOf(dataSnapshot.child(String.valueOf(i)).child("Happiness").getValue())) * 100;
                    e = Float.parseFloat(String.valueOf(dataSnapshot.child(String.valueOf(i)).child("Anger").getValue())) * 100;
                    f = Float.parseFloat(String.valueOf(dataSnapshot.child(String.valueOf(i)).child("Sadness").getValue())) * 100;

                    imagePredictions.put("Anger", a);
                    imagePredictions.put("Disgust", b);
                    imagePredictions.put("Fear", c);
                    imagePredictions.put("Happiness", d);
                    imagePredictions.put("Sadness", e);
                    imagePredictions.put("Surprise", f);

                    Results.put(i, imagePredictions);
                }
                drawChart1(imagePredictions);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        mUserName.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                CurrentImageNumber = Integer.parseInt(dataSnapshot.child("CurrentImage").getValue(String.class));
//
//                Toast.makeText(getApplicationContext(), String.valueOf(a), Toast.LENGTH_LONG).show();
////                Toast.makeText(getApplicationContext(), String.valueOf(b), Toast.LENGTH_LONG).show();
////                Toast.makeText(getApplicationContext(), String.valueOf(c), Toast.LENGTH_LONG).show();
////                Toast.makeText(getApplicationContext(), String.valueOf(d), Toast.LENGTH_LONG).show();
////                Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_LONG).show();
////                Toast.makeText(getApplicationContext(), String.valueOf(f), Toast.LENGTH_LONG).show();
//
//                imagePredictions.put("WTF", Float.parseFloat(String.valueOf(0.1231)));
////                Toast.makeText(getApplicationContext(), "Anger = " + String.valueOf(Float.parseFloat(String.valueOf(dataSnapshot.child(String.valueOf(1)).child("Anger").getValue())) * 100),Toast.LENGTH_LONG).show();
////                Toast.makeText(getApplicationContext(), "Anger = " + String.valueOf(imagePredictions.get("Anger")), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


//        float yValues [] = {10, 20, 30, 40, 50, 60};
//        float yValues [] = {a,b,c,d,e,f};

//        drawChart1(imagePredictions);
    }


//    public void getData(DataSnapshot dataSnapshot)
//    {
//
//    }
    public void drawChart(HashMap<Integer, HashMap<String, Float>> Results)
    {
        t1.setText(String.valueOf(Results.size()));

//        String xValues [] = {"Anger", "Disgust", "Fear", "Happiness", "Sadness", "Surprise"};
//
//        drawLineChart(yValues, xValues);
    }

    public void drawChart1(HashMap<String, Float> Results)
    {
        t1.setText(String.valueOf(Results.size()));
        int i = 1;
        for(Map.Entry<String, Float> entry: Results.entrySet()) {
            t1.setText(entry.getKey());
            t2.setText(String.valueOf(entry.getValue()));
            break;
        }
//        String xValues [] = {"Anger", "Disgust", "Fear", "Happiness", "Sadness", "Surprise"};
//
//        drawLineChart(yValues, xValues);
    }
    private void drawLineChart(float yValues[], String xValues[])
    {
        //lineChart.setDescription("Mental Health Report");
        ArrayList<Entry> yData = new ArrayList<>();



        for (int i = 0; i < yValues.length; i++)
        {
            yData.add(new Entry(yValues[i], i));
        }

        ArrayList<String> xData = new ArrayList<>();

        for (int i = 0; i < yValues.length; i++)
        {
            xData.add(xValues[i]);
        }

        LineDataSet lineDataset = new LineDataSet(yData, "Mental Health Report");
        lineDataset.setColors(ColorTemplate.COLORFUL_COLORS);

        LineData lineData = new LineData(lineDataset);
        lineData.setValueTextSize(13f);
        lineData.setValueTextColor(Color.BLACK);

        lineChart.setData(lineData);
        lineChart.invalidate();


    }

}
