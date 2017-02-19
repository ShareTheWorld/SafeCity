package com.city.safe.utils;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.city.safe.R;

public class Util {
	public static File cameraFile;
	public static final int CAMERA_PHOTO = 10002;

	/**
	 * 获取屏幕高度和宽带
	 * 
	 * @param mContext
	 * @return int[高，宽]
	 */
	public static int[] getScreen(Context mContext) {
		DisplayMetrics dm = new DisplayMetrics();
		// 取得窗口属性
		// getWindowManager().getDefaultDisplay().getMetrics(dm);
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);

		// 窗口的宽度
		int screenWidth = dm.widthPixels;

		// 窗口高度
		int screenHeight = dm.heightPixels;
		int screen[] = { screenHeight, screenWidth };
		return screen;

	}

	/**
	 * 
	 * @Title: dip2px
	 * @Description: TODO(dp转px)
	 * @param @param context
	 * @param @param dpValue
	 * @param @return 设定文件
	 * @return int 返回类型
	 * @throws
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 设置View的高度
	 * 
	 * @param v
	 * @param height
	 */
	public static void setViewHeight2(View v, int height) {
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) v
				.getLayoutParams(); // 取控件mGrid当前的布局参数
		linearParams.height = height;// 当控件的高强制设成height
		v.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件myGrid
	}

	/**
	 * 照相获取图片
	 * 
	 * @param mContext
	 * @return 图片路径
	 */
	public static void selectPicFromCamera(Context mContext) {
		if (!Util.GetSDState()) {
			showToast(mContext, "SD卡不存在，不能拍照");
			return;
		}
		cameraFile = new File(Util.getFileAddress(0, mContext.getResources()
				.getString(R.string.app_name), mContext), "userface"
				+ System.currentTimeMillis() + ".png");
		try {
			deleteFile(cameraFile);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			cameraFile.getParentFile().mkdirs();
			((Activity) mContext).startActivityForResult(new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
					MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
					CAMERA_PHOTO);
		}

	}

	/**
	 * 返回SD卡存储路径
	 * 
	 * @param state
	 *            1：picture 2:video 3:voice 4:ceche 其他是cache
	 * @param appName
	 *            项目名称
	 * @return
	 */
	public static String getFileAddress(int state, String appName,
			Context mContext) {

		String Address = "";

		if (GetSDState()) {

			Address = Environment.getExternalStorageDirectory().getPath() + "/"
					+ appName + "/";
		} else {
			Address = mContext.getCacheDir().getAbsolutePath() + "/" + appName
					+ "/";
		}
		switch (state) {
		case 1:
			Address = Address + "cache1/";
			break;
		case 2:
			Address = Address + "video/";
			break;
		case 3:
			Address = Address + "voice/";
			break;
		case 4:
			Address = Address + "file/";
			break;
		case 5:
			Address = Address + "photos/";
			break;
		default:
			Address = Address + "cache/";
			break;
		}
		File baseFile = new File(Address);
		if (!baseFile.exists()) {
			baseFile.mkdirs();
		}
		return Address;
	}

	// 返回是否有SD卡
	public static boolean GetSDState() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @Title: deleteFile
	 * @Description: TODO(删除文件)
	 * @param @param file 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {

		}
	}

	/**
	 * 显示toast
	 * 
	 * @param mContext
	 *            当前activity
	 * @param content
	 *            显示的内容
	 */

	public static void showToast(Context mContext, String content) {
		Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
	}

}
