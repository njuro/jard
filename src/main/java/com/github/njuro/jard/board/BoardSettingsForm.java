package com.github.njuro.jard.board;

import static com.github.njuro.jard.common.Constants.*;

import com.github.njuro.jard.attachment.AttachmentCategory;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Form for setting/updating {@link BoardSettings}. Always needs to be part of {@link BoardForm}.
 */
@Data
@Builder
@SuppressWarnings("JavadocReference")
public class BoardSettingsForm {

  /** @see BoardSettings#attachmentCategories */
  @Builder.Default private Set<AttachmentCategory> attachmentCategories = new HashSet<>();

  /** @see BoardSettings#threadLimit */
  @Positive(message = "{validation.board.threadlimit.positive}")
  @Max(value = MAX_THREAD_LIMIT, message = "{validation.board.threadlimit.max}")
  private int threadLimit;

  /** @see BoardSettings#bumpLimit */
  @Positive(message = "{validation.board.bumplimit.positive}")
  @Max(value = MAX_BUMP_LIMIT, message = "{validation.board.bumplimit.max}")
  private int bumpLimit;

  /** @see BoardSettings#nsfw */
  private boolean nsfw;

  /** @see BoardSettings#defaultPosterName */
  @Size(max = MAX_NAME_LENGTH, message = "{validation.board.default.poster.name.length}")
  private String defaultPosterName;

  /** @see BoardSettings#forceDefaultPosterName */
  private boolean forceDefaultPosterName;

  /** @see BoardSettings#countryFlags */
  private boolean countryFlags;

  /** @return {@link BoardSettings} created from values of this form */
  public BoardSettings toBoardSettings() {
    return BoardSettings.builder()
        .attachmentCategories(attachmentCategories)
        .threadLimit(threadLimit)
        .bumpLimit(bumpLimit)
        .nsfw(nsfw)
        .defaultPosterName(defaultPosterName)
        .forceDefaultPosterName(forceDefaultPosterName)
        .countryFlags(countryFlags)
        .build();
  }
}
