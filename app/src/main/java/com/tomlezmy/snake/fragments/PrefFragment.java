package com.tomlezmy.snake.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.tomlezmy.snake.R;


public class PrefFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ListPreference listPreferenceSnake = (ListPreference) findPreference("preference_snake_design");
        if(listPreferenceSnake.getValue().length() != 1) {
            // to ensure we don't get a null value
            // set first value by default
            listPreferenceSnake.setValueIndex(0);
        }
        ListPreference listPreferenceFood = (ListPreference) findPreference("preference_snake_food");
        if(listPreferenceFood.getValue().length() != 1) {
            // to ensure we don't get a null value
            // set first value by default
            listPreferenceFood.setValueIndex(0);
        }
        ListPreference listPreferenceGameSpeed = (ListPreference) findPreference("preference_game_speed");
        if (listPreferenceGameSpeed.getValue() == null) {
            listPreferenceGameSpeed.setValueIndex(1);
        }

        listPreferenceGameSpeed.setOnPreferenceChangeListener(this);
        listPreferenceSnake.setOnPreferenceChangeListener(this);
        listPreferenceFood.setOnPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast.makeText(getActivity(), "Changes will apply next game", Toast.LENGTH_SHORT).show();
        return true;
    }
}
