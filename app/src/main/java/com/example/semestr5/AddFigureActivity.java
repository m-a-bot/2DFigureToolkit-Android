package com.example.semestr5;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import figures.classes.*;
import figures.interfaces.IShape;
import points.Point2D;

public class AddFigureActivity extends AppCompatActivity {

    List<IShape> figs;
    Spinner spinner;
    List<EditText> boxes = new ArrayList<>();

    Spinner counts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_figure);


        LinearLayout data = findViewById(R.id.lldata);

        spinner = findViewById(R.id.spinner);

        counts = findViewById(R.id.count_points_spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                data.removeAllViews();
                boxes.clear();
                counts.setVisibility(View.INVISIBLE);
                switch (i)
                {
                    case 0:
                        data.addView(addText("Точка №1"));
                        data.addView(addRowTextFields(1));
                        data.addView(addText("Точка №2"));
                        data.addView(addRowTextFields(3));
                        break;
                    case 1:
                        counts.setVisibility(View.VISIBLE);
                        for (int k=1, p=1; k <= Integer.parseInt((String) counts.getSelectedItem()); k++, p+=2) {
                            data.addView(addText("Точка №" + k));

                            data.addView(addRowTextFields(p));
                        }
                        break;
                    case 3:
                        counts.setVisibility(View.VISIBLE);
                        counts.setSelection(1);
                        for (int k=1, p=1; k <= Integer.parseInt((String) counts.getSelectedItem()); k++, p+=2) {
                            data.addView(addText("Точка №" + k));

                            data.addView(addRowTextFields(p));
                        }
                        break;
                    case 2:
                        data.addView(addText("Центр: "));
                        data.addView(addRowTextFields(1));

                        LinearLayout row = new LinearLayout(AddFigureActivity.this);
                        row.setOrientation(LinearLayout.HORIZONTAL);

                        TextView tX = new TextView(AddFigureActivity.this);
                        tX.setText("Радиус: ");
                        tX.setTextSize(16);
                        tX.setWidth(200);
                        tX.setHeight(50);


                        EditText textR = new EditText(AddFigureActivity.this);
                        textR.setWidth(250);
                        textR.setHeight(200);
                        textR.setTextSize(16);
                        textR.setId(R.id.lldata + 1);
                        row.addView(tX);
                        row.addView(textR);

                        boxes.add(textR);
                        data.addView(row);
                        break;

                    case 4:
                        for (int k=0, p=1; k < 3; k++, p+=2) {
                            data.addView(addText("Точка №" + (k+1)));
                            data.addView(addRowTextFields(p));
                        }
                        break;
                    case 5:
                    case 6:
                    case 7:
                        for (int k=0, p=1; k < 4; k++, p+=2) {
                            data.addView(addText("Точка №" + (k+1)));
                            data.addView(addRowTextFields(p));
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        counts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                data.removeAllViews();
                boxes.clear();

                for (int k=1, p=1; k <= Integer.parseInt((String) counts.getSelectedItem()); k++, p+=2) {
                    data.addView(addText("Точка №" + k));

                    data.addView(addRowTextFields(p));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }



    public void choice_type_figure(View view) {

        if (spinner.getSelectedItemId() != AdapterView.INVALID_POSITION)
        {
            System.out.println(spinner.getSelectedItemId());
        }

    }

    private LinearLayout addRowTextFields(int id)
    {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView tX = new TextView(this);
        tX.setText("x: ");
        tX.setTextSize(16);
        tX.setWidth(80);
        tX.setHeight(200);


        EditText textX = new EditText(this);
        textX.setWidth(250);
        textX.setHeight(200);
        textX.setTextSize(16);
        textX.setId(R.id.lldata+id);

        TextView tY = new TextView(this);
        tY.setText("y: ");
        textX.setTextSize(16);
        tY.setWidth(80);
        tY.setHeight(200);

        EditText textY = new EditText(this);
        textY.setWidth(250);
        textY.setHeight(200);
        textX.setTextSize(16);
        textY.setId(R.id.lldata+id+1);

        row.addView(tX);
        row.addView(textX);
        row.addView(tY);
        row.addView(textY);

        boxes.add(textX);
        boxes.add(textY);

        return row;
    }

    private TextView addText(String text)
    {
        TextView textView = new TextView(AddFigureActivity.this);
        textView.setHeight(50);
        textView.setWidth(300);
        textView.setTextSize(16);
        textView.setText(text);

        return textView;
    }

    public void addFigure(View view) {

        int id = (int) spinner.getSelectedItemId();

        if (id != AdapterView.INVALID_POSITION) {

            int size = boxes.size();
            double[] p = new double[size];

            for (int i=0; i < size; i++)
            {
                try {

                    if (String.valueOf(boxes.get(i).getText()).isEmpty())
                    {
                        Toast.makeText(this, "Заполните параметры", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    p[i] = Double.parseDouble(String.valueOf(boxes.get(i).getText()));

                } catch (Exception e)
                {
                    Log.d("TAddFig", e.getMessage());
                }
            }


            IShape addedFigure = null;
            if (size % 2 != 0)
            {
                Point2D point = null;
                try {
                    point = new Point2D(new double[]{p[0], p[1]});
                    addedFigure = new Circle(point, p[2]);
                } catch (Exception e) {
                    Log.d("TAddFig", e.getMessage());
                }
            }
            else {
                Point2D[] points = new Point2D[size/2];
                try {

                    for (int i = 1, k = 0; i < size; i += 2, k++) {
                        points[k] = new Point2D(new double[]{p[i - 1], p[i]});
                    }

                } catch (Exception e)
                {
                    Log.d("TAddFig", e.getMessage());
                }

                if (id == 0)
                {
                    try {
                        addedFigure = new Segment(points[0], points[1]);
                        Log.d("Mtag", "Успешно создано");
                    } catch (Exception e) {
                        Log.d("TAddFig", e.getMessage());
                    }
                }
                if (id == 1)
                {
                    addedFigure = new Polyline(points);
                    Log.d("Mtag", "Успешно создано");
                }
                if (id == 3)
                {
                    addedFigure = new NGon(points);
                    Log.d("Mtag", "Успешно создано");
                }
                if (id == 4) {
                    addedFigure = new TGon(points);
                    Log.d("Mtag", "Успешно создано");
                }

                if (id == 5)
                {
                    addedFigure = new QGon(points);
                    Log.d("Mtag", "Успешно создано");
                }
                if (id == 6)
                {
                    addedFigure = new Rectangle(points);
                    Log.d("Mtag", "Успешно создано");
                }
                if (id == 7)
                {
                    addedFigure = new Trapeze(points);
                    Log.d("Mtag", "Успешно создано");
                }
            }

            System.out.println(addedFigure);
            if (addedFigure != null)
            {
                Intent intent = new Intent(AddFigureActivity.this, MainActivity.class);
                intent.putExtra("add", true);
                intent.putExtra("fig", addedFigure);

                setResult(RESULT_OK, intent);

                finish();
            }
        }
    }
}
