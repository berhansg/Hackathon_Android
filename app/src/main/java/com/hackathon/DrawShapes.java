package com.hackathon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawShapes extends View
{
    private List<Element> elements;
    private Paint paint = new Paint();
    public DrawShapes(Context context)
    {
        super(context);
    }

    public DrawShapes(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DrawShapes(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void setElements(List<Element> elmts)
    {
        Log.e("HACK", "__1__");
        if(elements == null) elements = new ArrayList<>();
        elements.clear();
        if(elmts != null)
        {
            Log.e("HACK", "__2__: " + elmts.size());
            elements.addAll(elmts);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(elements != null && elements.size() > 0)
        {
            for(Element element: elements)
            {
                paint.setColor(Color.parseColor("#"+element.color));

                Log.e("HACK", "__3__: " + element.type);
                if(element.type.equals("circle"))
                {
                    canvas.drawCircle(element.xPosition, element.yPosition, element.r, paint);
                }
                else if(element.type.equals("rectangle"))
                {
                    canvas.drawRect(element.xPosition, element.yPosition, element.xPosition + element.width, element.yPosition + element.height, paint);
                }
            }
        }
    }
}
