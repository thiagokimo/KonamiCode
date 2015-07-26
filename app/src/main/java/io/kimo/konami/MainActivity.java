package io.kimo.konami;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.kimo.konamicode.KonamiCode;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new KonamiCode.Builder(this)
                .into(this)
                .install();
    }
}
