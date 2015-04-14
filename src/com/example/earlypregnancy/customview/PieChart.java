/* Copyright (C) 2012 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package com.example.earlypregnancy.customview;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.earlypregnancy.R;

/**
 * Custom view that shows a pie chart and, optionally, a label.
 */
@SuppressLint("NewApi")
public class PieChart extends ViewGroup {
	private List<Item> items = new ArrayList<Item>();

	private float totalValues = 0.0f;

	private RectF mPieBounds = new RectF();

	private Paint mPiePaint;
	private Paint mShadowPaint;

	private boolean isShowText = false;

	private float mTextY = 0.0f;
	private float mTextWidth = 0.0f;
	private float mTextHeight = 0.0f;
	private int mTextPos = TEXTPOS_LEFT;

	private float mHighlightStrength = 1.15f;

	private float mPointerY;

	private int mPieRotation;

	private OnCurrentItemChangedListener mCurrentItemChangedListener = null;

	private PieView mPieView;

	// The angle at which we measure the current item. This is
	// where the pointer points.
	private int mCurrentItemAngle;

	// the index of the current item.
	private int mCurrentItem = 0;

	/**
	 * Draw text to the left of the pie chart
	 */
	public static final int TEXTPOS_LEFT = 0;

	/**
	 * Draw text to the right of the pie chart
	 */
	public static final int TEXTPOS_RIGHT = 1;

	/**
	 * The initial fling velocity is divided by this amount.
	 */
	public static final int FLING_VELOCITY_DOWNSCALE = 4;

	/**
     *
     */
	public static final int AUTOCENTER_ANIM_DURATION = 250;

	/**
	 * Interface definition for a callback to be invoked when the current item
	 * changes.
	 */
	public interface OnCurrentItemChangedListener {
		void OnCurrentItemChanged(PieChart source, int currentItem);
	}

	/**
	 * Class constructor taking only a context. Use this constructor to create
	 * {@link PieChart} objects from your own code.
	 *
	 * @param context
	 */
	public PieChart(Context context) {
		super(context);
		init();
	}

	/**
	 * Class constructor taking a context and an attribute set. This constructor
	 * is used by the layout engine to construct a {@link PieChart} from a set
	 * of XML attributes.
	 *
	 * @param context
	 * @param attrs
	 *            An attribute set which can contain attributes from
	 *            {@link com.example.android.customviews.R.styleable.PieChart}
	 *            as well as attributes inherited from {@link android.view.View}
	 *            .
	 */
	public PieChart(Context context, AttributeSet attrs) {
		super(context, attrs);

		// attrs contains the raw values for the XML attributes
		// that were specified in the layout, which don't include
		// attributes set by styles or themes, and which may have
		// unresolved references. Call obtainStyledAttributes()
		// to get the final values for each attribute.
		//
		// This call uses R.styleable.PieChart, which is an array of
		// the custom attributes that were declared in attrs.xml.
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.PieChart, 0, 0);

		try {
			// Retrieve the values from the TypedArray and store into
			// fields of this class.
			//
			// The R.styleable.PieChart_* constants represent the index for
			// each custom attribute in the R.styleable.PieChart array.
			mTextY = a.getDimension(R.styleable.PieChart_labelY, 0.0f);
			mTextWidth = a.getDimension(R.styleable.PieChart_labelWidth, 0.0f);
			mTextHeight = a
					.getDimension(R.styleable.PieChart_labelHeight, 0.0f);
			mTextPos = a.getInteger(R.styleable.PieChart_labelPosition, 0);
			mHighlightStrength = a.getFloat(
					R.styleable.PieChart_highlightStrength, 1.0f);
			mPieRotation = a.getInt(R.styleable.PieChart_pieRotation, 0);
		} finally {
			// release the TypedArray so that it can be reused.
			a.recycle();
		}

		init();
	}

	/**
	 * Returns true if the text label should be visible.
	 *
	 * @return True if the text label should be visible, false otherwise.
	 */
	public boolean getShowText() {
		return isShowText;
	}

	/**
	 * Controls whether the text label is visible or not. Setting this property
	 * to false allows the pie chart graphic to take up the entire visible area
	 * of the control.
	 *
	 * @param showText
	 *            true if the text label should be visible, false otherwise
	 */
	public void setShowText(boolean showText) {
		isShowText = showText;
		invalidate();
	}

	/**
	 * Returns the Y position of the label text, in pixels.
	 *
	 * @return The Y position of the label text, in pixels.
	 */
	public float getTextY() {
		return mTextY;
	}

	/**
	 * Set the Y position of the label text, in pixels.
	 *
	 * @param textY
	 *            the Y position of the label text, in pixels.
	 */
	public void setTextY(float textY) {
		mTextY = textY;
		invalidate();
	}

	/**
	 * Returns the width reserved for label text, in pixels.
	 *
	 * @return The width reserved for label text, in pixels.
	 */
	public float getTextWidth() {
		return mTextWidth;
	}

	/**
	 * Set the width of the area reserved for label text. This width is
	 * constant; it does not change based on the actual width of the label as
	 * the label text changes.
	 *
	 * @param textWidth
	 *            The width reserved for label text, in pixels.
	 */
	public void setTextWidth(float textWidth) {
		mTextWidth = textWidth;
		invalidate();
	}

	/**
	 * Returns the height of the label font, in pixels.
	 *
	 * @return The height of the label font, in pixels.
	 */
	public float getTextHeight() {
		return mTextHeight;
	}

	/**
	 * Set the height of the label font, in pixels.
	 *
	 * @param textHeight
	 *            The height of the label font, in pixels.
	 */
	public void setTextHeight(float textHeight) {
		mTextHeight = textHeight;
		invalidate();
	}

	/**
	 * Returns a value that specifies whether the label text is to the right or
	 * the left of the pie chart graphic.
	 *
	 * @return One of TEXTPOS_LEFT or TEXTPOS_RIGHT.
	 */
	public int getTextPos() {
		return mTextPos;
	}

	/**
	 * Set a value that specifies whether the label text is to the right or the
	 * left of the pie chart graphic.
	 *
	 * @param textPos
	 *            TEXTPOS_LEFT to draw the text to the left of the graphic, or
	 *            TEXTPOS_RIGHT to draw the text to the right of the graphic.
	 */
	public void setTextPos(int textPos) {
		if (textPos != TEXTPOS_LEFT && textPos != TEXTPOS_RIGHT) {
			throw new IllegalArgumentException(
					"TextPos must be one of TEXTPOS_LEFT or TEXTPOS_RIGHT");
		}
		mTextPos = textPos;
		invalidate();
	}

	/**
	 * Returns the strength of the highlighting applied to each pie segment.
	 *
	 * @return The highlight strength.
	 */
	public float getHighlightStrength() {
		return mHighlightStrength;
	}

	/**
	 * Set the strength of the highlighting that is applied to each pie segment.
	 * This number is a floating point number that is multiplied by the base
	 * color of each segment to get the highlight color. A value of exactly one
	 * produces no highlight at all. Values greater than one produce highlights
	 * that are lighter than the base color, while values less than one produce
	 * highlights that are darker than the base color.
	 *
	 * @param highlightStrength
	 *            The highlight strength.
	 */
	public void setHighlightStrength(float highlightStrength) {
		if (highlightStrength < 0.0f) {
			throw new IllegalArgumentException(
					"highlight strength cannot be negative");
		}
		mHighlightStrength = highlightStrength;
		invalidate();
	}

	/**
	 * Returns the current rotation of the pie graphic.
	 *
	 * @return The current pie rotation, in degrees.
	 */
	public int getPieRotation() {
		return mPieRotation;
	}

	/**
	 * Set the current rotation of the pie graphic. Setting this value may
	 * change the current item.
	 *
	 * @param rotation
	 *            The current pie rotation, in degrees.
	 */
	public void setPieRotation(int rotation) {
		rotation = (rotation % 360 + 360) % 360;
		mPieRotation = rotation;
		// mPieView.rotateTo(rotation);

		calcCurrentItem();
	}

	/**
	 * Returns the index of the currently selected data item.
	 *
	 * @return The zero-based index of the currently selected data item.
	 */
	public int getCurrentItem() {
		return mCurrentItem;
	}

	/**
	 * Set the currently selected item. Calling this function will set the
	 * current selection and rotate the pie to bring it into view.
	 *
	 * @param currentItem
	 *            The zero-based index of the item to select.
	 */
	public void setCurrentItem(int currentItem) {
		setCurrentItem(currentItem, true);
	}

	/**
	 * Set the current item by index. Optionally, scroll the current item into
	 * view. This version is for internal use--the scrollIntoView option is
	 * always true for external callers.
	 *
	 * @param currentItem
	 *            The index of the current item.
	 * @param scrollIntoView
	 *            True if the pie should rotate until the current item is
	 *            centered. False otherwise. If this parameter is false, the pie
	 *            rotation will not change.
	 */
	private void setCurrentItem(int currentItem, boolean scrollIntoView) {
		mCurrentItem = currentItem;
		if (mCurrentItemChangedListener != null) {
			mCurrentItemChangedListener.OnCurrentItemChanged(this, currentItem);
		}
		invalidate();
	}

	/**
	 * Register a callback to be invoked when the currently selected item
	 * changes.
	 *
	 * @param listener
	 *            Can be null. The current item changed listener to attach to
	 *            this view.
	 */
	public void setOnCurrentItemChangedListener(
			OnCurrentItemChangedListener listener) {
		mCurrentItemChangedListener = listener;
	}

	/**
	 * Add a new data item to this view. Adding an item adds a slice to the pie
	 * whose size is proportional to the item's value. As new items are added,
	 * the size of each existing slice is recalculated so that the proportions
	 * remain correct.
	 *
	 * @param label
	 *            The label text to be shown when this item is selected.
	 * @param value
	 *            The value of this item.
	 * @param color
	 *            The ARGB color of the pie slice associated with this item.
	 * @return The index of the newly added item.
	 */
	public int addItem(String label, float value, int color) {
		Item it = new Item();
		it.name = label;
		it.color = color;
		it.value = value;

		// Calculate the highlight color. Saturate at 0xff to make sure that
		// high values
		// don't result in aliasing.
		it.highlight = Color
				.argb(0xff, Math.min(
						(int) (mHighlightStrength * (float) Color.red(color)),
						0xff),
						Math.min((int) (mHighlightStrength * (float) Color
								.green(color)), 0xff), Math.min(
								(int) (mHighlightStrength * (float) Color
										.blue(color)), 0xff));
		totalValues += value;

		items.add(it);

		onDataChanged();

		return items.size() - 1;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// Do nothing. Do not call the superclass method--that would start a
		// layout pass
		// on this view's children. PieChart lays out its children in
		// onSizeChanged().
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		for (Item item : items) {
			canvas.drawText(item.name, item.textX, item.textY, mPiePaint);
		}
	}

	//
	// Measurement functions. This example uses a simple heuristic: it assumes
	// that
	// the pie chart should be at least as wide as its label.
	//
	@Override
	protected int getSuggestedMinimumWidth() {
		return (int) mTextWidth * 2;
	}

	@Override
	protected int getSuggestedMinimumHeight() {
		return (int) mTextWidth;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Try for a width based on our minimum
		int minw = getPaddingLeft() + getPaddingRight()
				+ getSuggestedMinimumWidth();

		int w = Math.max(minw, MeasureSpec.getSize(widthMeasureSpec));

		// Whatever the width ends up being, ask for a height that would let the
		// pie
		// get as big as it can
		int minh = (w - (int) mTextWidth) + getPaddingBottom()
				+ getPaddingTop();
		int h = Math.min(MeasureSpec.getSize(heightMeasureSpec), minh);

		setMeasuredDimension(w, h);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		//
		// Set dimensions for text, pie chart, etc
		//
		// Account for padding
		float xpad = (float) (getPaddingLeft() + getPaddingRight());
		float ypad = (float) (getPaddingTop() + getPaddingBottom());

		// Account for the label
		if (isShowText)
			xpad += mTextWidth;

		float ww = (float) w - xpad;
		float hh = (float) h - ypad;

		// Figure out how big we can make the pie.
		float diameter = Math.min(ww, hh);
		mPieBounds = new RectF(0.0f, 0.0f, diameter, diameter);
		mPieBounds.offsetTo(getPaddingLeft(), getPaddingTop());

		mPointerY = mTextY - (mTextHeight / 2.0f);
		float pointerOffset = mPieBounds.centerY() - mPointerY;

		// Make adjustments based on text position
		if (mTextPos == TEXTPOS_LEFT) {
			if (isShowText)
				mPieBounds.offset(mTextWidth, 0.0f);

			if (pointerOffset < 0) {
				pointerOffset = -pointerOffset;
				mCurrentItemAngle = 225;
			} else {
				mCurrentItemAngle = 135;
			}
		} else {

			if (pointerOffset < 0) {
				pointerOffset = -pointerOffset;
				mCurrentItemAngle = 315;
			} else {
				mCurrentItemAngle = 45;
			}
		}

		// Lay out the child view that actually draws the pie.
		mPieView.layout((int) mPieBounds.left, (int) mPieBounds.top,
				(int) mPieBounds.right, (int) mPieBounds.bottom);
		mPieView.setPivot(mPieBounds.width() / 2, mPieBounds.height() / 2);

		onDataChanged();
	}

	/**
	 * Calculate which pie slice is under the pointer, and set the current item
	 * field accordingly.
	 */
	private void calcCurrentItem() {
		int pointerAngle = (mCurrentItemAngle - 360 + mPieRotation) % 360;
		for (int i = 0; i < items.size(); ++i) {
			Item it = items.get(i);
			if (it.startAngle <= pointerAngle && pointerAngle <= it.endAngle) {
				if (i != mCurrentItem) {
					setCurrentItem(i, false);
				}
				break;
			}
		}
	}

	/**
	 * Do all of the recalculations needed when the data array changes.
	 */
	private void onDataChanged() {
		// When the data changes, we have to recalculate
		// all of the angles.
		float currentAngle = 0;
		float centerX, centerY;
		centerX = mPieBounds.width() / 2F;
		centerY = mPieBounds.height() / 2F;
		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			item.startAngle = currentAngle;
			if (i != items.size() - 1) {
				item.endAngle = currentAngle - item.value * 360.0f
						/ totalValues;
				currentAngle = item.endAngle;
			} else {
				item.endAngle = -360F;
			}

			item.textX = (float) (centerX + (mPieBounds.width() / 2F)
					* Math.cos(Math.toRadians(mPieRotation + item.startAngle
							+ (item.endAngle - item.startAngle) / 2F)));
			item.textY = (float) (centerY + (mPieBounds.height() / 2F)
					* Math.sin(Math.toRadians(mPieRotation + item.startAngle
							+ (item.endAngle - item.startAngle) / 2F)));

			// Recalculate the gradient shaders. There are
			// three values in this gradient, even though only
			// two are necessary, in order to work around
			// a bug in certain versions of the graphics engine
			// that expects at least three values if the
			// positions array is non-null.
			//
			item.shader = new SweepGradient(mPieBounds.width() / 2.0f,
					mPieBounds.height() / 2.0f, new int[] { item.highlight,
							item.highlight, item.color, item.color, },
					new float[] { 0, (float) (360 - item.endAngle) / 360.0f,
							(float) (360 - item.startAngle) / 360.0f, 1.0f });
		}
		calcCurrentItem();
	}

	/**
	 * Initialize the control. This code is in a separate method so that it can
	 * be called from both constructors.
	 */
	private void init() {
		// Force the background to software rendering because otherwise the Blur
		// filter won't work.
		setLayerToSW(this);

		// Set up the paint for the pie slices
		mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPiePaint.setStyle(Paint.Style.FILL);
		mPiePaint.setTextSize(mTextHeight);

		// Set up the paint for the shadow
		mShadowPaint = new Paint(0);
		mShadowPaint.setColor(0xff101010);
		mShadowPaint.setMaskFilter(new BlurMaskFilter(8,
				BlurMaskFilter.Blur.NORMAL));

		// Add a child view to draw the pie. Putting this in a child view
		// makes it possible to draw it on a separate hardware layer that
		// rotates
		// independently
		mPieView = new PieView(getContext());
		addView(mPieView);
		// mPieView.rotateTo(mPieRotation);

		// In edit mode it's nice to have some demo data, so add that here.
		if (this.isInEditMode()) {
			Resources res = getResources();
			addItem("Annabelle", 3, res.getColor(R.color.bluegrass));
			addItem("Brunhilde", 4, res.getColor(R.color.chartreuse));
			addItem("Carolina", 2, res.getColor(R.color.emerald));
			addItem("Dahlia", 3, res.getColor(R.color.seafoam));
			addItem("Ekaterina", 1, res.getColor(R.color.slate));
		}

	}

	private void setLayerToSW(View v) {
		if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	/**
	 * Internal child class that draws the pie chart onto a separate hardware
	 * layer when necessary.
	 */
	private class PieView extends View {
		// Used for SDK < 11
		private Matrix mTransform = new Matrix();
		private PointF mPivot = new PointF();
		private RectF mBounds;

		/**
		 * Construct a PieView
		 *
		 * @param context
		 */
		public PieView(Context context) {
			super(context);
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			if (Build.VERSION.SDK_INT < 11) {
				mTransform.set(canvas.getMatrix());
				// mTransform.preRotate(mPieRotation, mPivot.x, mPivot.y);
				canvas.setMatrix(mTransform);
			}

			for (Item it : items) {
				mPiePaint.setShader(it.shader);
				canvas.drawArc(mBounds, 360 - it.endAngle, it.endAngle
						- it.startAngle, true, mPiePaint);
			}
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			mBounds = new RectF(0, 0, w, h);
		}

		// public void rotateTo(float pieRotation) {
		// if (Build.VERSION.SDK_INT >= 11) {
		// setRotation(pieRotation);
		// } else {
		// invalidate();
		// }
		// }

		public void setPivot(float x, float y) {
			mPivot.x = x;
			mPivot.y = y;
			if (Build.VERSION.SDK_INT >= 11) {
				setPivotX(x);
				setPivotY(y);
			} else {
				invalidate();
			}
		}
	}

	/**
	 * Maintains the state for a data item.
	 */
	private class Item {
		public String name;
		public float value;
		public int color;

		// computed values
		public float startAngle;
		public float endAngle;
		public float textX, textY;

		public int highlight;
		public Shader shader;
	}

}
