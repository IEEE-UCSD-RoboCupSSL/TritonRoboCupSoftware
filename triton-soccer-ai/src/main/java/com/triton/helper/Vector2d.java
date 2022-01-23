package com.triton.helper;

public class Vector2d {
    public final float x;
    public final float y;

    public Vector2d(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d(Vector2d vector) {
        this.x = vector.x;
        this.y = vector.y;
    }

    public Vector2d(float angle) {
        this.x = (float) Math.cos(angle);
        this.y = (float) Math.sin(angle);
    }

    public float mag() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float angle() {
        return (float) Math.atan2(y, x);
    }

    public Vector2d unit() {
        float mag = mag();
        return this.scale(1f /  mag);
    }

    public Vector2d add(Vector2d vector) {
        return new Vector2d(x + vector.x, y + vector.y);
    }

    public Vector2d sub(Vector2d vector) {
        return new Vector2d(x - vector.x, y - vector.y);
    }

    public Vector2d scale(float scale) {
        return new Vector2d(x * scale, y * scale);
    }

    public float dot(Vector2d vector) {
        return x * vector.x + y * vector.y;
    }

    public float scalarProj(Vector2d vector) {
        return dot(vector) / vector.mag();
    }

    public Vector2d proj(Vector2d vector) {
        return vector.scale(dot(vector) / vector.dot(vector));
    }

    public float dist(Vector2d vector) {
        return sub(vector).mag();
    }

    @Override
    public String toString() {
        return "Vector2d{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
