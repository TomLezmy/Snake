package com.tomlezmy.snake.model;

import androidx.annotation.Nullable;

import java.util.Random;

// The logical manager of the game
public class LevelManager {
    private int boardSize;
    private Snake snake;
    private int[][] board;
    private int score;
    private Point food;
    private Boolean isGrowing;

    public LevelManager(int boardSize) {
        resetLevel(boardSize);
    }

    public void resetLevel(int boardSize) {
        this.boardSize = boardSize;
        snake = new Snake(new Point(boardSize / 2,boardSize / 2));
        score = 0;
        isGrowing = false;
        board = new int[boardSize][boardSize];
        placeFood();
    }

    public Point getFood() {
        return food;
    }

    public int getScore() {
        return score;
    }

    public Boolean getIsGrowing() {
        if (isGrowing == true) {
            isGrowing = false;
            return true;
        }
        return false;
    }

    @Nullable
    public Point[] move(EDirections direction) {
        Point oldHead = snake.getSnakeLocation();
        Point oldTail = snake.getTail();
        snake.move(direction);
        if (!isGrowing) {
            snake.removeTail();
        }

        if (snake.getSnakeLocation().getX() >= boardSize || snake.getSnakeLocation().getX() < 0 ||
                snake.getSnakeLocation().getY() >= boardSize || snake.getSnakeLocation().getY() < 0 || snake.checkHitSelf()) {
            // Snake hit something, game over
            // Return no new positions
            return null;
        }
        Point newHead = snake.getSnakeLocation();
        Point newTail = snake.getTail();
        return new Point[] {newHead, oldHead, newTail, oldTail};
    }

    public EDirections getTailDirection() {
        EDirections direction;
        Point tail = snake.getTail();
        Point afterTail = snake.getPartBeforeTail();
        if (tail.getX() != afterTail.getX()) {
            if (tail.getX() < afterTail.getX()) {
                direction = EDirections.DOWN;
            }
            else {
                direction = EDirections.UP;
            }
        }
        else {
            if (tail.getY() < afterTail.getY()) {
                direction = EDirections.RIGHT;
            }
            else {
                direction = EDirections.LEFT;
            }
        }
        return direction;
    }

    public Point placeFood() {
        Random rand = new Random();
        food = new Point(rand.nextInt(boardSize), rand.nextInt(boardSize));
        while (!snake.checkFreeSpace(food)) {
            food = new Point(rand.nextInt(boardSize), rand.nextInt(boardSize));
        }
        return food;
    }

    public Boolean checkAteFood() {
        if (snake.getSnakeLocation().equals(food)) {
            score++;
            isGrowing = true;
            placeFood();
            return true;
        }
        return false;
    }
}
