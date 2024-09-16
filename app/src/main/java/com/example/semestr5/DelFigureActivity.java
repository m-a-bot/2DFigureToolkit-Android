package com.example.semestr5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

import figures.interfaces.IShape;

public class DelFigureActivity extends AppCompatActivity {

    Spinner spinner;

    List<IShape> figures;

    ArrayAdapter<IShape> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del_figure);

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

        spinner = findViewById(R.id.spinner_figure);

        adapter = new ArrayAdapter<IShape>(this, android.R.layout.simple_spinner_item, figures);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);



    }

    public void delete_figure(View view) {

        int id_figure = (int) spinner.getSelectedItemId();

        if (spinner.getCount() == 0)
        {
            Toast.makeText(this, "Нет фигур", Toast.LENGTH_SHORT).show();
            return;
        }

        if (id_figure != AdapterView.INVALID_POSITION)
        {
            Intent intent = new Intent(DelFigureActivity.this, MainActivity.class);
            intent.putExtra("id_figure", id_figure);
            intent.putExtra("fig_delete", true);

            setResult(RESULT_OK, intent);
            finish();
        }

    }


}
