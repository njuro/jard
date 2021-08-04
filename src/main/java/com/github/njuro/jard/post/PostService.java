package com.github.njuro.jard.post;

import com.github.njuro.jard.attachment.AttachmentService;
import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.board.BoardService;
import com.github.njuro.jard.post.decorators.PostDecorator;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

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
   * @param post post to be saved - cannot be null
   * @return saved post (with assigned id)
   * @see #decoratePost(Post)
   */
  public Post savePost(Post post) {
    Objects.requireNonNull(post);
    Objects.requireNonNull(post.getThread());

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
   * @param boardLabel label of {@link Board} the post belongs to
   * @param postNumber number of the post
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
   * @param threadId ID of thread to get replies for - cannot be null
   * @param originalPostId ID of original post of the thread
   * @return thread replies ordered by their creation date from least to most recent
   * @throws NullPointerException if thread ID is {@code null} or thread's original post ID is
   *     {@code null}
   */
  public List<Post> getAllRepliesForThread(UUID threadId, UUID originalPostId) {
    Objects.requireNonNull(threadId);
    Objects.requireNonNull(originalPostId);

    return postRepository.findByThreadIdAndIdIsNotOrderByCreatedAtAsc(threadId, originalPostId);
  }

  /**
   * Retrieves up to 5 most recent posts from given thread (excluding thread's original post).
   *
   * @param threadId ID of thread to get replies for
   * @param originalPostId ID of original post of the thread
   * @return most recent replies to thread ordered by their creation date from least to most recent
   * @throws NullPointerException if thread ID is {@code null} or thread's original post ID is
   *     {@code null}
   */
  public List<Post> getLatestRepliesForThread(UUID threadId, UUID originalPostId) {
    Objects.requireNonNull(threadId);
    Objects.requireNonNull(originalPostId);

    List<Post> posts =
        postRepository.findTop5ByThreadIdAndIdIsNotOrderByCreatedAtDesc(threadId, originalPostId);
    Collections.reverse(posts);
    return posts;
  }

  /**
   * Counts number of posts in given thread.
   *
   * @param threadId ID of thread to count posts in - cannot be null
   * @return total number of posts in thread (including original post)
   * @throws NullPointerException if thread ID is {@code null}
   */
  public int getNumberOfPostsInThread(UUID threadId) {
    Objects.requireNonNull(threadId);

    return postRepository.countByThreadId(threadId).intValue();
  }

  /**
   * Retrieves all replies of thread created after given post.
   *
   * @param threadId ID of thread to get new replies for
   * @param lastPostNumber number of post after which we look for new replies
   * @return all new replies sorted by creation date from least to most recent
   * @throws NullPointerException if thread ID is {@code null}
   */
  public List<Post> getNewRepliesForThreadSince(UUID threadId, Long lastPostNumber) {
    Objects.requireNonNull(threadId);

    return postRepository.findByThreadIdAndPostNumberGreaterThanOrderByCreatedAtAsc(
        threadId, lastPostNumber);
  }

  /**
   * Process body of given post with all registered implementations of {@link PostDecorator} and
   * escapes any HTML in user input.
   *
   * @param post post to decorate - cannot be {@code null}
   * @throws NullPointerException if post is {@code null}
   */
  private void decoratePost(Post post) {
    Objects.requireNonNull(post);

    post.setBody(HtmlUtils.htmlEscape(post.getBody()).replace("&gt;", ">"));

    for (PostDecorator decorator : decorators) {
      decorator.decorate(post);
    }

    post.setBody(post.getBody().replace("\n", "<br/>"));
  }

  /**
   * Updates given post.
   *
   * @param post Post to update
   * @return updated post
   */
  public Post updatePost(Post post) {
    return postRepository.save(post);
  }

  /**
   * Deletes given post from database and also its attachment (if it has one).
   *
   * @param post to delete - cannot be {@code null}
   * @throws NullPointerException if post is {@code null}
   * @throws IOException if deletion of attachment file fails
   */
  public void deletePost(Post post) throws IOException {
    Objects.requireNonNull(post);

    if (post.getAttachment() != null) {
      attachmentService.deleteAttachment(post.getAttachment());
    }

    postRepository.delete(post);
  }

  /**
   * Deletes all given posts from database and also their attachments (if they have any).
   *
   * @param posts posts to delete - cannot be {@code null}
   * @throws NullPointerException if post list is {@code null}
   * @throws IOException if deletion of attachment file fails
   */
  public void deletePosts(List<Post> posts) throws IOException {
    Objects.requireNonNull(posts);

    var attachments =
        posts.stream()
            .map(Post::getAttachment)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    attachmentService.deleteAttachments(attachments);

    postRepository.deleteAll(posts);
  }
}
