package com.github.njuro.models;

import helpers.Constants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.nio.file.Paths;
import java.util.Objects;

@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    private String path;

    @NotNull
    private String originalFilename;

    @Column(unique = true)
    private String filename;

    @Transient
    private String url;

    public Attachment() {
    }

    public Attachment(String path, @NotNull String originalFilename, String filename) {
        this.path = path;
        this.originalFilename = originalFilename;
        this.filename = filename;

        initUrl();
    }

    @PostLoad
    public void initUrl() {
        this.url = Constants.USER_CONTENT_URL + Paths.get(path, filename).toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attachment that = (Attachment) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", originalFilename='" + originalFilename + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }
}
