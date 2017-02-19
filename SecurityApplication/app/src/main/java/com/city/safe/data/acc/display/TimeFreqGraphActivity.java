package com.city.safe.data.acc.display;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.city.safe.R;
import com.city.safe.data.acc.FileService;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TimeFreqGraphActivity extends Activity {

    public static final int IFreqCount = 4096 ;

    private ImageButton btnTimeButto ;
    private ImageButton btnFreqButto ;
    private TextView tvFilename ;
    private TextView tvFilename1 ;
    private GraphicalView chart;
    private GraphicalView chartFreq;
    private XYMultipleSeriesDataset dataset1;
    LinearLayout timeLayout;
    /**曲线数量*/
    private static final int SERIES_NR=1;
    private XYSeries series1;
    private static final String TAG = "message";
    LinearLayout freqLayout;


    FileService service = null;
    //chart
    private XYMultipleSeriesDataset dataset=null;
    private XYSeries seriesXY;
    private XYSeries series2;

    private String strFileName="" ;
    private String strFilePath="";
    private String strPhotoName="" ;
    private Algo	algObj = null ;

    List<Float> listY = null;
    float  fFreqXMax = 0;
    float  fFreqYMax = 0 ;

    float fDensity = 3.9f ;
    boolean bFreqView = false ;

    List<Float> lstselX = null ;
    List<Float> lstselY = null ;

    float fVagFreq = 100f ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.graph);
//		Toast.makeText(TimeFreqGraphActivity.this,"TimeFreq OnCreate Begin", Toast.LENGTH_LONG).show();
        tvFilename = (TextView)findViewById(R.id.id_filename);
        tvFilename1 = (TextView)findViewById(R.id.id_filename1);
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        if(bundle!=null){
            strFileName = bundle.getString("filename");
            strFilePath =bundle.getString("filepath");
            strPhotoName = bundle.getString("photoname");
            //           Toast.makeText(TimeFreqGraphActivity.this,strFileName, Toast.LENGTH_LONG).show();
        }
        tvFilename.setText(strFileName);
        tvFilename1.setText(strFileName);
//        Toast.makeText(getApplicationContext(), strFileName,Toast.LENGTH_SHORT).show();
        timeLayout = (LinearLayout)findViewById(R.id.id_graph_senser_time);
        //生成图表
//		chart = ChartFactory.getTimeChartView(this, getDateDemoDataset(), getDemoRenderer(), "ss");//hh:mm:ss
//        initTimeView(-10,10);
        timeLayout.setVisibility(View.VISIBLE);
        freqLayout = (LinearLayout)findViewById(R.id.id_graph_senser_freq);
        freqLayout.setVisibility(View.INVISIBLE);

        btnTimeButto = (ImageButton) findViewById(R.id.imgBtnLeft);
        btnTimeButto.setOnClickListener(new LeftListener()) ;

        btnFreqButto = (ImageButton) findViewById(R.id.imgBtnRight);
        btnFreqButto.setOnClickListener(new RightListener()) ;

        ImageButton btnRight = (ImageButton) findViewById(R.id.imageRight);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //delete by hongtao.fu
                if(!bFreqView){
                    Toast.makeText(getApplicationContext(), "Need Chick Freq button!",Toast.LENGTH_LONG).show();
                    return;
                }
                //计算△f
                if(lstselX.size()>1){
                    float fMin = 0,fMax = 0 ;
                    for(int n=0;n<lstselX.size();n++){
                        if(n==0)
                            fMin=fMax=lstselX.get(n) ;
                        else{
                            if(fMin>lstselX.get(n))
                                fMin = lstselX.get(n) ;
                            if(fMax<lstselX.get(n))
                                fMax = lstselX.get(n) ;
                        }
                    }
                    fDensity = (fMax-fMin)/(lstselX.size()-1) ;
                }
                Intent intent = new Intent();
                intent.setClass(TimeFreqGraphActivity.this, CableForceCalcActivity.class);
                //用Bundle携带数据
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putString("filename", strFileName);
                bundle.putString("filepath", strFilePath);
                bundle.putFloat("density", fDensity);
                bundle.putFloat("vagdensity", fVagFreq);
//                bundle.putString("photoname", strPhotoName);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }) ;

        Button btnBack = (Button) findViewById(R.id.btn_graphLeftTop);
        btnBack.setOnClickListener(new BackListener());
        service = new FileService(getApplicationContext()) ;
        updateChartHistory();

        algObj = new Algo() ;

        btnTimeButto.setImageResource(R.drawable.freq2);
        btnFreqButto.setImageResource(R.drawable.time1) ;
        View timeView = (View) findViewById(R.id.id_graph_senser_time);
        timeView.setVisibility(View.INVISIBLE);
        View FreqView = (View) findViewById(R.id.id_graph_senser_freq);
        FreqView.setVisibility(View.VISIBLE);
        TextView midText = (TextView) findViewById(R.id.id_graph_mid_text);
        midText.setText("Frequency domain graph");
        lstselX = new ArrayList<Float>() ;
        lstselY = new ArrayList<Float>()  ;
        updateFreqchartHistory() ;
        bFreqView = true ;
    }

    class LeftListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //		btnTimeButto.setBackgroundResource(R.drawable.freq1) ;
            btnTimeButto.setImageResource(R.drawable.freq2);
            btnFreqButto.setImageResource(R.drawable.time1) ;
            View timeView = (View) findViewById(R.id.id_graph_senser_time);
            timeView.setVisibility(View.INVISIBLE);
            View FreqView = (View) findViewById(R.id.id_graph_senser_freq);
            FreqView.setVisibility(View.VISIBLE);
            TextView midText = (TextView) findViewById(R.id.id_graph_mid_text);
            midText.setText("Frequency domain graph");
            lstselX = new ArrayList<Float>() ;
            lstselY = new ArrayList<Float>()  ;
            updateFreqchartHistory() ;
            bFreqView = true ;
        }
    }

    class RightListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            btnTimeButto.setImageResource(R.drawable.freq1) ;
            btnFreqButto.setImageResource(R.drawable.time2) ;
            View timeView = (View) findViewById(R.id.id_graph_senser_time);
            timeView.setVisibility(View.VISIBLE);
            View FreqView = (View) findViewById(R.id.id_graph_senser_freq);
            FreqView.setVisibility(View.INVISIBLE);
            TextView midText = (TextView) findViewById(R.id.id_graph_mid_text);
            midText.setText("Time domain data graph");
//			bFreqView = false ;
        }
    }

    class BackListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            finish();
        }
    }
    //设定如表样式
    private XYMultipleSeriesRenderer getDemoRenderer(float fXmin,float fXmax) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Sensor");//标题
        renderer.setChartTitleTextSize(20);
        renderer.setXTitle("Time(ms)");    //x轴说明
        renderer.setYTitle("Acceleration(m/s^2)");
        renderer.setAxisTitleTextSize(16);
        renderer.setAxesColor(Color.BLUE);// 设置XY轴颜色
        renderer.setLabelsTextSize(18);    //数轴刻度字体大小
        renderer.setLabelsColor(Color.BLACK);// 设置轴标签颜色
        renderer.setLegendTextSize(15);    //曲线说明
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0,Color.BLACK);
        renderer.setShowLegend(false);
        renderer.setMargins(new int[] {20, 15, 15, 10});
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.RED);
        r.setChartValuesTextSize(15);
        r.setChartValuesSpacing(3);
        r.setPointStyle(PointStyle.CIRCLE);
        r.setFillPoints(true);
//	    r.setFillBelowLine(true);
        renderer.addSeriesRenderer(r);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setShowGrid(true);
        renderer.setYAxisMax(fXmax);
        renderer.setYAxisMin(fXmin);
        renderer.setInScroll(true);  //调整大小
//	    renderer.setXAxisMin(0); // 设置X轴起点
//	    renderer.setXAxisMax(600); // 设置X轴最大点

        // 设置x轴和y轴的标签对齐方式
        renderer.setXLabelsAlign(Align.CENTER); //
        renderer.setYLabelsAlign(Align.LEFT);
        // 设置x轴标签数
        renderer.setXLabels(10);
        renderer.setYLabels(7);

        renderer.setBackgroundColor(Color.WHITE);//TRANSPARENT
        renderer.setApplyBackgroundColor(true);

        renderer.setClickEnabled(true);
        renderer.setSelectableBuffer(30);

        renderer.setPanEnabled(true) ;
        renderer.setPanEnabled(true, true) ;


        return renderer;
    }
    /**
     * 数据对象
     * @return
     */
    private XYMultipleSeriesDataset getDateDemoDataset() {
        dataset1 = new XYMultipleSeriesDataset();

        series1 = new XYSeries("series1 ");

        dataset1.addSeries(series1);

        Log.i(TAG, dataset1.toString());
        return dataset1;
    }

    private void initTimeView(float Xmin,float XMax){
        chart = ChartFactory.getCubeLineChartView(getApplicationContext(), getDateDemoDataset(), getDemoRenderer(Xmin,XMax), 0.1f);
        timeLayout.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.FILL_PARENT));

        chart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GraphicalView gv = (GraphicalView) v;
                // 将view转换为可以监听的GraphicalView
                SeriesSelection ss = gv.getCurrentSeriesAndPoint();
                // 获得被点击的系列和点
                if (ss == null)
                    return;
//				double[] point = new double[] { ss.getXValue(), ss.getValue() };
//				// 获得当前被点击点的X位置和Y数值
//				// final double[] dest = xychart.toRealPoint(point);
//				// 获得当前被点击点的坐标位置
//
//				 Toast.makeText(getApplicationContext(),
//				 "点击了("+point[0]+","+point[1]+")点\n它在屏幕上的坐标为:", 1).show();
            }
        });
    }

    private void initFreqView(double[] xyRange) {
        // 柱状图的两个序列的名字
        String[] titles = new String[] { "Data","Peak value"  };
        // 存放柱状图两个序列的值
        ArrayList<double[]> value = new ArrayList<double[]>();
        double[] d1 = new double[] { 0.1, 0.3, 0.7, 0.8, 0.5 };
        double[] d2 = new double[] { 0.2, 0.3, 0.4, 0.8, 0.6 };
        value.add(d1);
        value.add(d2);
        // 两个状的颜色
        int[] colors = { Color.RED, Color.BLUE };

        // 为li1添加柱状图
        chartFreq = xychar(titles, value, colors, 6, 5, xyRange, new int[] { 1, 2, 3, 4, 5 }, "Frequency graph", true);
        freqLayout.addView(chartFreq, new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        chartFreq.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GraphicalView gv = (GraphicalView) v;
                // 将view转换为可以监听的GraphicalView
                SeriesSelection ss = gv.getCurrentSeriesAndPoint();
                // 获得被点击的系列和点
                if (ss == null)
                    return;
                double[] point = new double[] { ss.getXValue(), ss.getValue() };
                // 获得当前被点击点的X位置和Y数值
                // final double[] dest = xychart.toRealPoint(point);
                // 获得当前被点击点的坐标位置

//				 Toast.makeText(getApplicationContext(),
//				 "点击了("+point[0]+","+point[1]+")点\n它在屏幕上的坐标为:", 1).show();

                series2.clear() ;
                boolean bFind = false ;
                for(int n=0;n<lstselX.size();n++){

                    if(lstselX.get(n)==(float) point[0]){
                        lstselX.remove(n) ;
                        lstselY.remove(n) ;
                        bFind = true ;
                        break ;
                    }
                }
                if (!bFind) {
                    lstselX.add((float) point[0]);
                    lstselY.add((float) point[1]);
                }
                for (int n = 0; n < lstselX.size(); n++) {
                    series2.add(lstselX.get(n), lstselY.get(n));
                }

                if(lstselX.size()>0){

                    dataset.removeSeries(series2);
                    dataset.addSeries(series2);

//					dataset.removeSeries(seriesXY);
//					dataset.addSeries(seriesXY);
                }

                chartFreq.invalidate();
            }
        });
    }

    public GraphicalView xychar(String[] titles, ArrayList<double[]> value,
                                int[] colors, int x, int y, double[] range, int[] xLable,
                                String xtitle, boolean f) {
        // 多个渲染
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        // 多个序列的数据集
        dataset = new XYMultipleSeriesDataset();
        // 构建数据集以及渲染
        seriesXY = new XYSeries(titles[0]);
        series2 = new XYSeries(titles[1]);
        for (int i = 0; i < titles.length; i++) {
            if (i == 0) {
                double[] yLable = value.get(i);
                for (int j = 0; j < yLable.length; j++) {
                    seriesXY.add(xLable[j], yLable[j]);
                }
                dataset.addSeries(seriesXY);
            } else {
                series2.add(2009.5, 1);
                dataset.addSeries(series2);
            }

            XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
            // 设置颜色
            xyRenderer.setColor(colors[i]);
            // 设置点的样式 //
            if (i == 0)
            {
                xyRenderer.setPointStyle(PointStyle.CIRCLE);
                xyRenderer.setFillPoints(true);
            }
            else
            {
                xyRenderer.setPointStyle(PointStyle.SQUARE);
                xyRenderer.setFillPoints(false);
            }
            // 将要绘制的点添加到坐标绘制中

            renderer.addSeriesRenderer(xyRenderer);

        }
        // 设置x轴标签数
        renderer.setXLabels(15);
        // 设置Y轴标签数
        renderer.setYLabels(y);
//		// 设置x轴的最小值
//		renderer.setXAxisMin(0.1);
//		// 设置x轴的最大值
//		renderer.setXAxisMax(1.5);
        renderer.setAxesColor(Color.BLUE);// 设置XY轴颜色
        // 设置x轴和y轴的标签对齐方式
        renderer.setXLabelsAlign(Align.CENTER); //
        renderer.setYLabelsAlign(Align.LEFT);
        // 设置现实网格
        renderer.setShowGrid(true);

        renderer.setShowAxes(true);
        // 设置条形图之间的距离
//		renderer.setBarSpacing(5.9);
//		renderer.setBarWidth(5.5f);
        renderer.setInScroll(false);
//		// 设置x轴和y轴标签的颜色
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0, Color.BLACK);
        //数轴刻度字体大小
        renderer.setLabelsTextSize(18);

        int length = renderer.getSeriesRendererCount();
        // 设置图标的标题
        renderer.setChartTitle(xtitle);
        renderer.setLabelsColor(Color.RED);

        // 设置图例的字体大小
        renderer.setLegendTextSize(18);
        // 设置x轴和y轴的最大最小值
        renderer.setRange(range);
        renderer.setMarginsColor(0x00888888);

        renderer.setBackgroundColor(Color.WHITE);// TRANSPARENT
        renderer.setApplyBackgroundColor(true);

        // click
        renderer.setClickEnabled(true);
        renderer.setSelectableBuffer(40);

        renderer.setMargins(new int[] {25, 15, 15, 10});

        renderer.setPanEnabled(true, true) ;
        renderer.setPanEnabled(true) ;

//		for (int i = 0; i < length; i++) {
//			SimpleSeriesRenderer ssr = renderer.getSeriesRendererAt(i);
//			ssr.setChartValuesTextAlign(Align.RIGHT);
//			ssr.setChartValuesTextSize(12);
//			ssr.setDisplayChartValues(f);
//		}
//		GraphicalView mChartView = ChartFactory.getBarChartView(  //getCubeLineChartView
        GraphicalView mChartView = ChartFactory.getCubeLineChartView(
                getApplicationContext(), dataset, renderer, 0.1f);  //Type.DEFAULT

        return mChartView;

    }

    private void updateChartHistory() {
//		Toast.makeText(TimeFreqGraphActivity.this,"update history begin", Toast.LENGTH_LONG).show();
        if(strFileName.equals("")){
            return ;
        }
        String strname = new String("") ;
//		try {
//			strname = service.read(strFileName) ;
//		 } catch (Exception e) {
//			 return;
//		}

        FileInputStream pStream = null ;
//        String sDir = Environment.getExternalStorageDirectory().toString()+"/citysafe" ;
        File picFile = new File(strFilePath);
        try {
            pStream = new FileInputStream(picFile);
            byte[] b = new byte[pStream.available()];// 新建一个字节数组
            pStream.read(b);// 将文件中的内容读取到字节数组中
            pStream.close();
            strname = new String(b);
            pStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(TimeFreqGraphActivity.this,
                    "FileNotFoundException", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(TimeFreqGraphActivity.this, "IOException",
                    Toast.LENGTH_LONG).show();
        }
        //矫正数据
        List<String> list = new ArrayList<String>();
        if(listY==null){
            listY = new ArrayList<Float>();
        }
        else{
            listY.clear();
        }
        StringTokenizer token = new StringTokenizer(strname, "\n");
        while (token.hasMoreTokens()) {
            list.add(token.nextToken());
        }
        int length = list.size()-1;

        //取2的n次方
        int nTemp = 1 ;
        for( int nLen = 0;;nLen++ ){
            nTemp*=2 ;
            if(nTemp>length){
                length = nTemp/2 ;
                break ;
            }
        }

        float[] ycache = new float[length];
        String timeStart = "" ;
        String timeEnd = "" ;
        String tempData = "";
        for(int nData=0;nData<length;nData++){
            tempData = list.get(nData) ;
            String[] arrTemp = tempData.split(" ") ;
            if(arrTemp.length>=9){
                if(nData==0||nData==(length-1)){
                    StringBuffer buf = new StringBuffer("") ;
                    buf.append(arrTemp[0]) ;
                    buf.append(" ") ;
                    buf.append(arrTemp[1]) ;
                    buf.append(" ") ;
                    buf.append(arrTemp[2]) ;
                    buf.append(" ") ;
                    buf.append(arrTemp[3]) ;
                    buf.append(" ") ;
                    buf.append(arrTemp[4]) ;
                    buf.append(" ") ;
                    buf.append(arrTemp[5]) ;
                    if(nData==0)
                        timeStart = buf.toString() ;
                    if(nData==length-1)
                        timeEnd = buf.toString() ;
                }
                ycache[nData]= Float.valueOf(arrTemp[8]);
            }
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd HH mm ss.SSS");
        long ltimeStart = 0;
        long  limeEnd = 1;
        try {
            ltimeStart = formatter.parse(timeStart).getTime();
            limeEnd = formatter.parse(timeEnd).getTime() ;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        fVagFreq = 1000*(float)length/(limeEnd-ltimeStart) ;
        Toast.makeText(TimeFreqGraphActivity.this,String.valueOf(fVagFreq), Toast.LENGTH_LONG).show();

        int[] xcache = new int[length];


        // float addY= 0f ; //random.nextInt()%10;//huqb
        // long addX= new Date().getTime();

        float fYMax = 0f;
        list.clear();
        // 将前面的点放入缓存
        for (int i = 0; i < length; i++) {
            xcache[i] = i * (1000/(int)fVagFreq);
            listY.add(ycache[i]) ;
            if(ycache[i]>fYMax){
                fYMax = ycache[i];
            }
        }
        //	Toast.makeText(TimeFreqGraphActivity.this,String.valueOf(fYMax), Toast.LENGTH_LONG).show();
        initTimeView((0f-fYMax),fYMax) ;
        dataset1.removeSeries(series1);
        series1.clear();
        //	series1.add(new Date(addX), addY);
        for (int k = 0; k < length-1; k++) {
            series1.add(xcache[k], ycache[k]);
        }

        dataset1.addSeries(series1);
        //曲线更新
        chart.invalidate();

    }

    public void updateFreqchartHistory(){
        int length = listY.size() ;
        if(length<0)
            return ;
        //取2的n次方
        int nTemp = 1 ;
        for( int nLen = 0;;nLen++ ){
            nTemp*=2 ;
            if(nTemp>length){
                length = nTemp/2 ;
                break ;
            }
        }
        if(length>IFreqCount)
            length = IFreqCount ; //huqb
        //将前面的点放入缓存
//		int[] xcache = new int[length] ;
//		double[] ycache = new double[length] ;
//		for (int i = 0; i < length; i++) {
//			xcache[i] = i ;
//			ycache[i] = ((double)i)/100 ;//(int)Float.valueOf(list.get(i)).floatValue() ;//
//		}
        float[] xcache = new float[IFreqCount] ;
        float[] ycache = new float[IFreqCount] ;
        long[]  ltimes = new long[IFreqCount] ;
        for (int n = 0; n < length; n++) {
            ycache[n] = Float.valueOf(listY.get(n));
        }
        algObj.GetFreqValue(ycache, xcache,fVagFreq,length);
        fDensity = fVagFreq ;
        for(int jj=0;jj<length;jj++){
            if(xcache[jj]>fFreqXMax)
                fFreqXMax = xcache[jj] ;
            if(ycache[jj]>fFreqYMax){
                fFreqYMax = ycache[jj] ;
            }
        }

        double[] xyRange = new double[]{0,fFreqXMax,0,fFreqYMax} ;
        initFreqView(xyRange);

        seriesXY.clear();
        for (int k = 0; k < length-1; k++) {
            seriesXY.add(xcache[k], ycache[k]);
        }
        dataset.removeSeries(seriesXY);
        dataset.addSeries(seriesXY);
        //曲线更新
        chartFreq.invalidate();
    }
}



