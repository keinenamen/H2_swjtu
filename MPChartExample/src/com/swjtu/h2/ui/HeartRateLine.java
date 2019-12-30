
package com.swjtu.h2.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.swjtu.h2.R;
import com.swjtu.h2.custom.GloableData;
import com.swjtu.h2.custom.MyMarkerView;
import com.swjtu.h2.notimportant.DemoBase;
import com.swjtu.h2.process.DBUtils;
import com.swjtu.h2.process.Tools;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.swjtu.h2.process.ShowToast.ShowShorttoast;

public class HeartRateLine extends DemoBase implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    long now = 17753;//TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
    int num_line = 30;
    int query_data = 0;
    int select_num = 2;
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
    String str_to = dateformat.format(System.currentTimeMillis());
    String str_from = dateformat.format(System.currentTimeMillis()-(long)num_line*24*60*60*1000);

    ArrayList<Entry> values = new ArrayList<Entry>();
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if(((String) message.obj)==null||((String) message.obj).length() == 0){
                ShowShorttoast(getApplicationContext(),"您还没有数据.");
                Tools.dissProgDlg();
                return false;
            }else {
                String[] strs = ((String) message.obj).split("[;]");
                Arrays.sort(strs);
                int start_location = strs.length;
                if (start_location < num_line) {
                    num_line = start_location;
                }
                values.clear();
                query_data = 0;
                for (int x = start_location - num_line; x < start_location; x++) {
                    String[] strings = strs[x].split("[:]");
                    values.add(new Entry(Integer.parseInt(strings[0]), Integer.parseInt(strings[1])));
                    if (Integer.parseInt(strings[1]) > query_data) {
                        query_data = Integer.parseInt(strings[1]);
                    }//Log.d("TAG", strings[1]);
                }
                //System.out.println(values);
                tv_date1.setText("max：" + String.valueOf(query_data) + "次");
                tv_date.setText(String.valueOf(values.size()) + "条记录");
                data_chart();
                return false;
            }
        }
    });
    private LineChart mChart;
    private TextView tv_from1;
    private TextView tv_date1;
    private TextView tv_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.linechart_heartrate);
        setListener();
        tv_from1 = findViewById(R.id.tv_from1);
        tv_date1 = findViewById(R.id.tv_date1);
        tv_date = findViewById(R.id.tv_date);
        mChart = findViewById(R.id.chart1);
        chart_set(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()-(long)num_line*24*60*60*1000),TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

//        tvX.setText("" + (mSeekBarX.getProgress() + 1));
//        tvY.setText("" + (mSeekBarY.getProgress()));
//
//        setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());

        // redraw
        mChart.invalidate();
    }

    private void setData(long res1,long res2) {
        final String username = GloableData.mApp.getUuid();
        final long resfrom = res1;
        final long resto = res2;
        // Log.d("TAG", username);
        Tools.showProgDlg(HeartRateLine.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> mp =
                            DBUtils.getUserInfoByName(username,resfrom,resto);
                    Message msg = new Message();
                    if (mp == null) {
                        msg.what = 0;
                        msg.obj = null;
                        Log.d("TAG", " 数据不存在！");
                    } else {
                        String ss = new String();
                        for (String key : mp.keySet()) {
                            ss = ss + key + ":" + mp.get(key) + ";";
                        }
                        msg.what = 1;
                        msg.obj = ss;
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", "查询失败！");
                }
            }
        }).start();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());

        mChart.centerViewToAnimated(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    private void setListener() {
        findViewById(R.id.iv_head_left).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {//
                finish();
            }
        });
        findViewById(R.id.tv_head_return).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {//
                finish();
            }
        });
        findViewById(R.id.iv_head_set).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                final String dateto = dateformat.format(System.currentTimeMillis());
                final String datefrom = dateformat.format(System.currentTimeMillis()-(long)num_line*24*60*60*1000);

                final String items[] = {"一    周","两    周","一个月","三个月"};
                AlertDialog.Builder builder = new AlertDialog.Builder(HeartRateLine.this,AlertDialog.THEME_HOLO_LIGHT);
                builder.setTitle("请选择时间段");
                View view = LayoutInflater.from(HeartRateLine.this).inflate(R.layout.layout_dialog, null);
                //    设置我们自己定义的布局文件作为弹出框的Content
                builder.setView(view);
                final EditText from_year = (EditText)view.findViewById(R.id.fromyear);
                final EditText to_year = (EditText)view.findViewById(R.id.to_year);
                final EditText from_month = (EditText)view.findViewById(R.id.frommonth);
                final EditText to_month = (EditText)view.findViewById(R.id.to_month);
                final EditText from_date = (EditText)view.findViewById(R.id.fromdate);
                final EditText to_date = (EditText)view.findViewById(R.id.to_date);

                String str_to_org[]=str_to.split("[-]");
                to_year.setText(str_to_org[0]);
                to_month.setText(str_to_org[1]);
                to_date.setText(str_to_org[2]);
                String str_from_org[]=str_from.split("[-]");
                from_year.setText(str_from_org[0]);
                from_month.setText(str_from_org[1]);
                from_date.setText(str_from_org[2]);

                builder.setSingleChoiceItems(items, select_num, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int count = 0;
                        long time ;
                        String date ;
                        switch (i){
                            case 0:
                                count=7;
                                num_line=7;
                                select_num = 0;
                                break;
                            case 1:
                                count=14;
                                num_line=14;
                                select_num = 1;
                                break;
                            case 2:
                                count=30;
                                num_line=30;
                                select_num = 2;
                                break;
                            case 3:
                                count=90;
                                num_line=90;
                                select_num = 3;
                                break;
                        }
                        try {
                            time = dateformat.parse(dateto).getTime() - (long)count*24*60*60*1000;
                            date = dateformat.format(time);
                            String str_date[]=date.split("[-]");
                            from_year.setText(str_date[0]);
                            from_month.setText(str_date[1]);
                            from_date.setText(str_date[2]);
                            time = dateformat.parse(dateto).getTime();
                            date = dateformat.format(time);
                            String str_date1[] = date.split("[-]");
                            to_year.setText(str_date1[0]);
                            to_month.setText(str_date1[1]);
                            to_date.setText(str_date1[2]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        str_from = from_year.getText().toString()+"-"+from_month.getText()+"-"+from_date.getText();
                        str_to = to_year.getText().toString()+"-"+to_month.getText()+"-"+to_date.getText();
                        //System.out.println(str);
                        try {
                            long result_from = TimeUnit.MILLISECONDS.toDays(dateformat.parse(str_from).getTime());
                            long result_to = TimeUnit.MILLISECONDS.toDays(dateformat.parse(str_to).getTime());
                            num_line = (int)(result_to - result_from);
                            tv_from1.setText(str_from + " - " + str_to);
                            chart_set(result_from,result_to);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                builder.create().show();
            }
        });
    }
    public void data_chart(){

        LineDataSet set1;
        mChart.clear();

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            Tools.dissProgDlg();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "心率值");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            final DecimalFormat mFormat = new DecimalFormat("###,###,##0");
            set1.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return mFormat.format(value);
                }
            });
            // create a data object with the datasets
            LineData data = new LineData(set1);
            data.setValueTextColor(Color.BLACK);
            //data.setValueFormatter();
            data.setValueTextSize(9f);
            // set data
            mChart.setData(data);
            Tools.dissProgDlg();
        }
    }

    public void chart_set(long res1,long res2)
    {
        mChart.setOnChartValueSelectedListener(this);
        // no description text
        mChart.getDescription().setEnabled(false);
        // enable touch gestures
        mChart.setTouchEnabled(true);
        mChart.setDragDecelerationFrictionCoef(0.9f);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        // set an alternative background color
        //mChart.setBackgroundColor(Color.LTGRAY);
        // add data
        setData(res1,res2);
        mChart.animateX(2500);
        // get the legend (only possible after setting data)
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart
        Legend l = mChart.getLegend();
        // modify the legend ...
        l.setForm(LegendForm.LINE);
        l.setTypeface(mTfLight);
        //l.setTextSize(11f);
        //l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
//        l.setYOffset(11f);
        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        LimitLine ll1 = new LimitLine(100f, "心率上限：100Hz");
        ll1.setLineWidth(2f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLabelPosition.LEFT_TOP);
        ll1.setTextSize(9f);
        ll1.setTypeface(tf);
        LimitLine ll2 = new LimitLine(60f, "心率下限：60Hz");
        ll2.setLineWidth(2f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLabelPosition.LEFT_TOP);
        ll2.setTextSize(9f);
        ll2.setTypeface(tf);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(11f);
        //xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setCenterAxisLabels(true);//X lable center
        xAxis.setGranularity(1f); // one hour //最小粒度，绽放不会再细分
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("MM/dd");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                long millis = TimeUnit.DAYS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        //leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaximum(110f);
        leftAxis.setAxisMinimum(50f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(mTfLight);
        //rightAxis.setTextColor(Color.RED);
        rightAxis.setAxisMaximum(110f);
        rightAxis.setAxisMinimum(50f);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy.MM.dd");
        String dateto = dateformat.format(System.currentTimeMillis());
        String datefrom = dateformat.format(System.currentTimeMillis()-(long)num_line*24*60*60*1000);
        //tv_date1.setText(datefrom);
        tv_from1.setText(str_from + " - " + str_to);
    }
}
