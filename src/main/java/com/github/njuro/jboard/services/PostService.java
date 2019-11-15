package com.github.njuro.jboard.services;

import com.github.njuro.jboard.decorators.Decorator;
import com.github.njuro.jboard.exceptions.PostNotFoundException;
import com.github.njuro.jboard.models.Attachment;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.dto.PostForm;
import com.github.njuro.jboard.repositories.PostRepository;
import com.github.njuro.jboard.utils.Tripcodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;


/**
 * Service methods for manipulating {@link Post posts}
 *
 * @author njuro
 */
@Service
@Transactional(noRollbackFor = PostNotFoundException.class)
public class PostService {

    private final BoardService boardService;

    private final ThreadService threadService;

    private final AttachmentService attachmentService;

    private final PostRepository postRepository;

    private final List<Decorator> decorators;

    @Autowired
    public PostService(BoardService boardService, @Lazy ThreadService threadService, AttachmentService attachmentService, PostRepository postRepository, List<Decorator> decorators) {
        this.boardService = boardService;
        this.threadService = threadService;
        this.attachmentService = attachmentService;
        this.postRepository = postRepository;
        this.decorators = decorators;
    }

    /**
     * Resolves post
     *
     * @param boardLabel - label of board where this post was made
     * @param postNumber - post number
     * @return resolved post
     * @throws PostNotFoundException when post was not found
     */
    public Post resolvePost(String boardLabel, Long postNumber) {
        return postRepository.findByThreadBoardLabelAndPostNumber(boardLabel, postNumber)
                .orElseThrow(PostNotFoundException::new);
    }

    /**
     * Creates new post from {@link PostForm}. Generates tripcode (if password was used) and saves an attachment
     *
     * @param form  - post form
     * @param board where this post was made
     * @return created post
     */
    public Post createPost(PostForm form, Board board) {
        String tripcode = Tripcodes.generateTripcode(form.getPassword());
        Post post = Post.builder().name(form.getName()).tripcode(tripcode).body(form.getBody()).build();

        if (form.getAttachment() != null && form.getAttachment().getSize() > 0) {
            Attachment attachment = attachmentService.saveAttachment(form.getAttachment(), board);
            post.setAttachment(attachment);
        }

        return post;
    }

    /**
     * Parses post's content with decorator, increases its board's post counter and thread modification time and saves it into database
     *
     * @param post  to save
     * @param board where this post was made
     * @return saved post
     */
    public Post savePost(Post post, Board board) {
        post.setPostNumber(boardService.getPostCounter(board));
        boardService.increasePostCounter(board);

        post.setBody(HtmlUtils.htmlEscape(post.getBody()).replace("&gt;", ">"));

        for (Decorator decorator : decorators) {
            decorator.decorate(post);
        }


        post.setBody(post.getBody().replace("\n", "<br/>"));

        post = postRepository.save(post);
        threadService.updateLastReplyTimestamp(post.getThread());
        return post;
    }

    /**
     * Saves updated post into database
     *
     * @param post to update
     * @return updated post
     */
    public Post updatePost(Post post) {
        return postRepository.save(post);
    }

    /**
     * Deletes post from database
     *
     * @param post to delete
     */
    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    public void deletePosts(List<Post> posts) {
        postRepository.deleteAll(posts);
    }
}
