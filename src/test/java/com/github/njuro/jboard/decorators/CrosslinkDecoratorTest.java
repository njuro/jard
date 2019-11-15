package com.github.njuro.jboard.decorators;

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

import static com.github.njuro.jboard.helpers.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@UseMockDatabase
@Slf4j
class CrosslinkDecoratorTest extends DecoratorTest {

    @Autowired
    private CrosslinkDecorator decorator;

    @Autowired
    private ThreadService threadService;

    private Post postR;
    private Post postF;

    @Override
    protected Decorator initDecorator() {
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
        assertThat(postR.getBody()).containsSubsequence("/boards/r/1", CROSSLINK_CLASS_VALID);
    }

    @Test
    void testInvalidCrossThreadLink() {
        decoratePost(postR, ">>42");
        assertThat(postR.getBody()).contains(CROSSLINK_CLASS_INVALID);
    }

    @Test
    void testMultipleCrossThreadLinks() {
        decoratePost(postR, "To different thread >>3 more text\n >>1 and to OP");
        assertThat(postR.getBody()).containsSubsequence("/boards/r/3#3", CROSSLINK_CLASS_VALID, CROSSLINK_DIFF_THREAD,
                "/boards/r/1#1", CROSSLINK_CLASS_VALID, CROSSLINK_OP);
    }

    @Test
    void testValidCrossBoardLink() {
        decoratePost(postR, "Some text  >>>/fit/1");
        decoratePost(postF, ">>>/r/4 some text");
        assertThat(postR.getBody()).contains(CROSSLINK_CLASS_VALID, "/boards/fit/1");
        assertThat(postF.getBody()).contains(CROSSLINK_CLASS_VALID, "/boards/r/3#4");
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
    @ValueSource(strings = {">>", " >>>", ">> 1", ">>/r/1", ">>> /fit/1", ">>abc", ">>>abc", ">>>//123", ">>/fit/"})
    void testInvalidCrosslinkPattern(String input) {
        decoratePost(postR, input);
        assertThat(postR.getBody()).doesNotContain(CROSSLINK_CLASS_VALID, CROSSLINK_CLASS_INVALID);
    }

}
