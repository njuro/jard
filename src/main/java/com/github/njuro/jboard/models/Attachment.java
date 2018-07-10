package com.github.njuro.jboard.models;

import com.github.njuro.jboard.helpers.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.nio.file.Paths;

/**
 * Entity representing an attachment to post
 *
 * @author njuro
 */
@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @EqualsAndHashCode.Include
    private String path;

    @NotNull
    private String originalFilename;

    @Column(unique = true)
    @EqualsAndHashCode.Include
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
    @ToString.Exclude
    private String url;

    @Transient
    @ToString.Exclude
    private File file;

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

}
