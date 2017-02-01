package com.udacity.stockhawk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

import static android.R.attr.x;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        String history = getIntent().getStringExtra(MainActivity.GRAPH_INTENT_EXTRA);
        LineChart chart = (LineChart) findViewById(R.id.linechart);

        LineDataSet dataSet = new LineDataSet(getList(history), this.getString(R.string.label));
        dataSet.setValueTextColor(android.R.color.white);
        chart.setData(new LineData(dataSet));

        Description desc = new Description();
        desc.setText(this.getString(R.string.description));
        chart.setDescription(desc);

        chart.getXAxis().setValueFormatter(new MyXAxisValueFormatter());
        chart.getAxisLeft().setValueFormatter(new MyYAxisValueFormatter());
        chart.getAxisRight().setValueFormatter(new MyYAxisValueFormatter());
        //refresh the chart
        chart.invalidate();

    }

    public List<Entry> getList(String history) {
        String points[];
        String values[];
        points = history.split("\n");
        List<Entry> entries = new ArrayList<>();
        int end = points.length -1;

        //Parsing from the back to include the dataset in increasing order
        for (int ind = end ; ind >= 0 ; ind--){
            values = points[ind].split(",");
            entries.add(new Entry(Long.valueOf(values[0].trim()), Float.valueOf(values[1].trim())));

        }

        return entries;
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter{

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis((long) value);
            return formatter.format(calendar.getTime());
        }
    }

    public class MyYAxisValueFormatter implements IAxisValueFormatter{

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return String.format(GraphActivity.this.getString(R.string.dollars), (int)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              value);
        }
    }

}
