package com.github.njuro.jard.board.dto;

import static com.github.njuro.jard.common.InputConstraints.*;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.board.BoardSettings;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.*;

/** DTO for {@link BoardSettings}. */
@SuppressWarnings("JavadocReference")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class BoardSettingsDto implements Serializable {

  private static final long serialVersionUID = 6562925928863515257L;

  /** {@link BoardSettings#attachmentCategories} */
  @Builder.Default
  @JsonSerialize(contentUsing = AttachmentCategory.AttachmentCategorySerializer.class)
  private Set<AttachmentCategory> attachmentCategories = new HashSet<>();

  /** {@link BoardSettings#attachmentCategories} */
  @Positive(message = "{validation.board.threadlimit.positive}")
  @Max(value = MAX_THREAD_LIMIT, message = "{validation.board.threadlimit.max}")
  private int threadLimit;

  /** {@link BoardSettings#bumpLimit} */
  @Positive(message = "{validation.board.bumplimit.positive}")
  @Max(value = MAX_BUMP_LIMIT, message = "{validation.board.bumplimit.max}")
  private int bumpLimit;

  /** {@link BoardSettings#nsfw} */
  private boolean nsfw;

  /** {@link BoardSettings#defaultPosterName} */
  @Size(max = MAX_NAME_LENGTH, message = "{validation.board.default.poster.name.length}")
  private String defaultPosterName;

  /** {@link BoardSettings#forceDefaultPosterName} */
  private boolean forceDefaultPosterName;

  /** {@link BoardSettings#countryFlags} */
  private boolean countryFlags;

  /** {@link BoardSettings#posterThreadIds} */
  private boolean posterThreadIds;

  /** {@link BoardSettings#captchaEnabled */
  private boolean captchaEnabled;
}
