package com.daimajia.slider.library.Transformers;

import android.support.v4.view.ViewCompat;
import android.view.View;

public class RotateUpTransformer extends BaseTransformer {

	private static final float ROT_MOD = -15f;

	@Override
	protected void onTransform(View view, float position) {
		final float width = view.getWidth();
		final float rotation = ROT_MOD * position;

		ViewCompat.setPivotX(view,width * 0.5f);
		ViewCompat.setPivotY(view,0f);
		ViewCompat.setTranslationX(view,0f);
		ViewCompat.setRotation(view,rotation);
	}
	
	@Override
	protected boolean isPagingEnabled() {
		return true;
	}

}
