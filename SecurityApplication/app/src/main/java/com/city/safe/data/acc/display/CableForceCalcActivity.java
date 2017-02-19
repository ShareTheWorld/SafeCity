package com.city.safe.data.acc.display;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.city.safe.data.acc.*;
import com.city.safe.R;

public class CableForceCalcActivity extends BaseActivity {

    private Button  btnCalc = null ;
    private String strFileName="" ;
    private String strFilePath="" ;
    private float  fDensity =3.9f;
    private float  fVagFreq = 100f;
    private EditText etDensity = null ;
    private EditText etCableLen = null ;
    private EditText etFrequency = null ;
    private EditText etSampFreq = null ;
    private TextView tvFocValue = null ;

    private String strPhotoName = "" ;
    String strPhotoPath = "";
    //file
    FileService service = null;
    String strparamname = "accparamfile.txt" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cableforce);

        initHead();
        btnCalc = (Button) findViewById(R.id.btn_cable_calc);
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        if(bundle!=null){
            strFileName = bundle.getString("filename");
            strFilePath = bundle.getString("filepath");
            fDensity = bundle.getFloat("density") ;//频差
            strPhotoName = bundle.getString("photoname");
            fVagFreq = bundle.getFloat("vagdensity");
        }
        etDensity = (EditText)findViewById(R.id.edt_lineardensity);
        etCableLen = (EditText)findViewById(R.id.edt_cablelen);
        etFrequency = (EditText)findViewById(R.id.edt_frequencydiff);
        etSampFreq = (EditText)findViewById(R.id.edt_samplingfreq);
        tvFocValue = (TextView)findViewById(R.id.text_forcevalue);

        etSampFreq.setText(String.valueOf(fVagFreq));
        etFrequency.setText(String.valueOf(fDensity));

        btn_leftTop.setVisibility(View.VISIBLE);
        btn_rightTop.setVisibility(View.INVISIBLE);
//		btn_rightTop.setBackgroundResource(R.drawable.calcforce1); //calcforce1
        tv_head.setText(R.string.calcforce);

        btn_leftTop.setOnClickListener(new BackListener()) ;
        btn_rightTop.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//				btn_rightTop.setBackgroundResource(R.drawable.calcforce2);
//				Intent intent = new Intent();
//		    	intent.setClass(CableForceCalcActivity.this, TimeFreqGraphActivity.class);
//				startActivity(intent);
            }
        }) ;
        btnCalc.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String strTemp = etDensity.getText().toString() ;
                if(strTemp.equals("")){
                    Toast.makeText(CableForceCalcActivity.this,"Desity value is empty!", Toast.LENGTH_LONG).show();
                    return ;
                }
                float fDensity = Float.valueOf(strTemp) ;

                strTemp = etCableLen.getText().toString() ;
                if(strTemp.equals("")){
                    Toast.makeText(CableForceCalcActivity.this,"Cable lenth value is empty!", Toast.LENGTH_LONG).show();
                    return ;
                }
                float fCalbeLen = Float.valueOf(strTemp) ;

                strTemp = etFrequency.getText().toString() ;
                if(strTemp.equals("")){
                    Toast.makeText(CableForceCalcActivity.this,"Freq difference value is empty!", Toast.LENGTH_LONG).show();
                    return ;
                }
                float fFreqDiff = Float.valueOf(strTemp) ;

                float T = 4*fDensity*fCalbeLen*fCalbeLen*fFreqDiff*fFreqDiff ;
                tvFocValue.setText(String.valueOf(T)) ;
            }
        }) ;

        strPhotoPath = Environment.getExternalStorageDirectory().toString() ;
        strPhotoPath = strPhotoPath+"/"+strPhotoName ;
        if(new File(strPhotoPath).exists()){
            Bitmap bmp = null ;
            ImageView image = (ImageView) findViewById(R.id.struct_image);
            try {
                FileInputStream fis = new FileInputStream(strPhotoPath);
                bmp = BitmapFactory.decodeStream(fis);
                image.setImageBitmap(bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        service = new FileService(getApplicationContext()) ;

        String strParam = null;
        try {
            strParam = service.read(strparamname) ;
        } catch (Exception e) {
            return;
        }
        if(!strParam.isEmpty())
        {
//			 String[] sParam = strParam.split(",") ;
//			 if(sParam.length>1){
//				 etSampFreq.setText(sParam[1]);
//			 }
        }
    }

    class BackListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //modify by hongtao.fu
//            Intent intent = new Intent();
//            intent.setClass(CableForceCalcActivity.this, TimeFreqGraphActivity.class);
//            //用Bundle携带数据
//            Bundle bundle=new Bundle();
//            //传递name参数为tinyphp
//            bundle.putString("filename", strFileName);
//            bundle.putString("filepaht",strFilePath);
//            intent.putExtras(bundle);
//            startActivity(intent);
            finish();;
        }
    }
}