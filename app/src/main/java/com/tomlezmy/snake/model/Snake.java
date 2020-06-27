package com.tomlezmy.snake.model;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    private int size;
    private Point head;
    private List<Point> body;

    public Snake(Point head) {
        this.size = 3;
        this.head = new Point(head);
        body = new ArrayList<>();
        body.add(new Point(head));
        body.add(new Point(head.getX(), head.getY() - 1));
        body.add(new Point(head.getX(), head.getY() - 2));
    }

    public Point getSnakeLocation() {
        return new Point(body.get(0));
    }

    public Point getTail() {
        return new Point (body.get(body.size() - 1));
    }

    public Point getPartBeforeTail() {
        return new Point (body.get(body.size() - 2));
    }

    public void move(EDirections direction) {
        switch (direction) {
            case UP:
                head.setX(head.getX() - 1);
                break;
            case DOWN:
                head.setX(head.getX() + 1);
                break;
            case LEFT:
                head.setY(head.getY() - 1);
                break;
            case RIGHT:
                head.setY(head.getY() + 1);
                break;
        }
        body.add(0, new Point(head));
    }

    public void removeTail() {
        body.remove(body.size() - 1);
    }

    public Boolean checkHitSelf() {
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }

    public Boolean checkFreeSpace(Point point) {
        for (Point snakePart : body) {
            if (snakePart.equals(point)) {
                return false;
            }
        }

        return true;
    }
}
