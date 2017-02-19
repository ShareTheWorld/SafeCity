package com.city.safe.data.acc.display;

public class Algo {

	private FFT fft = null;
	//	private int nSensorCount  = 100 ;
	public Algo() {
		super();
		// TODO Auto-generated constructor stub
		fft = new FFT();
	}

	public void GetFreqValue(float[] fYSensor,float[] fXValue,float fFrq,int nTempCount){
		//x: 1维数组的下标*频率/样本数
		int nCount = nTempCount ;//2^7
		float fFrenq = fFrq ;//((float)nCount/(float)(lTimes[nCount-1]-lTimes[0]))*1000.0f ;
		float fFreqXMax = 0 ;
		for(int n=0;n<nCount;n++){
			fXValue[n] = (n/2)*fFrenq/nCount ;

			if(fXValue[n]>fFreqXMax){
				fFreqXMax = fXValue[n] ;
			}
		}
		//y: fft
		float f = 16 ;
		f = (float) Math.sqrt(f) ;
		fft.calculate(fYSensor) ; //快速fft变化
	}
}