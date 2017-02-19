package com.city.safe.data.acc;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;

public class FileService {
	private Context  context ;
	public FileService(Context context) {
		this.context = context;
	}
	/**
	 * 保存文件
	 * @param strfilename 文件名
	 * @param strcontent   文件内容
	 */
	public void save(String strfilename, String strcontent) throws Exception {
		// TODO Auto-generated method stub
		FileOutputStream outstream = context.openFileOutput(strfilename, context.MODE_APPEND+context.MODE_WORLD_READABLE) ;
		outstream.write(strcontent.getBytes()) ;
		outstream.close() ;
	}

	public void Newsave(String strfilename, String strcontent) throws Exception {
		// TODO Auto-generated method stub
		FileOutputStream outstream = context.openFileOutput(strfilename, context.MODE_PRIVATE+context.MODE_WORLD_READABLE) ;
		outstream.write(strcontent.getBytes()) ;
		outstream.close() ;
	}

	public void savePrivate(String strfilename, String strcontent) throws Exception {
		// TODO Auto-generated method stub
		FileOutputStream outstream = context.openFileOutput(strfilename, context.MODE_PRIVATE) ;
		outstream.write(strcontent.getBytes()) ;
		outstream.close() ;
	}

	public String read(String filename) throws Exception {
		FileInputStream instream = context.openFileInput(filename);
		ByteArrayOutputStream outstrem = new ByteArrayOutputStream() ;
		byte[] buffer = new byte[1024] ;
		int len = 0 ;
		while ((len=instream.read(buffer))!=-1){
			outstrem.write(buffer);
		}
		byte[] data = outstrem.toByteArray();
		return new String(data) ;
	}
}
