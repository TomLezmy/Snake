package com.tomlezmy.snake.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tomlezmy.snake.R;
import com.tomlezmy.snake.fragments.AddHighScoreDialogFragment;
import com.tomlezmy.snake.model.EDirections;
import com.tomlezmy.snake.model.HighScores;
import com.tomlezmy.snake.model.LevelManager;
import com.tomlezmy.snake.model.Point;
import com.tomlezmy.snake.model.UserScore;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

// credit to eugeneloza for assets https://opengameart.org/users/eugeneloza
public class MainActivity extends AppCompatActivity implements AddHighScoreDialogFragment.MyDialogListener {
    final int BOARD_SIZE = 30;
    final String ADD_HIGH_SCORE_TAG = "ADD_HIGH_SCORE_TAG";
    int gameSpeed, chosenFoodAsset, chosenSnakeAsset;
    ImageButton btnUp, btnDown, btnLeft, btnRight;
    Button startBtn, pauseBtn;
    RelativeLayout gameLayout;
    ImageView[][] board;
    TextView scoreText;
    Queue<EDirections> directionQueue;
    EDirections currentDirection, prevDirection;
    LevelManager levelManager;
    Timer timer;
    Handler handler;
    Vibrator vibrator;
    MediaPlayer mediaPlayer;
    SharedPreferences sharedPreferences;
    int[] foodAssets = new int[] {R.drawable.rabbit, R.drawable.apple};
    List<HashMap<String, Integer>> snakeAssets = new ArrayList<HashMap<String, Integer>>();
    boolean gamePaused;
    HighScores highScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView settings = findViewById(R.id.settings);
        highScores = HighScores.getInstance(this);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        ImageView highScores = findViewById(R.id.high_scores_image);
        highScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HighScoreActivity.class));
            }
        });

        board = new ImageView[BOARD_SIZE][BOARD_SIZE];
        gameLayout = findViewById(R.id.game_layout);
        scoreText = findViewById(R.id.score_text);
        startBtn = findViewById(R.id.start_game_btn);
        pauseBtn = findViewById(R.id.pause_game_btn);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        createBoard();
        handler = new Handler();

        pauseBtn.setEnabled(false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        initializeSnakeAssets();

        btnUp = findViewById(R.id.button_up);
        btnDown = findViewById(R.id.button_down);
        btnLeft = findViewById(R.id.button_left);
        btnRight = findViewById(R.id.button_right);
        btnUp.setEnabled(false);
        btnDown.setEnabled(false);
        btnLeft.setEnabled(false);
        btnRight.setEnabled(false);
        // On each control click add the move to the direction queue if the current move isn't the same
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
                if (directionQueue.peek() != EDirections.UP) {
                    directionQueue.add(EDirections.UP);
                }
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
                if (directionQueue.peek() != EDirections.DOWN) {
                    directionQueue.add(EDirections.DOWN);
                }
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
                if (directionQueue.peek() != EDirections.LEFT) {
                    directionQueue.add(EDirections.LEFT);
                }
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
                if (directionQueue.peek() != EDirections.RIGHT) {
                    directionQueue.add(EDirections.RIGHT);
                }
            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseBtn.setEnabled(true);
                gamePaused = false;
                btnUp.setEnabled(true);
                btnDown.setEnabled(true);
                btnLeft.setEnabled(true);
                btnRight.setEnabled(true);
                startGame();
            }
        });
        // Pause button switches from pause to resume
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gamePaused) {
                    timer.cancel();
                    timer.purge();
                    pauseBtn.setText("Resume");
                    gamePaused = true;
                    btnUp.setEnabled(false);
                    btnDown.setEnabled(false);
                    btnLeft.setEnabled(false);
                    btnRight.setEnabled(false);
                }
                else {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            performMove();
                        }
                    }, 0, gameSpeed);
                    pauseBtn.setText("Pause");
                    gamePaused = false;
                    btnUp.setEnabled(true);
                    btnDown.setEnabled(true);
                    btnLeft.setEnabled(true);
                    btnRight.setEnabled(true);
                }
            }
        });
    }

    // Creating the board grid
    private void createBoard() {
        int id = 1;
        LayoutParams params;
        // Each square in the board is 32x32 and represents a position in the board grid
        params = new LayoutParams(32,32);
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new ImageView(this);
                board[i][j].setImageResource(R.drawable.grass);
                board[i][j].setId(id);
                params = new LayoutParams(32,32);
                if (j == 0) {
                    if (i != 0) {
                        params.addRule(RelativeLayout.BELOW, board[i - 1][j].getId());
                    }
                }
                else {
                    params.addRule(RelativeLayout.END_OF, board[i][j - 1].getId());
                    params.addRule(RelativeLayout.ALIGN_BOTTOM, board[i][j - 1].getId());
                }
                gameLayout.addView(board[i][j], params);
                id++;
            }
        }
    }

    // Starts the level manager and a scheduled command to move the snake at specific intervals according to the game speed
    private void startGame() {
        directionQueue = new ArrayDeque<>();
        levelManager = new LevelManager(BOARD_SIZE);
        chosenFoodAsset = Integer.parseInt(sharedPreferences.getString("preference_snake_food", "0"));
        chosenSnakeAsset = Integer.parseInt(sharedPreferences.getString("preference_snake_design", "0"));
        gameSpeed = Integer.parseInt(sharedPreferences.getString("preference_game_speed","300"));
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        // Starting direction is right
        currentDirection = EDirections.RIGHT;
        prevDirection = EDirections.RIGHT;
        clearBoard();
        // Put snake at the board center
        board[BOARD_SIZE / 2][BOARD_SIZE / 2].setImageResource(snakeAssets.get(chosenSnakeAsset).get("HeadRight"));
        board[BOARD_SIZE / 2][(BOARD_SIZE / 2)  - 1].setImageResource(snakeAssets.get(chosenSnakeAsset).get("BodyHorizontal"));
        board[BOARD_SIZE / 2][(BOARD_SIZE / 2) - 2].setImageResource(snakeAssets.get(chosenSnakeAsset).get("TailRight"));
        board[levelManager.getFood().getX()][levelManager.getFood().getY()].setImageResource(foodAssets[chosenFoodAsset]);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                performMove();
            }
        }, 0, gameSpeed);
    }

    // Called from the timer object each turn
    private void performMove() {
        // Check for new directions
        if (directionQueue.peek() != null) {
            // Remove reverse movement or same movement
            if (checkMovingInReverse() || currentDirection == directionQueue.peek()) {
                directionQueue.remove();
            }
            else {
                currentDirection = directionQueue.remove();
            }
        }

        // Get new positions from level manager
        final Point[] moveResults = levelManager.move(currentDirection);

        // If the results are null than game over
        if (moveResults != null) {
            final int headDirection = getHeadDirectionResource();
            final int bodyDirection = getBodyDirectionResource();
            final int tailDirection;
            final boolean isGrowing = levelManager.getIsGrowing();
            // Get tail direction only if snake isn't growing
            if (!isGrowing) {
                tailDirection = getTailDirectionResource();
            }
            else {
                tailDirection = 0;
            }
            // Changing snake position and direction
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Head
                    board[moveResults[0].getX()][moveResults[0].getY()].setImageResource(headDirection);
                    // Before Head
                    board[moveResults[1].getX()][moveResults[1].getY()].setImageResource(bodyDirection);
                    if (!isGrowing) {
                        // Tail
                        board[moveResults[2].getX()][moveResults[2].getY()].setImageResource(tailDirection);
                        // Remove end
                        board[moveResults[3].getX()][moveResults[3].getY()].setImageResource(R.drawable.grass);
                    }
                    if (levelManager.checkAteFood()) {
                        scoreText.setText("Score : " + levelManager.getScore());
                        playSound(R.raw.eat);
                        // Place new food
                        board[levelManager.getFood().getX()][levelManager.getFood().getY()].setImageResource(foodAssets[chosenFoodAsset]);
                    }
                }
            });

        }
        else {
            timer.cancel();
            playSound(R.raw.lose);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    pauseBtn.setEnabled(false);
                    if (levelManager.getScore() != 0 && highScores.checkHighScore(levelManager.getScore())) {
                        AddHighScoreDialogFragment addHighScoreDialogFragment = new AddHighScoreDialogFragment();
                        addHighScoreDialogFragment.show(getSupportFragmentManager(), ADD_HIGH_SCORE_TAG);
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Score " + levelManager.getScore() + "\nGame Over", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private int getHeadDirectionResource() {
        int res = snakeAssets.get(chosenSnakeAsset).get("HeadRight");

        switch (currentDirection) {
            case LEFT:
                res = snakeAssets.get(chosenSnakeAsset).get("HeadLeft");
                break;
            case UP:
                res = snakeAssets.get(chosenSnakeAsset).get("HeadUp");
                break;
            case DOWN:
                res = snakeAssets.get(chosenSnakeAsset).get("HeadDown");
                break;
        }

        return res;
    }

    private int getBodyDirectionResource() {
        int res;
        if (currentDirection == prevDirection) {
            if (currentDirection == EDirections.RIGHT || currentDirection == EDirections.LEFT) {
                res = snakeAssets.get(chosenSnakeAsset).get("BodyHorizontal");
            }
            else {
                res = snakeAssets.get(chosenSnakeAsset).get("BodyVertical");
            }
        }
        else {
            if ((currentDirection == EDirections.DOWN && prevDirection == EDirections.RIGHT) ||
            (currentDirection == EDirections.LEFT && prevDirection == EDirections.UP)) {
                res = snakeAssets.get(chosenSnakeAsset).get("TurnDownLeft");
            }
            else if ((currentDirection == EDirections.DOWN && prevDirection == EDirections.LEFT) ||
                    (currentDirection == EDirections.RIGHT && prevDirection == EDirections.UP)) {
                res = snakeAssets.get(chosenSnakeAsset).get("TurnDownRight");
            }
            else if ((currentDirection == EDirections.UP && prevDirection == EDirections.RIGHT) ||
                        (currentDirection == EDirections.LEFT && prevDirection == EDirections.DOWN)) {
                res = snakeAssets.get(chosenSnakeAsset).get("TurnUpLeft");
            }
            else {
                res = snakeAssets.get(chosenSnakeAsset).get("TurnUpRight");
            }
        }
        prevDirection = currentDirection;
        return res;
    }

    private int getTailDirectionResource() {
        int res = snakeAssets.get(chosenSnakeAsset).get("TailRight");
        switch (levelManager.getTailDirection()) {
            case UP:
                res = snakeAssets.get(chosenSnakeAsset).get("TailUp");
                break;
            case DOWN:
                res = snakeAssets.get(chosenSnakeAsset).get("TailDown");
                break;
            case LEFT:
                res = snakeAssets.get(chosenSnakeAsset).get("TailLeft");
                break;
        }

        return res;
    }

    private void playSound(int soundId) {
        if (sharedPreferences.getBoolean("preference_enable_sound", true)) {
            mediaPlayer = MediaPlayer.create(this, soundId);
            mediaPlayer.start();
        }
    }

    private void clearBoard() {
        scoreText.setText("Score : 0");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j].setImageResource(R.drawable.grass);
            }
        }
    }

    private void vibrate() {
        if (sharedPreferences.getBoolean("preference_enable_vibrations", true)) {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrator.vibrate(100);
            }
        }
    }

    private void initializeSnakeAssets() {
        HashMap<String, Integer> snake8Bit = new HashMap<>();
        snake8Bit.put("HeadUp", R.drawable.head_up_8_bit);
        snake8Bit.put("HeadDown", R.drawable.head_down_8_bit);
        snake8Bit.put("HeadLeft", R.drawable.head_left_8_bit);
        snake8Bit.put("HeadRight", R.drawable.head_right_8_bit);
        snake8Bit.put("TailUp", R.drawable.tail_up_8_bit);
        snake8Bit.put("TailDown", R.drawable.tail_down_8_bit);
        snake8Bit.put("TailLeft", R.drawable.tail_left_8_bit);
        snake8Bit.put("TailRight", R.drawable.tail_right_8_bit);
        snake8Bit.put("BodyHorizontal", R.drawable.body_horizontal_8_bit);
        snake8Bit.put("BodyVertical", R.drawable.body_vertical_8_bit);
        snake8Bit.put("TurnUpRight", R.drawable.turn_up_right_8_bit);
        snake8Bit.put("TurnUpLeft", R.drawable.turn_up_left_8_bit);
        snake8Bit.put("TurnDownRight", R.drawable.turn_down_right_8_bit);
        snake8Bit.put("TurnDownLeft", R.drawable.turn_down_left_8_bit);
        snakeAssets.add(snake8Bit);
        HashMap<String, Integer> snakeCartoon = new HashMap<>();
        snakeCartoon.put("HeadUp", R.drawable.head_up_cartoon);
        snakeCartoon.put("HeadDown", R.drawable.head_down_cartoon);
        snakeCartoon.put("HeadLeft", R.drawable.head_left_cartoon);
        snakeCartoon.put("HeadRight", R.drawable.head_right_cartoon);
        snakeCartoon.put("TailUp", R.drawable.tail_up_cartoon);
        snakeCartoon.put("TailDown", R.drawable.tail_down_cartoon);
        snakeCartoon.put("TailLeft", R.drawable.tail_left_cartoon);
        snakeCartoon.put("TailRight", R.drawable.tail_right_cartoon);
        snakeCartoon.put("BodyHorizontal", R.drawable.body_horizontal_cartoon);
        snakeCartoon.put("BodyVertical", R.drawable.body_vertical_cartoon);
        snakeCartoon.put("TurnUpRight", R.drawable.turn_up_right_cartoon);
        snakeCartoon.put("TurnUpLeft", R.drawable.turn_up_left_cartoon);
        snakeCartoon.put("TurnDownRight", R.drawable.turn_down_right_cartoon);
        snakeCartoon.put("TurnDownLeft", R.drawable.turn_down_left_cartoon);
        snakeAssets.add(snakeCartoon);
    }

    private boolean checkMovingInReverse() {
        return ((currentDirection == EDirections.DOWN) && (directionQueue.peek() == EDirections.UP)) ||
               ((currentDirection == EDirections.UP) && (directionQueue.peek() == EDirections.DOWN)) ||
               ((currentDirection == EDirections.LEFT) && (directionQueue.peek() == EDirections.RIGHT)) ||
               ((currentDirection == EDirections.RIGHT) && (directionQueue.peek() == EDirections.LEFT));
    }

    @Override
    protected void onPause() {
        if (timer != null && !gamePaused) {
            pauseBtn.performClick();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        super.onDestroy();
    }

    @Override
    public void onReturn(String name) {
        if (name != null) {
            highScores.addHighScore(new UserScore(name, levelManager.getScore()));
            startActivity(new Intent(MainActivity.this, HighScoreActivity.class));
        }
    }
}