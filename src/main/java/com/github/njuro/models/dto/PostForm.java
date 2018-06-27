package com.github.njuro.models.dto;

import org.springframework.web.multipart.MultipartFile;

/**
 * Data transfer object for "reply to thread" form
 */
public class PostForm {

    private String name;
    private String tripcode;
    private String body;

    private MultipartFile file;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTripcode() {
        return tripcode;
    }

    public void setTripcode(String tripcode) {
        this.tripcode = tripcode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
