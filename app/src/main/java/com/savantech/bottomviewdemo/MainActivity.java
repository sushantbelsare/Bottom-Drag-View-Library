package com.savantech.bottomviewdemo;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.savantech.bottomdragview.BottomView;

public class MainActivity extends AppCompatActivity implements BottomView.dragListener{

    BottomView bottomView;
    ImageButton tap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomView = findViewById(R.id.bottom_view);
        tap = findViewById(R.id.tap);
        bottomView.setDragListener(this);
        tap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bottomView.isViewExpanded())
                {
                   bottomView.expandView();
                   Toast.makeText(MainActivity.this,"Expanded!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    bottomView.collapseView();
                    Toast.makeText(MainActivity.this,"Collapsed!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDrag(float offset) {
        Log.d("DRAGGING WITH OFFSET : ",""+offset);
    }
}
