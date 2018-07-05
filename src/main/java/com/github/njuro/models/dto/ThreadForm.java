package com.github.njuro.models.dto;

import lombok.Data;

/**
 * Data transfer object for "new thread" form
 *
 * @author njuro
 */
@Data
public class ThreadForm {

    private String subject;
    private boolean stickied;
    private boolean locked;

    private PostForm post;
}
