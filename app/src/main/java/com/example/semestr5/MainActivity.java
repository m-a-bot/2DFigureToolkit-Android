package com.example.semestr5;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.semestr5.painter.Draw2D;

import org.bson.BsonDocument;
import org.bson.Document;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import figures.classes.*;
import figures.interfaces.IShape;
import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

import points.Point2D;




public class MainActivity extends AppCompatActivity {

    List<IShape> shapes = new ArrayList<>();

    MongoCollection<Document> collection;

    Draw2D painter;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {

                TextView info = findViewById(R.id.text_target);
                EditText text = findViewById(R.id.editTextResult);

                info.setVisibility(View.INVISIBLE);
                text.setVisibility(View.INVISIBLE);
                painter.removeAllSelectedFigure();

                if (result.getResultCode() == Activity.RESULT_OK)
                {
                    Intent intent = result.getData();

                    try {

                        boolean add = intent.getBooleanExtra("add", false);

                        IShape fig = (IShape) intent.getSerializableExtra("fig");

                        boolean fig_delete = intent.getBooleanExtra("fig_delete", false);

                        int id_fig = intent.getIntExtra("id_figure", -1);

                        boolean move = intent.getBooleanExtra("move", false);

                        int cross = intent.getIntExtra("Cross", -1);

                        IShape fig1 = (IShape) intent.getSerializableExtra("fig1"),
                                fig2 = (IShape) intent.getSerializableExtra("fig2");

                        double square = intent.getDoubleExtra("Square", -1);

                        double perimeter = intent.getDoubleExtra("Perimeter", -1);

                        if (add && fig != null)
                        {
                            shapes.add(fig);
                            Toast.makeText(this, "Фигура добавлена", Toast.LENGTH_SHORT).show();
                        }

                        if (fig_delete)
                        {
                            shapes.remove(id_fig);
                            Toast.makeText(this, "Фигура удалена", Toast.LENGTH_SHORT).show();
                        }

                        if (move && fig != null)
                        {
                            shapes.set(id_fig, fig);
                            painter.addSelectedFigure(shapes.get(id_fig));
                            Toast.makeText(this, "Фигура перемещена", Toast.LENGTH_SHORT).show();
                        }

                        if (cross != -1 || square != -1 || perimeter != -1) {
                            info.setVisibility(View.VISIBLE);
                            text.setVisibility(View.VISIBLE);
                        }

                        if (cross != -1)
                        {
                            painter.addSelectedFigure(fig1);
                            painter.addSelectedFigure(fig2);

                            Toast.makeText(this, "Пересечение подсчитано", Toast.LENGTH_SHORT).show();

                            info.setText("Пересечение:");
                            if (cross == 0)
                                text.setText("Не пересекаются");
                            else
                                text.setText("Пересекаются");
                        }

                        if (square != -1)
                        {

                            painter.addSelectedFigure(shapes.get(id_fig));
                            Toast.makeText(this, "Площадь подсчитана", Toast.LENGTH_SHORT).show();
                            info.setText("Площадь:");
                            text.setText(String.format("%s", square));
                        }

                        if (perimeter != -1)
                        {

                            painter.addSelectedFigure(shapes.get(id_fig));
                            info.setText("Периметр:");
                            Toast.makeText(this, "Периметр подсчитан", Toast.LENGTH_SHORT).show();
                            text.setText(String.format("%s", perimeter));
                        }

                        painter.invalidate();

                    } catch (Exception e)
                    {

                    }
                }
            });



    ActivityResultLauncher<String> create_txt = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("text/plain"), result -> {

                if (result != null)
                {

                    try {
                        var out = getContentResolver().openOutputStream(result, "w");

                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);

                        String size_figures = shapes.size() + "\n";


                        bufferedOutputStream.write(size_figures.getBytes(StandardCharsets.UTF_8));


                        for(IShape x : shapes)
                        {
                            String fig = x + "\n";
                            bufferedOutputStream.write(fig.getBytes(StandardCharsets.UTF_8));
                        }

                        bufferedOutputStream.close();
                        out.close();

                        Toast.makeText(MainActivity.this, "Фигуры сохранены в файл!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e)
                    {
                        Log.d("tg", e.getMessage());
                    }
                }
            });

    ActivityResultLauncher<String> create_image = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("image/jpeg"), result -> {
                if (result != null)
                {
                    try {

                        var stream = getContentResolver().openOutputStream(result, "w");

                        painter.save(stream);

                        stream.close();

                        Toast.makeText(MainActivity.this, "Изображение сохранено!", Toast.LENGTH_SHORT).show();
                    } catch (IOException ioException)
                    {
                        Log.e("IOException", ioException.getMessage());
                    }
                }
            });

    ActivityResultLauncher<String> load_doc = registerForActivityResult(
            new ActivityResultContracts.GetContent(), result -> {
                if (result != null)
                {
                    List<IShape> figures1 = new ArrayList<>();

                    try {

                        var in = getContentResolver().openInputStream(result);

                        StringBuilder s = new StringBuilder();

                        byte[] buffer = new byte[65536];

                        while (in.available() > 0)
                        {
                            int d = in.read(buffer);
                            s.append(new String(buffer));
                        }

                        String res = s.toString();

                        String[] lines = res.split("\n");

                        for (int i=1; i < lines.length; i++)
                            add_figures(lines[i], figures1);

                        in.close();

                        for (var x : figures1)
                            System.out.println(x);



                        Toast.makeText(MainActivity.this, "Фигуры загружены из файла!", Toast.LENGTH_SHORT).show();

                        shapes.clear();

                        shapes.addAll(figures1);

                        painter.removeAllSelectedFigure();

                    } catch (Exception e)
                    {
                        Log.d("tg", e.getMessage());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        painter = findViewById(R.id.draw2d);

        painter.setFigures(shapes);

        connectDB();
    }


    // +
    public void add_figure(View view) {
        Intent intent = new Intent(this, AddFigureActivity.class);

        mStartForResult.launch(intent);
    }

    // +
    public void del_figure(View view) {
        Intent intent = new Intent(this, DelFigureActivity.class);

        intent.putExtra("figures", (Serializable) shapes);

        mStartForResult.launch(intent);
    }

    // It's done
    public void save_to_db(View view) {
       try {
           this.addFigures(shapes);
           Toast.makeText(this, "Данные загружены в БД.", Toast.LENGTH_SHORT).show();
       } catch (Exception e)
       {
           Toast.makeText(this, "Данные не загружены в БД. " + e.getMessage(), Toast.LENGTH_SHORT).show();
       }
    }

    // It's done
    public void load_from_db(View view) throws Exception {

        painter.removeAllSelectedFigure();
        List<IShape> figures1 = new ArrayList<>();

        collection.find().iterator().getAsync(result -> {
            if (result.isSuccess()) {
                System.out.println("OK3");
                MongoCursor<Document> d = result.get();

                while (d.hasNext()) {
                    try {
                        getData(d.next(), figures1);
                        System.out.println("Load");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (figures1.size() == 0) {
                    System.out.println("Список пустой");
                    return;
                }

                shapes.clear();

                shapes.addAll(figures1);

                painter.invalidate();

                Toast.makeText(this, "Данные загружены из БД.", Toast.LENGTH_SHORT).show();
            }
            else
                System.out.println("E2");
        });
    }



    // +
    public void save_to_file(View view) {

        create_txt.launch("");

    }

    // +
    public void load_from_file(View view) {

        load_doc.launch("text/plain");

    }

    // +
    public void save_image(View view) {

        create_image.launch("");

    }



    // It's done
    public void clear_figures(View view) {
        shapes.clear();
        painter.removeAllSelectedFigure();

        TextView info = findViewById(R.id.text_target);
        EditText text = findViewById(R.id.editTextResult);

        info.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);

        painter.invalidate();
        Toast.makeText(this, "Фигуры удалены.", Toast.LENGTH_SHORT).show();
    }

    // +
    public void movement_figure(View view) {
        Intent intent = new Intent(this, MoveFigureActivity.class);
        intent.putExtra("figures", (Serializable) shapes);

        mStartForResult.launch(intent);
    }

    // +
    public void cross_figures(View view) {
        Intent intent = new Intent(this, CrossFigureActivity.class);
        intent.putExtra("figures", (Serializable) shapes);

        mStartForResult.launch(intent);
    }


    // +
    public void perimeter_figure(View view) {
        Intent intent = new Intent(this, PerimeterFigureActivity.class);
        intent.putExtra("figures", (Serializable) shapes);

        mStartForResult.launch(intent);
    }


    // +
    public void square_figure(View view) {
        Intent intent = new Intent(this, SquareFigureActivity.class);
        intent.putExtra("figures", (Serializable) shapes);

        mStartForResult.launch(intent);
    }

    public void addFigures(List<IShape> shapes)
    {
        if (collection == null)
            return;
        collection.deleteMany(new BsonDocument()).getAsync(result -> {
            if (result.isSuccess()) {
                Log.v("Data", "Data deleted");

                List<Document> docs = new ArrayList<>();

                for(IShape shape : shapes)
                {
                    docs.add(shape.bsonDocument());
                }
                collection.insertMany(docs).getAsync(result1 -> {
                    if (result1.isSuccess())
                        Log.v("Data", "Data inserted");
                    else
                        Log.e("Error", "Data did not inserted");
                });

            }else
                Log.e("Error", "Error");
        });
    }

    public List<IShape> getData(Document d, List<IShape> data) throws Exception {

        List<Document> circle_points = d.getList("circle", Document.class);
        try {
            if (circle_points != null) {
                List<Double> x = circle_points.get(0).getList("coords", Double.class);
                double r = circle_points.get(1).getDouble("radius");

                data.add(new Circle(new Point2D(array(x)), r));
            }

            List<Document> segment_points = d.getList("segment", Document.class);
            if (segment_points != null) {
                List<Double> start = circle_points.get(0).getList("coords", Double.class);
                List<Double> finish = circle_points.get(1).getList("coords", Double.class);

                data.add(new Segment(new Point2D(array(start)), new Point2D(array(finish))));
            }
            List<Document> polyline_points = d.getList("polyline", Document.class);
            if (polyline_points != null) {
                Point2D[] p = new Point2D[polyline_points.size()];
                for (int i = 0; i < polyline_points.size(); i++)
                    p[i] = new Point2D(array(polyline_points.get(i).getList("coords", Double.class)));
                data.add(new Polyline(p));
            }
            List<Document> ngon_points = d.getList("ngon", Document.class);
            if (ngon_points != null) {
                Point2D[] p = new Point2D[ngon_points.size()];
                for (int i = 0; i < ngon_points.size(); i++)
                    p[i] = new Point2D(array(ngon_points.get(i).getList("coords", Double.class)));
                data.add(new NGon(p));
            }
            List<Document> qgon_points = d.getList("qgon", Document.class);
            if (qgon_points != null) {
                Point2D[] p = new Point2D[qgon_points.size()];
                for (int i = 0; i < qgon_points.size(); i++)
                    p[i] = new Point2D(array(qgon_points.get(i).getList("coords", Double.class)));
                data.add(new QGon(p));
            }
            List<Document> tgon_points = d.getList("tgon", Document.class);
            if (tgon_points != null) {
                Point2D[] p = new Point2D[tgon_points.size()];
                for (int i = 0; i < tgon_points.size(); i++)
                    p[i] = new Point2D(array(tgon_points.get(i).getList("coords", Double.class)));
                data.add(new TGon(p));
            }
            List<Document> rectangle_points = d.getList("rectangle", Document.class);
            if (rectangle_points != null) {
                Point2D[] p = new Point2D[rectangle_points.size()];
                for (int i = 0; i < rectangle_points.size(); i++)
                    p[i] = new Point2D(array(rectangle_points.get(i).getList("coords", Double.class)));
                data.add(new Rectangle(p));
            }
            List<Document> trapeze_points = d.getList("trapeze", Document.class);
            if (trapeze_points != null) {
                Point2D[] p = new Point2D[trapeze_points.size()];
                for (int i = 0; i < trapeze_points.size(); i++)
                    p[i] = new Point2D(array(trapeze_points.get(i).getList("coords", Double.class)));
                data.add(new Trapeze(p));
            }
        } catch (Exception e) {
            System.out.println("Ошибка " + e.getMessage());
        }

        return data;
    }
    private double[] array(List<Double> doubleList)
    {
        double[] values = new double[doubleList.size()];
        for (int i=0; i<doubleList.size();i++)
            values[i] = doubleList.get(i);
        return values;
    }

    private void add_figures(String s, List<IShape> figures)
    {
        String x = s;
        double[] numbers = this.numbersInLine(s);
        try {
            if (x.startsWith("Circle")) {
                figures.add(new Circle(new Point2D(new double[]{numbers[0], numbers[1]}),
                        numbers[2]));
            }
            if (x.startsWith("Segment")) {
                figures.add(new Segment(new Point2D(new double[]{numbers[0], numbers[1]}),
                        new Point2D(new double[]{numbers[2], numbers[3]})));
            }
            if (x.startsWith("Polyline")) {
                Point2D[] points = new Point2D[numbers.length / 2];
                for (int i = 1, k = 0; i < numbers.length; i += 2, k++) {
                    points[k] = new Point2D(new double[]{numbers[i - 1], numbers[i]});
                }
                figures.add(new Polyline(points));
            }
            if (x.startsWith("NGon")) {
                Point2D[] points = new Point2D[numbers.length / 2];
                for (int i = 1, k = 0; i < numbers.length; i += 2, k++) {
                    points[k] = new Point2D(new double[]{numbers[i - 1], numbers[i]});
                }
                figures.add(new NGon(points));
            }
            if (x.startsWith("TGon")) {
                Point2D[] points = new Point2D[numbers.length / 2];
                for (int i = 1, k = 0; i < numbers.length; i += 2, k++) {
                    points[k] = new Point2D(new double[]{numbers[i - 1], numbers[i]});
                }
                figures.add(new TGon(points));
            }
            if (x.startsWith("QGon")) {
                Point2D[] points = new Point2D[numbers.length / 2];
                for (int i = 1, k = 0; i < numbers.length; i += 2, k++) {
                    points[k] = new Point2D(new double[]{numbers[i - 1], numbers[i]});
                }
                figures.add(new QGon(points));
            }
            if (x.startsWith("Rectangle")) {
                Point2D[] points = new Point2D[numbers.length / 2];
                for (int i = 1, k = 0; i < numbers.length; i += 2, k++) {
                    points[k] = new Point2D(new double[]{numbers[i - 1], numbers[i]});
                }
                figures.add(new Rectangle(points));
            }
            if (x.startsWith("Trapeze")) {
                Point2D[] points = new Point2D[numbers.length / 2];
                for (int i = 1, k = 0; i < numbers.length; i += 2, k++) {
                    points[k] = new Point2D(new double[]{numbers[i - 1], numbers[i]});
                }
                figures.add(new Trapeze(points));
            }
        } catch (Exception e)
        {

        }
    }

    private double[] numbersInLine(String line)
    {
        Pattern pattern = Pattern.compile("-?\\d+\\.\\d+");
        Matcher m = pattern.matcher(line);
//        System.out.println(line);
        List<Double> d = new ArrayList<>();
        int k = 0;

        while (m.find()) {
            d.add(Double.parseDouble(m.group()));
        }

        double[] num = new double[d.size()];
        for (int i=0; i < d.size(); i++)
            num[i] = d.get(i);
        return num;
    }

    public void connectDB()
    {
        Realm.init(this);

        String appId = "data-qfjlp";

        AppConfiguration appConfiguration = new AppConfiguration.Builder(appId)
                .appName(BuildConfig.VERSION_NAME)
                .appVersion(Integer.toString(BuildConfig.VERSION_CODE))
                .build();

        App app = new App(appConfiguration);

        Credentials credentials = Credentials.anonymous();

        app.loginAsync(credentials, new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if (result.isSuccess()) {
                    System.out.println("Yes");
                    Log.v("QUICKSTART", "Successfully authenticated anonymously.");
                    User user = app.currentUser();

                    assert user != null;
                    MongoClient mongoClient = user.getMongoClient("MyCluster");
                    MongoDatabase db = mongoClient.getDatabase("figures");

                    collection = db.getCollection("current_state_test");

                } else {
                    Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
                }
            }
        });
    }
}