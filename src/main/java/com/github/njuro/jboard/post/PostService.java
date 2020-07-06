package com.github.njuro.jboard.post;

import com.github.njuro.jboard.attachment.Attachment;
import com.github.njuro.jboard.attachment.AttachmentService;
import com.github.njuro.jboard.board.Board;
import com.github.njuro.jboard.board.BoardService;
import com.github.njuro.jboard.post.decorators.PostDecorator;
import com.github.njuro.jboard.thread.Thread;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

/** Service methods for storing/processing/retrieving/deleting {@link Post} entities. */
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

  /**
   * Generates and sets post number of given {@link Post}, process its body using registered post
   * decorators and saves it to database.
   *
   * @param post to be saved - cannot be null
   * @return saved post (with assigned id)
   * @see #decoratePost(Post)
   */
  public Post savePost(Post post) {
    Objects.requireNonNull(post, "Post cannot be null");
    Objects.requireNonNull(post.getThread(), "Post must have set thread");

    Board board = post.getThread().getBoard();
    post.setPostNumber(boardService.registerNewPost(board));

    if (post.getBody() != null) {
      decoratePost(post);
    }

    return postRepository.save(post);
  }

  /**
   * Attempts to resolve {@link Post} by given identifiers.
   *
   * @param boardLabel - label of {@link Board} the post belongs to
   * @param postNumber - number of the post
   * @return resolved post
   * @throws PostNotFoundException if such post does not exist in database
   */
  public Post resolvePost(String boardLabel, Long postNumber) {
    return postRepository
        .findByThreadBoardLabelAndPostNumber(boardLabel, postNumber)
        .orElseThrow(PostNotFoundException::new);
  }

  /**
   * Retrieves all posts belonging to given thread (excluding thread's original post).
   *
   * @param thread to get replies of - cannot be null
   * @return thread replies ordered by their creation date from least to most recent
   * @throws NullPointerException if thread is null
   */
  public List<Post> getAllRepliesForThread(Thread thread) {
    Objects.requireNonNull(thread, "Thread cannot be null");

    return postRepository.findByThreadIdAndIdIsNotOrderByCreatedAtAsc(
        thread.getId(), thread.getOriginalPost().getId());
  }

  /**
   * Retrieves up to 5 most recent posts from given thread (excluding thread's original post).
   *
   * @param thread to get replies from - cannot be null
   * @return most recent replies to thread ordered by their creation date from least to most recent
   * @throws NullPointerException if thread is null or thread's original post is null
   */
  public List<Post> getLatestRepliesForThread(Thread thread) {
    Objects.requireNonNull(thread, "Thread cannot be null");
    Objects.requireNonNull(thread.getOriginalPost(), "Thread's original post cannot be null");

    List<Post> posts =
        postRepository.findTop5ByThreadIdAndIdIsNotOrderByCreatedAtDesc(
            thread.getId(), thread.getOriginalPost().getId());
    Collections.reverse(posts);
    return posts;
  }

  /***
   * Counts number of posts in given thread.
   *
   * @param thread to count posts in
   * @return total number of posts in thread (including original post)
   * @throws NullPointerException if thread is null
   * */
  public int getNumberOfPostsInThread(Thread thread) {
    Objects.requireNonNull(thread, "Thread cannot be null");

    return postRepository.countByThreadId(thread.getId()).intValue();
  }

  /**
   * Retrieves all replies of thread created after given post.
   *
   * @param lastPostNumber number of post after which we look for new replies
   * @return all new replies sorted by creation date from least to most recent
   * @throws NullPointerException if thread is null
   */
  public List<Post> getNewRepliesForThreadSince(Thread thread, Long lastPostNumber) {
    Objects.requireNonNull(thread, "Thread cannot be null");

    return postRepository.findByThreadIdAndPostNumberGreaterThanOrderByCreatedAtAsc(
        thread.getId(), lastPostNumber);
  }

  /**
   * Process body of given post with all registered implementations of {@link PostDecorator} and
   * escapes any HTML in user input.
   *
   * @param post to process - cannot be null
   * @throws NullPointerException if post is null
   */
  private void decoratePost(Post post) {
    Objects.requireNonNull(post, "Post cannot be null");

    post.setBody(HtmlUtils.htmlEscape(post.getBody()).replace("&gt;", ">"));

    for (PostDecorator decorator : decorators) {
      decorator.decorate(post);
    }

    post.setBody(post.getBody().replace("\n", "<br/>"));
  }

  /**
   * Deletes given post from database and also its attachment (if it has one).
   *
   * @param post to delete - cannot be null
   */
  public void deletePost(Post post) {
    Objects.requireNonNull(post, "Post cannot be null");

    if (post.getAttachment() != null) {
      attachmentService.deleteAttachmentFile(post.getAttachment());
    }
    postRepository.delete(post);
  }

  /**
   * Deletes all given posts from database and also their attachments (if they have any).
   *
   * @param posts to delete
   */
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
