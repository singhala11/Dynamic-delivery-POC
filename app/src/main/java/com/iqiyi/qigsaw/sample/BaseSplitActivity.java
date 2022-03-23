package com.iqiyi.qigsaw.sample;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.splitcompat.SplitCompat;


    public abstract class BaseSplitActivity extends AppCompatActivity {

        @Override
        protected void attachBaseContext(Context newBase) {
            super.attachBaseContext(newBase);
            SplitCompat.install(this);
        }
    }

