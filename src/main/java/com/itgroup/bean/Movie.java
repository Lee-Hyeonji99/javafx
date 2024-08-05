package com.itgroup.bean;

public class Movie {

    private int id;             // 영화 아이디
    private String name;        // 영화 제목
    private String nation;      // 국가
    private String viewingDate; // 관람일
    private String comments;    // 관람평
    private int rating;         // 평점

    public Movie() {
    }

    public Movie(int id, String name, String nation, String viewingDate, String comments, int rating) {
        this.id = id;
        this.name = name;
        this.nation = nation;
        this.viewingDate = viewingDate;
        this.comments = comments;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String genre) {
        this.nation = genre;
    }

    public String getViewingDate() {
        return viewingDate;
    }

    public void setViewingDate(String viewingDate) {
        this.viewingDate = viewingDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
