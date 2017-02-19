package com.city.safe.data.spotmonitor;

import java.io.File;

import android.os.Environment;

public class FileHelper {
	public final static String savedSdPath = "/spotmoitor";
	private File dirpath;
	public FileHelper(){
		File dir=Environment.getExternalStorageDirectory();
		dirpath=new File(dir.toString(), savedSdPath);
		if(!dirpath.exists())
		  dirpath.mkdir();
	}
	public File getPath(){
		return dirpath;
	}
}
