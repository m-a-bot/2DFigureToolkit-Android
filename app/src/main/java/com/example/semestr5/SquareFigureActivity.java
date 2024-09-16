package com.example.semestr5;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import figures.interfaces.IShape;

public class SquareFigureActivity extends AppCompatActivity {

    Spinner spinner;
    List<IShape> figures;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_figure);

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

        spinner = findViewById(R.id.spinner5);

        ArrayAdapter<IShape> adapter = new ArrayAdapter<IShape>(this, android.R.layout.simple_spinner_item, figures);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    public void calculate(View view) {

        int id_figure = (int) spinner.getSelectedItemId();

        if (id_figure != AdapterView.INVALID_POSITION)
        {
            try {
                Intent intent = new Intent(SquareFigureActivity.this, MainActivity.class);
                intent.putExtra("Square", figures.get(id_figure).square());
                intent.putExtra("id_figure", id_figure);



                setResult(RESULT_OK, intent);
                System.out.println("Success" +" " +"Площадь подсчитана!");
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Нет фигур", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
