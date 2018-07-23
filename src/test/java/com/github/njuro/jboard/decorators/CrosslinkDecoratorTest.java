package com.github.njuro.jboard.decorators;

import com.github.njuro.jboard.config.TestConfiguration;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.services.ThreadService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.njuro.jboard.helpers.Constants.CROSSLINK_CLASS_INVALID;
import static com.github.njuro.jboard.helpers.Constants.CROSSLINK_CLASS_VALID;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CrosslinkDecoratorTest {

    @Autowired
    private CrosslinkDecorator decorator;

    @Autowired
    private ThreadService threadService;

    private Post postR;
    private Post postF;

    @BeforeAll
    void initPosts() {
        postR = Post.builder().thread(threadService.resolveThread("r", 2L)).build();
        postF = Post.builder().thread(threadService.resolveThread("fit", 1L)).build();
    }

    @Test
    void testValidCrossThreadlink() {
        decoratePost(postR, ">>1");
        assertThat(postR.getBody()).containsSubsequence("/board/r/1", CROSSLINK_CLASS_VALID);
    }

    @Test
    void testInvalidCrossThreadLink() {
        decoratePost(postR, ">>42");
        assertThat(postR.getBody()).contains(CROSSLINK_CLASS_INVALID);
    }

    @Test
    void testMultipleCrossThreadLinks() {
        decoratePost(postR, "Some text >>3 more text\n >>2 even more text");
        assertThat(postR.getBody()).containsSubsequence("/board/r/2#3", CROSSLINK_CLASS_VALID, "/board/r/2#2", CROSSLINK_CLASS_VALID);
    }

    @Test
    void testValidCrossBoardLink() {
        decoratePost(postR, "Some text  >>>/fit/1");
        decoratePost(postF, ">>>/r/4 some text");
        assertThat(postR.getBody()).contains(CROSSLINK_CLASS_VALID, "/board/fit/1");
        assertThat(postF.getBody()).contains(CROSSLINK_CLASS_VALID, "/board/r/2#4");
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
        assertThat(postF.getBody()).contains(CROSSLINK_CLASS_VALID, "/board/fit");
        assertThat(postR.getBody()).contains(CROSSLINK_CLASS_INVALID);
    }

    @ParameterizedTest
    @ValueSource(strings = {">>", " >>>", ">> 1", ">>/r/1", ">>> /fit/1", ">>abc", ">>>abc", ">>>//123", ">>/fit/"})
    void testInvalidCrosslinkPattern(String input) {
        decoratePost(postR, input);
        assertThat(postR.getBody()).doesNotContain(CROSSLINK_CLASS_VALID, CROSSLINK_CLASS_INVALID);
    }

    private void decoratePost(Post post, String body) {
        post.setBody(body);
        decorator.decorate(post);
        log.info(body + " -> " + post.getBody());
    }
}
