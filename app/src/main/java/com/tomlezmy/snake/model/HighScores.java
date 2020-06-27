package com.tomlezmy.snake.model;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class HighScores {
    private static HighScores instance;
    private List<UserScore> scores;
    private File highScoreFile;

    private HighScores(Context context) {
        // Read high scores from file or create new file if doesn't exist
        scores = new ArrayList<>();
        highScoreFile = new File(context.getFilesDir().getAbsolutePath(),"high_scores");
        if (highScoreFile.exists()) {
            readScores();
        }
        else {
            try {
                highScoreFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static HighScores getInstance(Context context) {
        if (instance == null) {
            instance = new HighScores(context);
        }
        return instance;
    }

    private void readScores() {
        try {
            FileInputStream fis = new FileInputStream(highScoreFile);
            ObjectInputStream ois =new ObjectInputStream(fis);
            scores = (List<UserScore>)ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void writeScores() {
        try {
            FileOutputStream fos = new FileOutputStream(highScoreFile,false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(scores);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean checkHighScore(int scoreToCheck) {
        if (scores.isEmpty() || scores.size() < 10) {
            return true;
        }
        return scores.get(scores.size() - 1).getScore() < scoreToCheck;
    }

    public void addHighScore(UserScore userScore) {
        int scoreIndex;

        for (scoreIndex = 0; scoreIndex < scores.size(); scoreIndex++) {
            if (scores.get(scoreIndex).getScore() < userScore.getScore()) {
                break;
            }
        }

        scores.add(scoreIndex, userScore);
        if (scores.size() > 10) {
            scores.remove(scores.get(10));
        }
        writeScores();
    }

    public List<UserScore> getScores() {return scores;}
}
