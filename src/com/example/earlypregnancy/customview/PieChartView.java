package com.example.earlypregnancy.customview;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.example.earlypregnancy.R;

public class PieChartView extends View {

	private Paint paintPie;
	private Paint paintText;
	private RectF rectFPie;
	private Rect rectTextMax;
	private ArrayList<Item> items;

	private float totalValues;

	public PieChartView(Context context) {
		super(context);

		init();
	}

	public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init();
	}

	public PieChartView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		float sizeMin = rectTextMax != null ? (Math.min(getHeight() - 2
				* (rectTextMax.height()), getWidth() - 2
				* (rectTextMax.width() + 0))) : (getHeight() - 2 * paintText
				.getTextSize());
		rectFPie.left = (getWidth() - sizeMin) / 2F;
		rectFPie.right = rectFPie.left + sizeMin;
		rectFPie.top = (getHeight() - sizeMin) / 2F;
		rectFPie.bottom = rectFPie.top + sizeMin;

		float centerX = rectFPie.left + rectFPie.width() / 2F, centerY = rectFPie.top
				+ rectFPie.height() / 2F;

		for (Item item : items) {
			float centerAngle = item.startAngle
					+ (item.endAngle - item.startAngle) / 2F;

			item.nameX = (float) (centerX + ((rectFPie.height() + rectFPie
					.width()) / 4F) * Math.cos(Math.toRadians(centerAngle)));
			item.nameY = (float) (centerY + ((rectFPie.height() + rectFPie
					.width()) / 4F) * Math.sin(Math.toRadians(centerAngle)));

			if (centerAngle > -90 && centerAngle < 90) {
				item.nameX += 10;
			} else {
				item.nameX = item.nameX - rectTextMax.width() - 10;
			}

			if (centerAngle > 0 && centerAngle < 180) {
				item.nameY += rectTextMax.height();
			}

		}

		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// canvas.drawArc(rectF, 0, 90, true, paint);

		for (Item item : items) {
			paintPie.setShader(item.shader);
			canvas.drawArc(rectFPie, item.startAngle, item.endAngle
					- item.startAngle, true, paintPie);
		}

		for (Item item : items) {
			if (Math.abs(item.endAngle - item.startAngle) > 30) {
				canvas.drawText(item.name, item.nameX, item.nameY, paintText);
			}
		}

	}

	private void init() {
		paintPie = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintPie.setStyle(Paint.Style.FILL);
		paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintText.setStyle(Paint.Style.FILL);
		paintText.setTextSize(getContext().getResources().getDimension(
				R.dimen.pulOutput_s));
		rectFPie = new RectF();
		items = new ArrayList<PieChartView.Item>();
		// addItem("AAA", 2F, Color.RED);
		// addItem("BBB", 8F, Color.GREEN);
		// addItem("CCC", 2F, Color.BLUE);
	}

	public void addItem(String name, float values, int color) {
		Item item = new Item();
		item.name = name;
		item.values = values;
		item.color = color;
		item.rectText = new Rect();
		paintText.getTextBounds(name, 0, name.length(), item.rectText);
		if (rectTextMax == null) {
			rectTextMax = item.rectText;
		} else if (rectTextMax.width() < item.rectText.width()) {
			rectTextMax = item.rectText;
		}
		totalValues += values;
		items.add(item);

		float currentAngle = -90F;

		float sizeMin = Math.min(getHeight() - 2 * (rectTextMax.height()),
				getWidth() - 2 * (rectTextMax.width() + 0));
		rectFPie.left = (getWidth() - sizeMin) / 2F;
		rectFPie.right = rectFPie.left + sizeMin;
		rectFPie.top = (getHeight() - sizeMin) / 2F;
		rectFPie.bottom = rectFPie.top + sizeMin;

		float centerX = rectFPie.left + rectFPie.width() / 2F, centerY = rectFPie.top
				+ rectFPie.height() / 2F;

		for (int i = 0; i < items.size(); i++) {
			item = items.get(i);
			item.startAngle = currentAngle;
			if (i != items.size() - 1) {
				item.endAngle = currentAngle + item.values * 360.0F
						/ totalValues;
				currentAngle = item.endAngle;
			} else {
				item.endAngle = 270;
			}

			float mHighlightStrength = 1.12F;
			int highlight = Color.argb(0xff, Math.min(
					(int) (mHighlightStrength * (float) Color.red(item.color)),
					0xff),
					Math.min((int) (mHighlightStrength * (float) Color
							.green(item.color)), 0xff), Math.min(
							(int) (mHighlightStrength * (float) Color
									.blue(item.color)), 0xff));

			item.shader = new SweepGradient(rectFPie.width() / 2.0f,
					rectFPie.height() / 2.0f, new int[] { highlight, highlight,
							item.color, item.color, }, new float[] { 0,
							(float) (360 - item.endAngle) / 360.0f,
							(float) (360 - item.startAngle) / 360.0f, 1.0f });

			float centerAngle = item.startAngle
					+ (item.endAngle - item.startAngle) / 2F;

			item.nameX = (float) (centerX + ((rectFPie.height() + rectFPie
					.width()) / 4F) * Math.cos(Math.toRadians(centerAngle)));
			item.nameY = (float) (centerY + ((rectFPie.height() + rectFPie
					.width()) / 4F) * Math.sin(Math.toRadians(centerAngle)));

			if (centerAngle > -90 && centerAngle < 90) {
				item.nameX += 10;
			} else {
				item.nameX = item.nameX - rectTextMax.width() - 10;
			}
		}

		invalidate();

	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public class Item {
		private String name;
		private float values;
		private int color;
		private Shader shader;
		private Rect rectText;
		private float endAngle;
		private float startAngle;

		private float nameX, nameY;
	}

}
