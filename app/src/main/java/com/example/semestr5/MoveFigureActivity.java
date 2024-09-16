package com.example.semestr5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.Serializable;
import java.util.List;

import figures.interfaces.IShape;
import points.Point2D;

public class MoveFigureActivity extends AppCompatActivity {

    Spinner spinner;

    Spinner type_movement;

    List<IShape> figures;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_figure);

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

        LinearLayout d = findViewById(R.id.lldata);

        spinner = findViewById(R.id.spinner2);

        type_movement = findViewById(R.id.spinner3);

        type_movement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                d.removeAllViews();
                if (i == 0)
                {
                    TextView vect = new TextView(MoveFigureActivity.this);

                    vect.setWidth(350);
                    vect.setHeight(50);
                    vect.setTextSize(16);
                    vect.setText("Вектор сдвига: ");

                    d.addView(vect);
                    d.addView(addRowTextFields(1));
                }
                else if (i == 1)
                {
                    LinearLayout row = new LinearLayout(MoveFigureActivity.this);
                    row.setOrientation(LinearLayout.HORIZONTAL);

                    TextView textView = new TextView(MoveFigureActivity.this);
                    textView.setWidth(350);
                    textView.setHeight(50);
                    textView.setTextSize(16);
                    textView.setText("Угол поворота: ");

                    EditText txt = new EditText(MoveFigureActivity.this);
                    txt.setWidth(250);
                    txt.setHeight(200);
                    txt.setTextSize(16);
                    txt.setId((R.id.lldata+1));

                    row.addView(textView);
                    row.addView(txt);

                    d.addView(row);
                }
                else if (i == 2)
                {
                    LinearLayout row = new LinearLayout(MoveFigureActivity.this);
                    row.setOrientation(LinearLayout.HORIZONTAL);

                    TextView textView = new TextView(MoveFigureActivity.this);
                    textView.setWidth(350);
                    textView.setHeight(50);
                    textView.setTextSize(16);
                    textView.setText("Ось симметрии: ");

                    row.addView(textView);

                    Spinner axes = new Spinner(MoveFigureActivity.this);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MoveFigureActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.name_axes));

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    axes.setId((R.id.lldata+1));

                    axes.setAdapter(adapter);
                    row.addView(axes);

                    d.addView(row);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<IShape> adapter = new ArrayAdapter<IShape>(this, android.R.layout.simple_spinner_item, figures);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

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

        return row;
    }

    public void movement_figure(View view) {

        LinearLayout d = findViewById(R.id.lldata);

        if (spinner.getCount() == 0)
        {
            Toast.makeText(this, "Нет фигур", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = (int) spinner.getSelectedItemId();

        int id_movement = (int) type_movement.getSelectedItemId();

        if (id != AdapterView.INVALID_POSITION && id_movement != AdapterView.INVALID_POSITION )
        {
            if (id_movement == 0)
            {
                try {
                    EditText text1 = d.findViewById((R.id.lldata+1)),
                    text2 = d.findViewById((R.id.lldata+2));
                    System.out.println(text1.getText() +" "+ text2.getText());

                    if (String.valueOf(text1.getText()).isEmpty() || String.valueOf(text2.getText()).isEmpty())
                    {
                        Toast.makeText(this, "Заполните параметры", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Point2D mov = new Point2D(new double[]{Double.parseDouble(String.valueOf(text1.getText())),
                            Double.parseDouble(String.valueOf(text2.getText()))});

                    figures.get(id).shift(mov);

                } catch (Exception e)
                {
                    Log.d("TMoveFig", e.getMessage());
                }
            }
            else if (id_movement == 1)
            {
                EditText text = d.findViewById((R.id.lldata+1));

                if (String.valueOf(text.getText()).isEmpty())
                {
                    Toast.makeText(this, "Заполните параметры", Toast.LENGTH_SHORT).show();
                    return;
                }

                double phi = Double.parseDouble(String.valueOf(text.getText()));

                figures.get(id).rot(phi);
            }
            else if (id_movement == 2)
            {
                Spinner axes = d.findViewById((R.id.lldata+1));
                try {
                    String ax = String.valueOf(axes.getSelectedItem());

                    if (ax.equals("x"))
                        figures.get(id).symAxis(0);
                    if (ax.equals("y"))
                        figures.get(id).symAxis(1);

                } catch (Exception e)
                {
                    Log.d("TMoveFig", e.getMessage());
                }
                
            }
            IShape movingFig = figures.get(id);

            Intent intent = new Intent(MoveFigureActivity.this, MainActivity.class);

            intent.putExtra("move", true);
            intent.putExtra("fig", movingFig);
            intent.putExtra("id_figure", id);
            Log.d("Mtag", "Успешно перемещена фигура");

            setResult(RESULT_OK, intent);

            finish();
        }
    }
}
