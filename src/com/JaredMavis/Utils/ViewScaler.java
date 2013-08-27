package com.JaredMavis.Utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewScaler {
	 // Scales the contents of the given view so that it completely fills the given
	// container on one axis (that is, we're scaling is	otropically).
	public static void scaleContents(View rootView, View container){
		float xScale = (float)container.getWidth() / rootView.getWidth();
		float yScale = (float)container.getHeight() / rootView.getHeight();
		float scale = Math.min(xScale, yScale);
		
		scaleViewAndChildren(rootView, scale);
	}
	
	// Scale the given view, its contents, and all of its children by the given factor.
	private static void scaleViewAndChildren(View root, float scale){
		ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
		
		if (layoutParams.width != ViewGroup.LayoutParams.FILL_PARENT &&
			layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT){
			layoutParams.width *= scale;
		}
		
		if (layoutParams.height != ViewGroup.LayoutParams.FILL_PARENT &&
				layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT){
			layoutParams.height *= scale;
		}
		
		/// Scale any margins
		if (layoutParams instanceof ViewGroup.MarginLayoutParams){
			ViewGroup.MarginLayoutParams marginParams = 
					(ViewGroup.MarginLayoutParams) layoutParams;
			marginParams.leftMargin *= scale;
			marginParams.rightMargin *= scale;
			marginParams.topMargin *= scale;
			marginParams.bottomMargin *= scale;
		}
		
		root.setLayoutParams(layoutParams);
		
		root.setPadding(
				(int)(root.getPaddingLeft() * scale),
				(int)(root.getPaddingRight() * scale),
				(int)(root.getPaddingTop() * scale),
				(int)(root.getPaddingBottom() * scale));
		
		/// scale all text
		if (root instanceof TextView){
			TextView textView = (TextView)root;
			refitText(textView);
		}
		
		/// If the root has children then go through each of them scaling
		if (root instanceof ViewGroup){
			ViewGroup groupView = (ViewGroup)root;
			for (int childIndex = 0; childIndex < groupView.getChildCount(); childIndex++){
				scaleViewAndChildren(groupView.getChildAt(childIndex), scale);
			}
		}
	}
	
	/* Re size the font so the specified text fits in the text box and is as large as possible
     */
    private static void refitText(TextView textView) { 
        int targetWidth = textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight();
        int targetHeight = textView.getHeight() - textView.getPaddingTop() - textView.getPaddingBottom();
        int targetSize = Math.min(targetWidth, targetHeight);
        
        Paint mTestPaint = textView.getPaint();
        Rect currentTextRect = new Rect();
        mTestPaint.getTextBounds(textView.getText().toString(), 0, textView.getText().length(), currentTextRect);
        float textSize = 2;
        float scalingAmount = 2;
        float minimumThreshold = .5f; // how close we need to get to call it quits 
        
        while (currentTextRect.width() + minimumThreshold < targetSize && currentTextRect.height() + minimumThreshold < targetSize){
        	if (currentTextRect.width() > targetSize || currentTextRect.height() > targetSize){
        		textSize /= scalingAmount;
        		scalingAmount /= 5;
        	}
        	textSize *= scalingAmount;
        	mTestPaint.setTextSize(textSize);
        	mTestPaint.getTextBounds(textView.getText().toString(), 0, textView.getText().length(), currentTextRect);
        }
        textSize /= scalingAmount;
        textView.getPaint().setTextSize(textSize);
        textView.getPaint().getTextBounds(textView.getText().toString(), 0, textView.getText().length(), currentTextRect);
    }
}
