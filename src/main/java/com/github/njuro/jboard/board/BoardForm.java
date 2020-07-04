package com.github.njuro.jboard.board;

import static com.github.njuro.jboard.common.Constants.MAX_BOARD_LABEL_LENGTH;
import static com.github.njuro.jboard.common.Constants.MAX_BOARD_NAME_LENGTH;
import static com.github.njuro.jboard.common.Constants.MAX_BUMP_LIMIT;
import static com.github.njuro.jboard.common.Constants.MAX_THREAD_LIMIT;

import com.github.njuro.jboard.attachment.AttachmentType;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoardForm {

  @NotBlank(message = "{validation.board.label.blank}")
  @Size(max = MAX_BOARD_LABEL_LENGTH, message = "{validation.board.label.length}")
  private String label;

  @NotBlank(message = "{validation.board.name.blank}")
  @Size(max = MAX_BOARD_NAME_LENGTH, message = "{validation.board.name.length}")
  private String name;

  @Builder.Default private Set<AttachmentType> attachmentTypes = new HashSet<>();

  private boolean nsfw;

  @Positive(message = "{validation.board.threadlimit.positive}")
  @Max(value = MAX_THREAD_LIMIT, message = "{validation.board.threadlimit.max}")
  private int threadLimit;

  @Positive(message = "{validation.board.bumplimit.positive}")
  @Max(value = MAX_BUMP_LIMIT, message = "{validation.board.bumplimit.max}")
  private int bumpLimit;

  public Board toBoard() {
    return Board.builder()
        .label(label)
        .name(name)
        .attachmentType(attachmentType)
        .nsfw(nsfw)
        .threadLimit(threadLimit)
        .bumpLimit(bumpLimit)
        .build();
  }
}
