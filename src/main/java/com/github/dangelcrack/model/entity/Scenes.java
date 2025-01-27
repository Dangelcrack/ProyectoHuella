package com.github.dangelcrack.model.entity;

public enum Scenes {
    LOGIN("/com/github/dangelcrack/view/login.fxml"),
    REGISTER("/com/github/dangelcrack/view/register.fxml"),
    ROOT("/com/github/dangelcrack/view/layout.fxml"),
    USERCONFIG("/com/github/dangelcrack/view/userconfig.fxml"),
    ACTIVITIES("/com/github/dangelcrack/view/activities.fxml"),
    TRACKS("/com/github/dangelcrack/view/tracks.fxml");
    private String url;
    Scenes(String url) {this.url = url;}
    public String getURL() {return url;}
}
