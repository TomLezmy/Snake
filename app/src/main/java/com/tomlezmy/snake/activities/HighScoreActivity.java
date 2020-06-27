package com.tomlezmy.snake.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tomlezmy.snake.R;
import com.tomlezmy.snake.model.HighScores;
import com.tomlezmy.snake.model.UserScore;

public class HighScoreActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private HighScores highScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        // Creating a smaller activity to pop above the main screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width*0.8), (int)(height*0.54));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);

        tableLayout = findViewById(R.id.highScoreTableLayout);
        highScores =  HighScores.getInstance(this);

        tableLayout.removeAllViews();

        // Creating the table headers
        TableRow topTableRow = new TableRow  (this);
        topTableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
        topTableRow.setBackgroundResource(R.drawable.table_header_border);
        tableLayout.addView(topTableRow);

        TextView topTextView = new TextView(this);
        topTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.4f));
        topTextView.setText("Rank");
        topTextView.setTextSize(30f);
        topTextView.setGravity(Gravity.CENTER);
        topTextView.setTypeface(null, Typeface.BOLD);
        topTextView.setBackgroundResource(R.drawable.table_header_border);
        topTableRow.addView(topTextView);

        topTextView = new TextView(this);
        topTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f));
        topTextView.setText("Name");
        topTextView.setTextSize(30f);
        topTextView.setGravity(Gravity.CENTER);
        topTextView.setTypeface(null, Typeface.BOLD);
        topTextView.setBackgroundResource(R.drawable.table_header_border);
        topTableRow.addView(topTextView);

        topTextView = new TextView(this);
        topTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f));
        topTextView.setText("Score");
        topTextView.setTextSize(30f);
        topTextView.setGravity(Gravity.CENTER);
        topTextView.setTypeface(null, Typeface.BOLD);
        topTextView.setBackgroundResource(R.drawable.table_header_border);
        topTableRow.addView(topTextView);

        // Filling in the current high scores
        int rank = 1;
        for (UserScore userScore : highScores.getScores()) {
            TableRow tb = new TableRow  (this);
            tb.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
            tb.setBackgroundResource(R.drawable.table_border);
            tableLayout.addView(tb);

            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.4f));
            tv.setText(Integer.toString(rank++));
            tv.setTextSize(24f);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.table_border);
            tb.addView(tv);

            tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f));
            tv.setText(userScore.getUserName());
            tv.setTextSize(24f);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.table_border);
            tb.addView(tv);

            tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f));
            tv.setText(userScore.getScore() + "");
            tv.setTextSize(24f);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.table_border);
            tb.addView(tv);
        }

        // Filling in the empty rows
        while (rank <= 10) {
            TableRow tb = new TableRow  (this);
            tb.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
            tb.setBackgroundResource(R.drawable.table_border);
            tableLayout.addView(tb);

            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.4f));
            tv.setText(Integer.toString(rank++));
            tv.setTextSize(24f);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.table_border);
            tb.addView(tv);

            tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f));
            tv.setTextSize(24f);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.table_border);
            tb.addView(tv);

            tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f));
            tv.setTextSize(24f);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.table_border);
            tb.addView(tv);
        }
    }
}