package com.daimajia.slider.library.SliderTypes;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.daimajia.slider.library.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;


import java.io.File;

/**
 * When you want to make your own slider view, you must extends from this class.
 * BaseSliderView provides some useful methods.
 * I provide two example: {@link com.daimajia.slider.library.SliderTypes.DefaultSliderView} and
 * {@link com.daimajia.slider.library.SliderTypes.TextSliderView}
 * if you want to show progressbar, you just need to set a progressbar id as @+id/loading_bar.
 */
public abstract class BaseSliderView {

    protected Context mContext;

    private Bundle mBundle;

    /**
     * Error place holder image.
     */
    private int mErrorPlaceHolderRes;

    /**
     * Empty imageView placeholder.
     */
    private int mEmptyPlaceHolderRes;

    private String mUrl;
    private File mFile;
    private int mRes;

    protected OnSliderClickListener mOnSliderClickListener;

    private boolean mErrorDisappear;

    private ImageLoadListener mLoadListener;

    private String mDescription;
    private String mSubTitle;
    private Object mTag;

    private RoundingParams roundingParams;

    private ImagePipelineConfig mImagePipelineConfig;

    /**
     * Scale type of the image.
     */
    private ScaleType mScaleType = ScaleType.Fit;

    public enum ScaleType{
        CenterCrop, CenterInside, Fit, FitCenterCrop
    }

    protected BaseSliderView(Context context) {
        mContext = context;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }

    /**
     * the placeholder image when loading image from url or file.
     * @param resId Image resource id
     * @return
     */
    public BaseSliderView empty(int resId){
        mEmptyPlaceHolderRes = resId;
        return this;
    }

    /**
     * determine whether remove the image which failed to download or load from file
     * @param disappear
     * @return
     */
    public BaseSliderView errorDisappear(boolean disappear){
        mErrorDisappear = disappear;
        return this;
    }

    /**
     * if you set errorDisappear false, this will set a error placeholder image.
     * @param resId image resource id
     * @return
     */
    public BaseSliderView error(int resId){
        mErrorPlaceHolderRes = resId;
        return this;
    }

    /**
     * the description of a slider image.
     * @param description
     * @return
     */
    public BaseSliderView description(String description){
        mDescription = description;
        return this;
    }

    /**
     * the subtitle of a slider image.
     * @param subTitle
     * @return
     */
    public BaseSliderView subTitle(String subTitle){
        mSubTitle = subTitle;
        return this;
    }

    /**
     * set a url as a image that preparing to load
     * @param url
     * @return
     */
    public BaseSliderView image(String url){
        if(mFile != null || mRes != 0){
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mUrl = url;
        return this;
    }

    /**
     * set a file as a image that will to load
     * @param file
     * @return
     */
    public BaseSliderView image(File file){
        if(mUrl != null || mRes != 0){
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mFile = file;
        return this;
    }

    public BaseSliderView image(int res){
        if(mUrl != null || mFile != null){
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mRes = res;
        return this;
    }

    /**
     * lets users add a bundle of additional information
     * @param bundle
     * @return
     */
    public BaseSliderView bundle(Bundle bundle){
        mBundle = bundle;
        return this;
    }

    public String getUrl(){
        return mUrl;
    }

    public boolean isErrorDisappear(){
        return mErrorDisappear;
    }

    public int getEmpty(){
        return mEmptyPlaceHolderRes;
    }

    public int getError(){
        return mErrorPlaceHolderRes;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    public Context getContext(){
        return mContext;
    }

    /**
     * set a slider image click listener
     * @param l
     * @return
     */
    public BaseSliderView setOnSliderClickListener(OnSliderClickListener l){
        mOnSliderClickListener = l;
        return this;
    }

    public BaseSliderView imagePipelineConfig(ImagePipelineConfig imagePipelineConfig){
        mImagePipelineConfig = imagePipelineConfig;
        return this;
    }

    /**
     * When you want to implement your own slider view, please call this method in the end in `getView()` method
     * @param v the whole view
     * @param targetImageView where to place image
     */
    protected void bindEventAndShow(final View v, SimpleDraweeView targetImageView){
        final BaseSliderView me = this;
        if (!Fresco.hasBeenInitialized()) {
            if(mImagePipelineConfig != null) {
                Fresco.initialize(mContext, mImagePipelineConfig);
            } else {
                Fresco.initialize(mContext);
            }
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(mOnSliderClickListener != null){
                mOnSliderClickListener.onSliderClick(me);
            }
            }
        });

        if (targetImageView == null)
            return;

        if (mLoadListener != null) {
            mLoadListener.onStart(me);
        }

        GenericDraweeHierarchy hierarchy = targetImageView.getHierarchy();
        if (hierarchy == null) {
            hierarchy = new GenericDraweeHierarchyBuilder(mContext.getResources()).build();
        }
        if(getEmpty() != 0){
            hierarchy.setPlaceholderImage(getEmpty());
        }
        if(getError() != 0){
            hierarchy.setFailureImage(getError());
        }
        switch (mScaleType){
            case Fit:
                hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
                break;
            case CenterCrop:
                hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
                break;
            case CenterInside:
                hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);
                break;
        }
        if (roundingParams != null) {
            hierarchy.setRoundingParams(roundingParams);
        }
        targetImageView.setHierarchy(hierarchy);
        Uri uri;
        if(mUrl != null){
            if (TextUtils.isEmpty(mUrl)) {
                mUrl = "http://";
            }
            uri = Uri.parse(mUrl);
        }else if(mFile != null){
            uri = Uri.parse("file://" + mFile.getPath());
        }else if(mRes != 0){
            uri = Uri.parse("res://tv.acfundanmaku.video/" + mRes);
        }else{
            return;
        }
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(true)
                .build();
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        builder.setImageRequest(request)
                .setAutoPlayAnimations(true)
                .setControllerListener(new BaseControllerListener<ImageInfo>(){
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if(v.findViewById(R.id.loading_bar) != null){
                            v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                        if(mLoadListener != null){
                            mLoadListener.onEnd(false,me);
                        }
                        if(v.findViewById(R.id.loading_bar) != null){
                            v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                        }
                    }
                });
        DraweeController controller = builder.build();
        targetImageView.setController(controller);
   }



    public BaseSliderView setScaleType(ScaleType type){
        mScaleType = type;
        return this;
    }

    public ScaleType getScaleType(){
        return mScaleType;
    }

    /**
     * the extended class have to implement getView(), which is called by the adapter,
     * every extended class response to render their own view.
     * @return
     */
    public abstract View getView();

    /**
     * set a listener to get a message , if load error.
     * @param l
     */
    public void setOnImageLoadListener(ImageLoadListener l){
        mLoadListener = l;
    }

    public interface OnSliderClickListener {
        public void onSliderClick(BaseSliderView slider);
    }

    /**
     * when you have some extra information, please put it in this bundle.
     * @return
     */
    public Bundle getBundle(){
        return mBundle;
    }

    public interface ImageLoadListener{
        public void onStart(BaseSliderView target);
        public void onEnd(boolean result, BaseSliderView target);
    }

    /**
     *
     * @return
     */
    public RoundingParams getRoundingParams() {
        return roundingParams;
    }


    /**
     * 圆角支持
     * @param roundingParams
     * @return
     */
    public BaseSliderView setRoundingParams(RoundingParams roundingParams) {
        this.roundingParams = roundingParams;
        return this;
    }
}
