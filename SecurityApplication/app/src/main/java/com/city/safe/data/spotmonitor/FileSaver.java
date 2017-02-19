package com.city.safe.data.spotmonitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class FileSaver {
	private File file;
	private PrintWriter pw;
	public FileSaver(String filename){
		FileHelper fh=new FileHelper();
		file=new File(fh.getPath(), filename);
		try {
			if(!file.exists()){
				file.createNewFile();
			}
			pw=new PrintWriter(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public void close(){
    	try{
    		if(pw!=null)
    	    	  pw.close();
    	}catch(Exception e){
    	}
    }
	public void saveRecord(String record){
		if(pw!=null){
			pw.println(record);
		}
	}
	public  static void saveToFile(File path, byte[] data){
	FileOutputStream fos=null;
	 try{
		 fos=new FileOutputStream(path);
		 fos.write(data);
		 fos.flush();
		 fos.close();
	 }catch(Exception e){
		 e.printStackTrace();
		 if(fos!=null){
			 try {
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		 }
	 }
	}
	
}
