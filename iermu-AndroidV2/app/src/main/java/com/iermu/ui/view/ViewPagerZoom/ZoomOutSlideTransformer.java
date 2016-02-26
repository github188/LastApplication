package com.iermu.ui.view.ViewPagerZoom;

import android.view.View;

import com.nineoldandroids.view.ViewHelper;

public class ZoomOutSlideTransformer extends BaseTransformer {

	private static final float MIN_SCALE = 0.8f;
	private static final float MIN_ALPHA = 0.9f;

	@Override
	protected void onTransform(View view, float position) {
		if (position >= -1 || position <= 1) {
			// Modify the default slide transition to shrink the page as well
			final float height = view.getHeight();
            final float width = view.getWidth();
			final float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
			final float vertMargin = height * (1 - scaleFactor) / 2;
			final float horzMargin = view.getWidth() * (1 - scaleFactor) / 2;

            // Center vertically
            ViewHelper.setPivotY(view,0.5f * height);
			ViewHelper.setPivotX(view,0.5f * width);


			if (position < 0) {
                ViewHelper.setTranslationX(view,horzMargin - vertMargin / 2);// - vertMargin / 2
			} else {
                ViewHelper.setTranslationX(view,-horzMargin + vertMargin / 2);// + vertMargin / 2
			}

			// Scale the page down (between MIN_SCALE and 1)
			ViewHelper.setScaleX(view,scaleFactor);
            ViewHelper.setScaleY(view,scaleFactor);

			// Fade the page relative to its size.
            ViewHelper.setAlpha(view,MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
		}

//        int pageWidth = view.getWidth();
//        int pageHeight = view.getHeight();
//
//        Log.i("TAG", view + " , " + position + "");
//
//        if (position < -1)
//        { // [-Infinity,-1)
//            // This page is way off-screen to the left.
//            view.setAlpha(1);
//
//        } else if (position <= 1) //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
//        { // [-1,1]
//            // Modify the default slide transition to shrink the page as well
//            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
//            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
//            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
//
//            if (position < 0) {
//                view.setTranslationX(horzMargin - vertMargin / 2);
//            } else {
//                view.setTranslationX(-horzMargin + vertMargin / 2);
//            }
//
//            // Scale the page down (between MIN_SCALE and 1)
//            view.setScaleX(scaleFactor);
//            view.setScaleY(scaleFactor);
//
//
//            // Fade the page relative to its size.
////            view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
////                    / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
//
//        } else
//        { // (1,+Infinity]
//            // This page is way off-screen to the right.
//            view.setAlpha(1);
//        }

	}
}
