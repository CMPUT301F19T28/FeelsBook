package com.cmput.feelsbook;

public class Shape {
    private int x;
    private int y;
    private String color;

    public Shape(){
        this.x = 0;
        this.y = 0;
        this.color = "This_is_a_color";
    }

    public Shape(int x, int y, String color){
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
