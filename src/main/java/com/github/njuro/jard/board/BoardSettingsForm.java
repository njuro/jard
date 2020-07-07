package com.github.njuro.jard.board;

import static com.github.njuro.jard.common.Constants.MAX_BUMP_LIMIT;
import static com.github.njuro.jard.common.Constants.MAX_NAME_LENGTH;
import static com.github.njuro.jard.common.Constants.MAX_THREAD_LIMIT;

import com.github.njuro.jard.attachment.AttachmentCategory;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoardSettingsForm {

  @Builder.Default private Set<AttachmentCategory> attachmentCategories = new HashSet<>();

  @Positive(message = "{validation.board.threadlimit.positive}")
  @Max(value = MAX_THREAD_LIMIT, message = "{validation.board.threadlimit.max}")
  private int threadLimit;

  @Positive(message = "{validation.board.bumplimit.positive}")
  @Max(value = MAX_BUMP_LIMIT, message = "{validation.board.bumplimit.max}")
  private int bumpLimit;

  private boolean nsfw;

  @Size(max = MAX_NAME_LENGTH, message = "{validation.board.default.poster.name.length}")
  private String defaultPosterName;

  private boolean forceDefaultPosterName;

  public BoardSettings toBoardSettings() {
    return BoardSettings.builder()
        .attachmentCategories(attachmentCategories)
        .threadLimit(threadLimit)
        .bumpLimit(bumpLimit)
        .nsfw(nsfw)
        .defaultPosterName(defaultPosterName)
        .forceDefaultPosterName(forceDefaultPosterName)
        .build();
  }
}
