package com.github.njuro.models.dto;

/**
 * Data transfer object for "new thread" form
 *
 * @author njuro
 */
public class ThreadForm {


    private String subject;
    private boolean stickied;
    private boolean locked;

    private PostForm post;


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isStickied() {
        return stickied;
    }

    public void setStickied(boolean stickied) {
        this.stickied = stickied;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public PostForm getPost() {
        return post;
    }

    public void setPost(PostForm post) {
        this.post = post;
    }
}
