package com.github.njuro.jard.post.decorators;

import static com.github.njuro.jard.common.Constants.CROSSLINK_CLASS_INVALID;
import static com.github.njuro.jard.common.Constants.CROSSLINK_CLASS_VALID;
import static com.github.njuro.jard.common.Constants.CROSSLINK_DIFF_THREAD;
import static com.github.njuro.jard.common.Constants.CROSSLINK_OP;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.njuro.jard.database.UseMockDatabase;
import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.thread.ThreadService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Service;

@DataJpaTest(
    includeFilters = {
      @Filter(Service.class),
      @Filter(type = FilterType.ASSIGNABLE_TYPE, value = CrosslinkDecorator.class)
    })
@UseMockDatabase
@Slf4j
class CrosslinkDecoratorTest extends PostDecoratorTest {

  @Autowired private CrosslinkDecorator decorator;

  @Autowired private ThreadService threadService;

  private Post postR;
  private Post postF;

  @Override
  protected PostDecorator initDecorator() {
    return decorator;
  }

  @BeforeEach
  void initPosts() {
    postR = Post.builder().thread(threadService.resolveThread("r", 1L)).build();
    postF = Post.builder().thread(threadService.resolveThread("fit", 1L)).build();
  }

  @Test
  void testValidCrossThreadlink() {
    decoratePost(postR, ">>1");
    assertThat(postR.getBody()).containsSubsequence("/boards/r/thread/1", CROSSLINK_CLASS_VALID);
  }

  @Test
  void testInvalidCrossThreadLink() {
    decoratePost(postR, ">>42");
    assertThat(postR.getBody()).contains(CROSSLINK_CLASS_INVALID);
  }

  @Test
  void testMultipleCrossThreadLinks() {
    decoratePost(postR, "To different thread >>3 more text\n >>1 and to OP");
    assertThat(postR.getBody())
        .containsSubsequence(
            "/boards/r/thread/3#3",
            CROSSLINK_CLASS_VALID,
            CROSSLINK_DIFF_THREAD,
            "/boards/r/thread/1#1",
            CROSSLINK_CLASS_VALID,
            CROSSLINK_OP);
  }

  @Test
  void testValidCrossBoardLink() {
    decoratePost(postR, "Some text  >>>/fit/1");
    decoratePost(postF, ">>>/r/4 some text");
    assertThat(postR.getBody()).contains(CROSSLINK_CLASS_VALID, "/boards/fit/thread/1");
    assertThat(postF.getBody()).contains(CROSSLINK_CLASS_VALID, "/boards/r/thread/3#4");
  }

  @Test
  void testInvalidCrossBoardLink() {
    decoratePost(postF, "This points to >>>/r/42");
    decoratePost(postR, "And this to >>>/a/1");
    assertThat(postR.getBody()).contains(CROSSLINK_CLASS_INVALID);
    assertThat(postF.getBody()).contains(CROSSLINK_CLASS_INVALID);
  }

  @Test
  void testPureCrossBoardLink() {
    decoratePost(postF, "This is pure valid link to >>>/fit/  ");
    decoratePost(postR, "And this is invalid link to >>>/q/");
    assertThat(postF.getBody()).contains(CROSSLINK_CLASS_VALID, "/boards/fit");
    assertThat(postR.getBody()).contains(CROSSLINK_CLASS_INVALID);
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
  void testInvalidCrosslinkPattern(String input) {
    decoratePost(postR, input);
    assertThat(postR.getBody()).doesNotContain(CROSSLINK_CLASS_VALID, CROSSLINK_CLASS_INVALID);
  }
}
