package com.meetme.tuneablegame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.meetme.tuneablegame.Gapper.GapperController;

public class GameActivity extends AppCompatActivity {
    public static final String ID = "RS Activity";

    public static final String MY_AD_UNIT_ID = "1234";
    GameView gameView;
    Button btnRestart;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(ID,"oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = (GameView) findViewById(R.id.gameview);
        btnRestart = (Button) findViewById(R.id.btn_restart);

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GapperController) gameView.mGameThread.mController).downloadConfig();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(ID,"onresume");
        //gameView.resume();
        getWindow().setBackgroundDrawable(null);
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(ID,"onpause");
        //gameView.pause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(ID,"ondestroy");
        finish();
    }
}
