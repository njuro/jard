package com.github.njuro.jboard.services;

import com.github.njuro.jboard.decorators.Decorator;
import com.github.njuro.jboard.exceptions.PostNotFoundException;
import com.github.njuro.jboard.models.Attachment;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.repositories.PostRepository;
import java.util.Collections;
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
      final BoardService boardService,
      final AttachmentService attachmentService,
      final PostRepository postRepository,
      final List<Decorator> decorators) {
    this.boardService = boardService;
    this.attachmentService = attachmentService;
    this.postRepository = postRepository;
    this.decorators = decorators;
  }

  public Post savePost(final Post post) {
    final Board board = post.getThread().getBoard();
    post.setPostNumber(this.boardService.getPostCounter(board));
    this.boardService.increasePostCounter(board);

    decoratePost(post);

    if (post.getAttachment() != null) {
      post.setAttachment(this.attachmentService.saveAttachment(post.getAttachment()));
    }

    return this.postRepository.save(post);
  }

  private void decoratePost(final Post post) {
    post.setBody(HtmlUtils.htmlEscape(post.getBody()).replace("&gt;", ">"));

    for (final Decorator decorator : this.decorators) {
      decorator.decorate(post);
    }

    post.setBody(post.getBody().replace("\n", "<br/>"));
  }

  public Post resolvePost(final String boardLabel, final Long postNumber) {
    return this.postRepository
        .findByThreadBoardLabelAndPostNumber(boardLabel, postNumber)
        .orElseThrow(PostNotFoundException::new);
  }

  public List<Post> getAllRepliesForThread(final Thread thread) {
    return this.postRepository.findByThreadIdAndIdIsNotOrderByCreatedAtAsc(
        thread.getId(), thread.getOriginalPost().getId());
  }

  public List<Post> getLatestRepliesForThread(final Thread thread) {
    final List<Post> posts =
        this.postRepository.findTop5ByThreadIdAndIdIsNotOrderByCreatedAtDesc(
            thread.getId(), thread.getOriginalPost().getId());
    Collections.reverse(posts);
    return posts;
  }

  public List<Post> getNewRepliesForThreadSince(final Thread thread, final Long lastPostNumber) {
    return this.postRepository.findByThreadIdAndPostNumberGreaterThanOrderByCreatedAtAsc(
        thread.getId(), lastPostNumber);
  }

  public void deletePost(final Post post) {
    if (post.getAttachment() != null) {
      this.attachmentService.deleteAttachmentFile(post.getAttachment());
    }
    this.postRepository.delete(post);
  }

  public void deletePosts(final List<Post> posts) {
    final List<Attachment> attachments =
        posts.stream()
            .filter(post -> post.getAttachment() != null)
            .map(Post::getAttachment)
            .collect(Collectors.toList());
    this.attachmentService.deleteAttachmentFiles(attachments);
    this.postRepository.deleteAll(posts);
  }
}
