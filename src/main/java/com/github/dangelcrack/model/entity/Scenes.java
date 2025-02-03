package com.github.dangelcrack.model.entity;

public enum Scenes {
    LOGIN("/com/github/dangelcrack/view/login.fxml"),
    REGISTER("/com/github/dangelcrack/view/register.fxml"),
    ROOT("/com/github/dangelcrack/view/layout.fxml"),
    USERCONFIG("/com/github/dangelcrack/view/userconfig.fxml"),
    HABITOS("/com/github/dangelcrack/view/habitos.fxml"),
    ADDHABITO("/com/github/dangelcrack/view/addhabitos.fxml"),
    EDITHABITO("/com/github/dangelcrack/view/edithabitos.fxml"),
    TRACKS("/com/github/dangelcrack/view/tracks.fxml"),
    ADDTRACK("/com/github/dangelcrack/view/addtrack.fxml"),
    DELETETRACK("/com/github/dangelcrack/view/deletetrack.fxml"),
    EDITTRACK("/com/github/dangelcrack/view/edittrack.fxml"),
    IMPACTS("/com/github/dangelcrack/view/impacts.fxml"),
    RANKING("/com/github/dangelcrack/view/ranking.fxml"),;
    private String url;
    Scenes(String url) {this.url = url;}
    public String getURL() {return url;}
}
