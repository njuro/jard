package com.github.njuro.jard.attachment.embedded;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedException;
import ac.simons.oembed.OembedService;
import com.github.njuro.jard.attachment.Attachment;
import com.github.njuro.jard.attachment.embedded.handlers.EmbeddedAttachmentHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Service for handling of embedded attachments using OEmbed format providers. */
@Service
public class EmbedService {

  private final List<EmbeddedAttachmentHandler> embeddedAttachmentHandlers;
  private final OembedService oembedService;

  @Autowired
  public EmbedService(List<EmbeddedAttachmentHandler> embeddedAttachmentHandlers) {
    this.embeddedAttachmentHandlers = embeddedAttachmentHandlers;

    oembedService = configureOembedService();
  }

  /**
   * Registers all detected embedded attachment handlers and sets up OEmbed service.
   *
   * @see EmbeddedAttachmentHandler
   * @see OembedService
   */
  private OembedService configureOembedService() {
    List<OembedEndpoint> endpoints =
        embeddedAttachmentHandlers.stream()
            .map(EmbeddedAttachmentHandler::registerEndpoint)
            .collect(Collectors.toList());

    var service =
        new OembedService(
            HttpClientBuilder.create().addInterceptorLast(new OembedResponseFixer()).build(),
            null,
            endpoints,
            null);
    service.setAutodiscovery(true);
    service.setCacheName("oembed");

    return service;
  }

  /**
   * Processes embedded attachment.
   *
   * @param url URL to content to be embedded
   * @param attachment {@link Attachment} connected to this embedding
   * @throws OembedException if getting response from OEmbed endpoint fails
   * @throws IllegalArgumentException if there is no response from OEmbed endpoint
   */
  public void processEmbedded(String url, Attachment attachment) {
    var response =
        oembedService
            .getOembedResponseFor(url)
            .orElseThrow(() -> new IllegalArgumentException("Failed to get oembed response"));
    var handler = getHandlerForProvider(response.getProviderName());
    handler.setEmbedData(response, url, attachment);
    attachment.setOriginalFilename(handler.getTitle(response));
  }

  /**
   * @param providerName Name of content provider
   * @return handler registered for given content provider
   * @throws IllegalArgumentException if there is no registered handler for given provider
   */
  public EmbeddedAttachmentHandler getHandlerForProvider(String providerName) {
    return embeddedAttachmentHandlers.stream()
        .filter(handler -> handler.getProviderName().equalsIgnoreCase(providerName))
        .findAny()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Failed to get handler for provider: " + providerName));
  }

  /**
   * Response interceptor which fixes responses not adhering to Oembed format and thus causing
   * parsing exception.
   */
  static class OembedResponseFixer implements HttpResponseInterceptor {

    /**
     * Pattern for matching width and height parameters in JSON/XML responses containing (illegal)
     * percentage value.
     */
    private static final Pattern PERCENTAGE_DIMENSION_PATTERN =
        Pattern.compile(
            "(?:<width.*>|\"width\":|<height.*>|\"height\":)\\s*\"?(\\d+%)\"?\\s*(?:</width>|</height>)?",
            Pattern.CASE_INSENSITIVE);

    @Override
    public void process(HttpResponse response, HttpContext context) throws IOException {
      var entity = new BasicHttpEntity();
      String content = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
      entity.setContent(
          IOUtils.toInputStream(removePercentageDimensions(content), StandardCharsets.UTF_8));
      response.setEntity(entity);
    }

    /**
     * Replaces all dimensions parameters (width, height) in the response with percentage values
     * with {@code 0}.
     */
    private String removePercentageDimensions(String response) {
      var matcher = PERCENTAGE_DIMENSION_PATTERN.matcher(response);
      var sb = new StringBuilder();
      while (matcher.find()) {
        matcher.appendReplacement(
            sb, matcher.group(0).replaceFirst(Pattern.quote(matcher.group(1)), "0"));
      }
      matcher.appendTail(sb);
      return sb.toString();
    }
  }
}
