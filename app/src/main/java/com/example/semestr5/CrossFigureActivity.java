package com.example.semestr5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import figures.interfaces.IShape;

public class CrossFigureActivity extends AppCompatActivity {

    Spinner type_figure, spinner1, spinner2;

    List<IShape> figures;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_figure);

        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras != null)
                figures = (List<IShape>) extras.getSerializable("figures");
            else
                figures = null;
        } else {
            figures = (List<IShape>) savedInstanceState.getSerializable("figures");
        }

        type_figure = findViewById(R.id.spinner_type_figure);
        spinner1 = findViewById(R.id.spinner_figure_type1);
        spinner2 = findViewById(R.id.spinner_figure_type2);

        String[] typeFigures = new String[] {"Segment", "Polyline", "Circle", "NGon", "TGon", "QGon", "Rectangle", "Trapeze"};

        type_figure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (figures != null) {
                    setAdapterSpinner(spinner1, getFilterOutedList(figures, typeFigures[i]));
                    setAdapterSpinner(spinner2, getFilterOutedList(figures, typeFigures[i]));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void calculate(View view)
    {
        int id1 = (int) spinner1.getSelectedItemId(),
                id2 = (int) spinner2.getSelectedItemId();

        if (spinner1.getCount() == 0 || spinner2.getCount() == 0)
        {
            Toast.makeText(this, "Недостаточно фигур", Toast.LENGTH_SHORT).show();
            return;
        }

        if (id1 != AdapterView.INVALID_POSITION && id2 != AdapterView.INVALID_POSITION)
        {
            IShape sh1 = (IShape) spinner1.getSelectedItem(),
            sh2 = (IShape) spinner2.getSelectedItem();

            if (sh1 == sh2)
            {
                Toast.makeText(this, "Выберите разные фигуры", Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    Intent intent = new Intent(CrossFigureActivity.this, MainActivity.class);
                    int res = 0;
                    if (sh1.cross(sh2))
                        res = 1;
                    intent.putExtra("Cross", res);
                    intent.putExtra("fig1", sh1);
                    intent.putExtra("fig2", sh2);

                    setResult(RESULT_OK, intent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void setAdapterSpinner(Spinner comboBox, List<IShape> list)
    {
        ArrayAdapter<IShape> adapter = new ArrayAdapter<IShape>(this, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        comboBox.setAdapter(adapter);
    }

    private List<IShape> getFilterOutedList(List<IShape> figures, String typeData)
    {
        List<IShape> filterList = new ArrayList<>();

        for (IShape x : figures)
        {
            if (x.getClass().getSimpleName().equals(typeData))
                filterList.add(x);
        }

        return filterList;
    }
}
