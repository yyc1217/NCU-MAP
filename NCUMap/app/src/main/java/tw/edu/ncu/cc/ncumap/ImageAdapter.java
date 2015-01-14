package tw.edu.ncu.cc.ncumap;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by andre.hu on 2014/11/2.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private boolean[] isSelected;

    public ImageAdapter(Context c, boolean[] isSelected) {
        this.isSelected = isSelected;
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * create a new ImageView for each item referenced by the Adapter
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(10, 10, 10, 10);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            imageView = (ImageView) convertView;
        }
        if (isSelected[position])
            imageView.setBackgroundColor(Color.HSVToColor(new float[]{position * 19, (float) 0.4, (float) 0.9}));
        else
            imageView.setBackgroundColor(Color.parseColor("#00000000"));
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    /**
     * references to our images
     */
    private Integer[] mThumbIds = {
            R.drawable.wheelchair_ramp, R.drawable.disabled_car_parking,
            R.drawable.disabled_motor_parking, R.drawable.emergency,
            R.drawable.aed, R.drawable.restaurant,
            R.drawable.sport_recreation, R.drawable.administration,
            R.drawable.research, R.drawable.dormitory,
            R.drawable.other, R.drawable.toilet,
            R.drawable.atm, R.drawable.bus_station,
            R.drawable.parking_lot
    };
}