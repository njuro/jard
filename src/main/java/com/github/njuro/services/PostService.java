package com.github.njuro.services;

import com.github.njuro.models.Attachment;
import com.github.njuro.models.Board;
import com.github.njuro.models.Post;
import com.github.njuro.models.dto.PostForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for CRUD operations with posts.
 *
 * @author njuro
 */
@Repository
interface PostRepository extends JpaRepository<Post, Long> {
}

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

    public Post createPost(PostForm form, Board board) {
        Post post = new Post(form.getName(), form.getTripcode(), form.getBody());

        if (form.getAttachment().getSize() > 0) {
            Attachment attachment = attachmentService.uploadAttachment(form.getAttachment(), board);
            post.setAttachment(attachment);
        }

        return post;
    }

    public Post savePost(Post post, Board board) {
        post.setCreatedAt(LocalDateTime.now());
        post.setPostNumber(boardService.getPostCounter(board));
        boardService.increasePostCounter(board);

        return postRepository.save(post);
    }
}
