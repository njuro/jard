package com.github.njuro.models.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Data transfer object for "reply to thread" form
 */

@Data
public class PostForm {

    private String name;
    private String password;
    private String body;

    private MultipartFile attachment;

}
