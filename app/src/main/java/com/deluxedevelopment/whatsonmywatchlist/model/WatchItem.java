package com.deluxedevelopment.whatsonmywatchlist.model;

public class WatchItem {
    private long id;
    private String title;
    private String type;
    private WatchStatus status;
    private String notes;
    private Boolean liked;

    public enum WatchStatus {
        NOT_WATCHED,
        WATCHING,
        WATCHED
    }

    public WatchItem(String title, String type) {
        this.title = title;
        this.type = type;
        this.status = WatchStatus.NOT_WATCHED;
        this.liked = null;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public WatchStatus getStatus() { return status; }
    public void setStatus(WatchStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getLiked() { return liked; }
    public void setLiked(Boolean liked) { this.liked = liked; }
}