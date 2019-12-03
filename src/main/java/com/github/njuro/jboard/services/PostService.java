package com.github.njuro.jboard.services;

import com.github.njuro.jboard.decorators.Decorator;
import com.github.njuro.jboard.exceptions.PostNotFoundException;
import com.github.njuro.jboard.models.Attachment;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.repositories.PostRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

/**
 * Service methods for manipulating {@link Post posts}
 *
 * @author njuro
 */
@Service
@Transactional(noRollbackFor = PostNotFoundException.class)
public class PostService {

  private final BoardService boardService;

  private final AttachmentService attachmentService;

  private final PostRepository postRepository;

  private final List<Decorator> decorators;

  @Autowired
  public PostService(
      BoardService boardService,
      AttachmentService attachmentService,
      PostRepository postRepository,
      List<Decorator> decorators) {
    this.boardService = boardService;
    this.attachmentService = attachmentService;
    this.postRepository = postRepository;
    this.decorators = decorators;
  }

  public Post savePost(Post post) {
    Board board = post.getThread().getBoard();
    post.setPostNumber(boardService.getPostCounter(board));
    boardService.increasePostCounter(board);

    decoratePost(post);

    if (post.getAttachment() != null) {
      post.setAttachment(attachmentService.saveAttachment(post.getAttachment()));
    }

    return postRepository.save(post);
  }

  private void decoratePost(Post post) {
    post.setBody(HtmlUtils.htmlEscape(post.getBody()).replace("&gt;", ">"));

    for (Decorator decorator : decorators) {
      decorator.decorate(post);
    }

    post.setBody(post.getBody().replace("\n", "<br/>"));
  }

  public Post resolvePost(String boardLabel, Long postNumber) {
    return postRepository
        .findByThreadBoardLabelAndPostNumber(boardLabel, postNumber)
        .orElseThrow(PostNotFoundException::new);
  }

  public List<Post> findNewPostsInThread(Thread thread, Long lastPostNumber) {
    return postRepository.findByThreadIdAndPostNumberGreaterThanOrderByCreatedAtAsc(
        thread.getId(), lastPostNumber);
  }

  public void deletePost(Post post) {
    if (post.getAttachment() != null) {
      attachmentService.deleteAttachmentFile(post.getAttachment());
    }
    postRepository.delete(post);
  }

  public void deletePosts(List<Post> posts) {
    List<Attachment> attachments =
        posts.stream()
            .filter(post -> post.getAttachment() != null)
            .map(Post::getAttachment)
            .collect(Collectors.toList());
    attachmentService.deleteAttachmentFiles(attachments);
    postRepository.deleteAll(posts);
  }
}
