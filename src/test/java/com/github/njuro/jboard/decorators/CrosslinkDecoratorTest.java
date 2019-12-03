package com.github.njuro.jboard.decorators;

import static com.github.njuro.jboard.helpers.Constants.CROSSLINK_CLASS_INVALID;
import static com.github.njuro.jboard.helpers.Constants.CROSSLINK_CLASS_VALID;
import static com.github.njuro.jboard.helpers.Constants.CROSSLINK_DIFF_THREAD;
import static com.github.njuro.jboard.helpers.Constants.CROSSLINK_OP;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.njuro.jboard.database.UseMockDatabase;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.services.ThreadService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@UseMockDatabase
@Slf4j
class CrosslinkDecoratorTest extends DecoratorTest {

  @Autowired private CrosslinkDecorator decorator;

  @Autowired private ThreadService threadService;

  private Post postR;
  private Post postF;

  @Override
  protected Decorator initDecorator() {
    return this.decorator;
  }

  @BeforeEach
  void initPosts() {
    this.postR = Post.builder().thread(this.threadService.resolveThread("r", 1L)).build();
    this.postF = Post.builder().thread(this.threadService.resolveThread("fit", 1L)).build();
  }

  @Test
  void testValidCrossThreadlink() {
    decoratePost(this.postR, ">>1");
    assertThat(this.postR.getBody()).containsSubsequence("/boards/r/1", CROSSLINK_CLASS_VALID);
  }

  @Test
  void testInvalidCrossThreadLink() {
    decoratePost(this.postR, ">>42");
    assertThat(this.postR.getBody()).contains(CROSSLINK_CLASS_INVALID);
  }

  @Test
  void testMultipleCrossThreadLinks() {
    decoratePost(this.postR, "To different thread >>3 more text\n >>1 and to OP");
    assertThat(this.postR.getBody())
        .containsSubsequence(
            "/boards/r/3#3",
            CROSSLINK_CLASS_VALID,
            CROSSLINK_DIFF_THREAD,
            "/boards/r/1#1",
            CROSSLINK_CLASS_VALID,
            CROSSLINK_OP);
  }

  @Test
  void testValidCrossBoardLink() {
    decoratePost(this.postR, "Some text  >>>/fit/1");
    decoratePost(this.postF, ">>>/r/4 some text");
    assertThat(this.postR.getBody()).contains(CROSSLINK_CLASS_VALID, "/boards/fit/1");
    assertThat(this.postF.getBody()).contains(CROSSLINK_CLASS_VALID, "/boards/r/3#4");
  }

  @Test
  void testInvalidCrossBoardLink() {
    decoratePost(this.postF, "This points to >>>/r/42");
    decoratePost(this.postR, "And this to >>>/a/1");
    assertThat(this.postR.getBody()).contains(CROSSLINK_CLASS_INVALID);
    assertThat(this.postF.getBody()).contains(CROSSLINK_CLASS_INVALID);
  }

  @Test
  void testPureCrossBoardLink() {
    decoratePost(this.postF, "This is pure valid link to >>>/fit/  ");
    decoratePost(this.postR, "And this is invalid link to >>>/q/");
    assertThat(this.postF.getBody()).contains(CROSSLINK_CLASS_VALID, "/boards/fit");
    assertThat(this.postR.getBody()).contains(CROSSLINK_CLASS_INVALID);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ">>",
        " >>>",
        ">> 1",
        ">>/r/1",
        ">>> /fit/1",
        ">>abc",
        ">>>abc",
        ">>>//123",
        ">>/fit/"
      })
  void testInvalidCrosslinkPattern(final String input) {
    decoratePost(this.postR, input);
    assertThat(this.postR.getBody()).doesNotContain(CROSSLINK_CLASS_VALID, CROSSLINK_CLASS_INVALID);
  }
}
