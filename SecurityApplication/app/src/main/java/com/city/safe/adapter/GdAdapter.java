package com.city.safe.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.city.safe.R;
import com.city.safe.utils.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.photoselector.model.PhotoModel;
//import com.city.safe.R;

/**
 * 
 * @ClassName: AppointmentsAdapter
 * @Description: TODO(发布商品的图片)
 * @author scene
 * @date 2015-4-17 上午11:36:50
 * 
 */
public class GdAdapter extends BaseAdapter {
	private Context mContext;
	private List<PhotoModel> mLists;

	public GdAdapter(Context mContext, List<PhotoModel> mLists) {
		this.mLists = mLists;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return mLists == null ? 0 : mLists.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mLists == null ? null : mLists.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View view, ViewGroup group) {
		Holder holder;
		if (view == null) {
			holder = new Holder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			view = inflater.inflate(
					R.layout.activity_slidingmenu_albums_item_item, null);
			holder.img = (ImageView) view.findViewById(R.id.img);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		PhotoModel info = mLists.get(position);

		if (info != null) {
			if (info.getOriginalPath().equals("default")) {
				ImageLoader.getInstance().displayImage(
						"drawable://" + R.drawable.ic_add, holder.img);
			} else {
				ImageLoader.getInstance().displayImage(
						"file://" + info.getOriginalPath(), holder.img);
			}
			Util.setViewHeight2(
					holder.img,
					(Util.getScreen(mContext)[1] - Util.dip2px(mContext, 50)) / 4);

		}
		return view;
	}

	class Holder {
		ImageView img;
	}

}
