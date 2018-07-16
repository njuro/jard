package com.github.njuro.jboard.services;

import com.github.njuro.jboard.exceptions.PostNotFoundException;
import com.github.njuro.jboard.models.Attachment;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.dto.PostForm;
import com.github.njuro.jboard.utils.Tripcodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByThreadBoardLabelAndPostNumber(String label, Long postNumber);
}

/**
 * Service methods for manipulating {@link Post posts}
 *
 * @author njuro
 */
@Service
@Transactional
public class PostService {

    private final BoardService boardService;

    private final AttachmentService attachmentService;

    private final PostRepository postRepository;

    @Autowired
    public PostService(BoardService boardService, AttachmentService attachmentService, PostRepository postRepository) {
        this.boardService = boardService;
        this.attachmentService = attachmentService;
        this.postRepository = postRepository;
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
        Post post = new Post(form.getName(), tripcode, form.getBody());

        if (form.getAttachment().getSize() > 0) {
            Attachment attachment = attachmentService.saveAttachment(form.getAttachment(), board);
            post.setAttachment(attachment);
        }

        return post;
    }

    /**
     * Saves post into database and increases its board's post counter
     *
     * @param post  to save
     * @param board where this post was made
     * @return saved post
     */
    public Post savePost(Post post, Board board) {
        post.setPostNumber(boardService.getPostCounter(board));
        boardService.increasePostCounter(board);

        return postRepository.save(post);
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
}
