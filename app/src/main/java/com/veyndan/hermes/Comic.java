package com.veyndan.hermes;

public class Comic {

    private int num;
    private String alt;
    private String img;
    private String title;

    public int getNum() {
        return num;
    }

    public String getAlt() {
        return alt;
    }

    public String getImg() {
        return img;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Comic{" +
                "num=" + num +
                ", alt='" + alt + '\'' +
                ", img='" + img + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}