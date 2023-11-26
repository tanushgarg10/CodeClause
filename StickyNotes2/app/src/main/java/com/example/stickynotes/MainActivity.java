package com.example.stickynotes;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText noteEdt;
    private Button increaseSizeBtn, decreaseSizeBtn, boldBtn, underLineBtn, italicBtn;
    private TextView sizeTV;
    float currentSize;

    // creating a new note
    StickyNote note = new StickyNote();

    @Override
    protected
    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noteEdt = findViewById(R.id.idEdt);
        increaseSizeBtn = findViewById(R.id.idBtnIncreaseSize);
        decreaseSizeBtn = findViewById(R.id.idBtnReduceSize);
        boldBtn = findViewById(R.id.idBtnBold);
        underLineBtn = findViewById(R.id.idBtnUnderline);
        italicBtn = findViewById(R.id.idBtnItalic);
        sizeTV = findViewById(R.id.idTVSize);
        currentSize = noteEdt.getTextSize();
        sizeTV.setText(""+currentSize);
        increaseSizeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sizeTV.setText(""+currentSize);
                currentSize++;
                noteEdt.setTextSize(currentSize);
            }
        });
        decreaseSizeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sizeTV.setText(""+currentSize);
                currentSize--;
                noteEdt.setTextSize(currentSize);
            }
        });
        boldBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                italicBtn.setTextColor(getResources().getColor(R.color.white));
                italicBtn.setBackgroundColor(getResources().getColor(R.color .purple_200));
                if (noteEdt. getTypeface().isBold()){
                    noteEdt.setTypeface (Typeface.DEFAULT);
                    boldBtn.setTextColor(getResources().getColor(R.color.white));
                    boldBtn.setBackgroundColor (getResources().getColor(R.color.purple_200));
                }else {
                    boldBtn.setTextColor(getResources().getColor(R.color.purple_200));
                    boldBtn.setBackgroundColor(getResources().getColor(R.color.white));
                    noteEdt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            }
        });
        underLineBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(noteEdt.getPaintFlags()==8) {
                    underLineBtn.setTextColor(getResources().getColor(R.color.white));
                    underLineBtn.setBackgroundColor(getResources().getColor(R.color.purple_200));
                    noteEdt.setPaintFlags(noteEdt.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                }
                else {
                underLineBtn.setTextColor (getResources().getColor(R.color.purple_200));
                underLineBtn.setBackgroundColor (getResources().getColor(R.color.white));
                noteEdt.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                }
            }
        });
        italicBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boldBtn.setTextColor(getResources().getColor(R.color.white));
                boldBtn.setBackgroundColor(getResources().getColor (R. color. purple_200));
                if(noteEdt.getTypeface() .isItalic()){
                    noteEdt.setTypeface(Typeface.DEFAULT);
                    italicBtn.setTextColor(getResources().getColor(R.color.white));
                    italicBtn.setBackgroundColor (getResources() .getColor(R.color.purple_200));
                }else {
                    italicBtn.setTextColor(getResources().getColor(R.color.purple_200));
                    italicBtn.setBackgroundColor(getResources().getColor(R.color.white));
                    noteEdt.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                }
            }
        });

    }
    public void updateWidget() {
        // the AppWidgetManager helps
        // us to manage all the
        // widgets from this app
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        // the RemoteViews class allows us to inflate a
        // layout resource file hierarchy and provides some
        // basic operations for modifying the content of the
        // inflated hierarchy
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.new_app_widget);

        // by using ComponentName class we can get specific
        // application component and to identify the
        // component we pass the package name and the class
        // name inside of that package
        ComponentName thisWidget = new ComponentName(this, AppWidget.class);

        // update the text of the textview of the widget
        remoteViews.setTextViewText(R.id.appwidget_text, noteEdt.getText().toString());

        // finally us the AppWidgetManager instance to
        // update all the widgets
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    // this function saves
    // the current status
    // of the EditText
    public void saveButton(android.view.View v) {
        // update the content of file stored in the memory
        note.setStick(noteEdt.getText().toString(), this);
        updateWidget();
        Toast.makeText(this, "Updated Successfully!!", Toast.LENGTH_SHORT).show();
    }
}
