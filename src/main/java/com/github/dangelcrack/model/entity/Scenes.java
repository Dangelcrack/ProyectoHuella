package com.github.dangelcrack.model.entity;

public enum Scenes {
    LOGIN("view/login.fxml");
    private String url;
    Scenes(String url) {this.url = url;}
    public String getURL() {return url;}
}
