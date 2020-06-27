package com.tomlezmy.snake.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.tomlezmy.snake.fragments.PrefFragment;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().add(android.R.id.content, new PrefFragment()).commit();

    }

}