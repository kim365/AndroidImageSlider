package com.daimajia.slider.library.SliderTypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.R;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * This is a slider with a description TextView.
 */
public class TextSliderView extends BaseSliderView {
    private LinearLayout mTitleLayout;

    public TextSliderView(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.render_type_text,null);
        SimpleDraweeView target = (SimpleDraweeView) v.findViewById(R.id.daimajia_slider_image);
        TextView description = (TextView)v.findViewById(R.id.description);
        TextView subTitle = (TextView) v.findViewById(R.id.sub_title);
        mTitleLayout = (LinearLayout) v.findViewById(R.id.title_layout);
        description.setText(getDescription());
        subTitle.setText(getSubTitle());
        bindEventAndShow(v, target);
        return v;
    }

    public void setTitleWidth(int w) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTitleLayout.getLayoutParams();
        params.width = w;
        mTitleLayout.setLayoutParams(params);
    }
}
