package com.example.semestr5.painter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import figures.interfaces.IShape;

public class Draw2D extends View {

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private List<IShape> figures;

    public void setFigures(List<IShape> figures) {
        this.figures = figures;
    }

    public void addSelectedFigure(IShape selectedFigure) {
        this.selectedFigure.add(selectedFigure);
    }

    public void removeAllSelectedFigure()
    {
        this.selectedFigure.clear();
    }

    private List<IShape> selectedFigure = new ArrayList<>();

    public Draw2D(Context context) {
        super(context);
    }

    public Draw2D(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Draw2D(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.save();

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0,0, getWidth(), getHeight(), paint);

        Painter.DrawAxis(canvas);

        if (figures != null)
            Painter.Draw(canvas, figures, Color.GREEN);

        if (selectedFigure != null)
        {
            Painter.Draw(canvas, selectedFigure, Color.RED);
        }

        canvas.restore();

    }

    public void save(OutputStream stream)
    {
        Bitmap  bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        } catch (Exception e)
        {
            Log.d("MTag", "Изображение не сохранено");
        }
    }
}
