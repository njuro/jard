package com.github.njuro.jboard.post;

import com.github.njuro.jboard.attachment.Attachment;
import com.github.njuro.jboard.attachment.AttachmentService;
import com.github.njuro.jboard.board.Board;
import com.github.njuro.jboard.board.BoardService;
import com.github.njuro.jboard.post.decorators.PostDecorator;
import com.github.njuro.jboard.thread.Thread;
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

  private final List<PostDecorator> decorators;

  @Autowired
  public PostService(
      BoardService boardService,
      AttachmentService attachmentService,
      PostRepository postRepository,
      List<PostDecorator> decorators) {
    this.boardService = boardService;
    this.attachmentService = attachmentService;
    this.postRepository = postRepository;
    this.decorators = decorators;
  }

  public Post savePost(Post post) {
    Board board = post.getThread().getBoard();
    post.setPostNumber(boardService.registerNewPost(board));

    decoratePost(post);

    if (post.getAttachment() != null) {
      post.setAttachment(attachmentService.saveAttachment(post.getAttachment()));
    }

    return postRepository.save(post);
  }

  public Post resolvePost(String boardLabel, Long postNumber) {
    return postRepository
        .findByThreadBoardLabelAndPostNumber(boardLabel, postNumber)
        .orElseThrow(PostNotFoundException::new);
  }

  public List<Post> getAllRepliesForThread(Thread thread) {
    return postRepository.findByThreadIdAndIdIsNotOrderByCreatedAtAsc(
        thread.getId(), thread.getOriginalPost().getId());
  }

  public List<Post> getLatestRepliesForThread(Thread thread) {
    List<Post> posts =
        postRepository.findTop5ByThreadIdAndIdIsNotOrderByCreatedAtDesc(
            thread.getId(), thread.getOriginalPost().getId());
    Collections.reverse(posts);
    return posts;
  }

  public int getNumberOfPostsInThread(Thread thread) {
    return postRepository.countByThreadId(thread.getId()).intValue();
  }

  public List<Post> getNewRepliesForThreadSince(Thread thread, Long lastPostNumber) {
    return postRepository.findByThreadIdAndPostNumberGreaterThanOrderByCreatedAtAsc(
        thread.getId(), lastPostNumber);
  }

  private void decoratePost(Post post) {
    post.setBody(HtmlUtils.htmlEscape(post.getBody()).replace("&gt;", ">"));

    for (PostDecorator decorator : decorators) {
      decorator.decorate(post);
    }

    post.setBody(post.getBody().replace("\n", "<br/>"));
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
