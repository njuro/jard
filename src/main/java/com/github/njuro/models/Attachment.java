package com.github.njuro.models;

import com.github.njuro.helpers.Constants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.File;
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

    @Basic
    private int width;

    @Basic
    private int height;

    @Basic
    private int thumbWidth;

    @Basic
    private int thumbHeight;

    @Transient
    private String url;

    @Transient
    private File file;

    public Attachment() {
    }

    public Attachment(String path, @NotNull String originalFilename, String filename) {
        this.path = path;
        this.originalFilename = originalFilename;
        this.filename = filename;

        initContentPaths();
    }

    @PostLoad
    public void initContentPaths() {
        this.url = Constants.USER_CONTENT_URL + Paths.get(path, filename).toString();
        this.file = Constants.USER_CONTENT_PATH.resolve(Paths.get(path, filename)).toFile();
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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
