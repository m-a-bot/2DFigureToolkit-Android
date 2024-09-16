package com.example.semestr5.painter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import figures.classes.Circle;
import figures.classes.NGon;
import figures.classes.Polyline;
import figures.classes.QGon;
import figures.classes.Rectangle;
import figures.classes.Segment;
import figures.classes.TGon;
import figures.classes.Trapeze;
import figures.interfaces.IShape;
import points.Point2D;

public class Painter {

    static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public static void Draw(Canvas canvas, List<IShape> figures, int color)
    {
        for (IShape figure : figures) {
            Draw(canvas, figure, color);
        }
    }


    public static void DrawAxis(Canvas canvas)
    {
        paint.setColor(Color.DKGRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        canvas.drawLine(0, (float)(canvas.getHeight()/2.), canvas.getWidth(), (float)(canvas.getHeight()/2.), paint);
        canvas.drawLine((float)(canvas.getWidth()/2.), 0, (float)(canvas.getWidth()/2.), canvas.getHeight(), paint);
    }

    public static void Draw(Canvas canvas, IShape figure, int color)
    {
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        float width = canvas.getWidth(),
                height = canvas.getHeight();

        float scale = 2;

        if (figure.getClass().getSimpleName().equals("Circle")) {
            Circle circle = (Circle) figure;
            double[] x = circle.getP().getX();
            double r = circle.getR();
            canvas.drawCircle((float)(width/2 + x[0]*scale),(float)(height/2 -x[1]*scale),(float)(r*scale),paint);
        }
        if (figure.getClass().getSimpleName().equals("Segment")) {
            Segment segment = (Segment) figure;
            double[] start = segment.getStart().getX();
            double[] finish = segment.getFinish().getX();
            canvas.drawLine((float)(width/2+start[0]*scale),(float)(height/2 -start[1]*scale), (float)(width/2+finish[0]*scale),(float)(height/2 -finish[1]*scale), paint);
        }
        if (figure.getClass().getSimpleName().equals("Polyline")) {
            Polyline polyline = (Polyline) figure;
            Point2D[] p = polyline.getP();

            for (int f = 1; f < p.length; f++) {
                double[] start = p[f - 1].getX();
                double[] finish = p[f].getX();
                canvas.drawLine((float)(width/2+start[0]*scale),(float)(height/2 -start[1]*scale), (float)(width/2+finish[0]*scale),(float)(height/2 -finish[1]*scale), paint);
            }
        }
        if (figure.getClass().getSimpleName().equals("NGon")) {
            NGon ngon = (NGon) figure;
            Point2D[] p = ngon.getP();
            for (int f = 1; f < p.length; f++) {
                double[] start = p[f - 1].getX();
                double[] finish = p[f].getX();
                canvas.drawLine((float)(width/2+start[0]*scale),(float)(height/2 -start[1]*scale), (float)(width/2+finish[0]*scale),(float)(height/2 -finish[1]*scale), paint);
            }
            double[] start = p[p.length - 1].getX();
            double[] finish = p[0].getX();
            canvas.drawLine((float)(width/2+start[0]*scale),(float)(height/2 -start[1]*scale), (float)(width/2+finish[0]*scale),(float)(height/2 -finish[1]*scale), paint);
        }
        if (figure.getClass().getSimpleName().equals("TGon")) {
            TGon ngon = (TGon) figure;
            Point2D[] p = ngon.getP();
            for (int f = 1; f < p.length; f++) {
                double[] start = p[f - 1].getX();
                double[] finish = p[f].getX();
                canvas.drawLine((float)(width/2+start[0]*scale),(float)(height/2 -start[1]*scale), (float)(width/2+finish[0]*scale),(float)(height/2 -finish[1]*scale), paint);
            }
            double[] start = p[p.length - 1].getX();
            double[] finish = p[0].getX();
            canvas.drawLine((float)(width/2+start[0]*scale),(float)(height/2 -start[1]*scale), (float)(width/2+finish[0]*scale),(float)(height/2 -finish[1]*scale), paint);
        }
        if (figure.getClass().getSimpleName().equals("QGon")) {
            QGon ngon = (QGon) figure;
            Point2D[] p = ngon.getP();
            for (int f = 1; f < p.length; f++) {
                double[] start = p[f - 1].getX();
                double[] finish = p[f].getX();
                canvas.drawLine((float)(width/2+start[0]*scale),(float)(height/2 -start[1]*scale), (float)(width/2+finish[0]*scale),(float)(height/2 -finish[1]*scale), paint);
            }
            double[] start = p[p.length - 1].getX();
            double[] finish = p[0].getX();
            canvas.drawLine((float)(width/2+start[0]*scale),(float)(height/2 -start[1]*scale), (float)(width/2+finish[0]*scale),(float)(height/2 -finish[1]*scale), paint);
        }
        if (figure.getClass().getSimpleName().equals("Rectangle")) {
            Rectangle ngon = (Rectangle) figure;
            Point2D[] p = ngon.getP();
            for (int f = 1; f < p.length; f++) {
                double[] start = p[f - 1].getX();
                double[] finish = p[f].getX();
                canvas.drawLine((float)(width/2+start[0]*scale),(float)(height/2 -start[1]*scale), (float)(width/2+finish[0]*scale),(float)(height/2 -finish[1]*scale), paint);
            }
            double[] start = p[p.length - 1].getX();
            double[] finish = p[0].getX();
            canvas.drawLine((float)(width/2+start[0]*scale),(float)(height/2 -start[1]*scale), (float)(width/2+finish[0]*scale),(float)(height/2 -finish[1]*scale), paint);
        }
        if (figure.getClass().getSimpleName().equals("Trapeze")) {
            Trapeze ngon = (Trapeze) figure;
            Point2D[] p = ngon.getP();
            for (int f = 1; f < p.length; f++) {
                double[] start = p[f - 1].getX();
                double[] finish = p[f].getX();
                canvas.drawLine((float)(width/2+start[0]*scale),(float)(height/2 -start[1]*scale), (float)(width/2+finish[0]*scale),(float)(height/2 -finish[1]*scale), paint);
            }
            double[] start = p[p.length - 1].getX();
            double[] finish = p[0].getX();
            canvas.drawLine((float)(width/2+start[0]*scale),(float)(height/2 -start[1]*scale), (float)(width/2+finish[0]*scale),(float)(height/2 -finish[1]*scale), paint);
        }
    }
}
