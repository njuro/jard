package com.github.njuro.jard.attachment.embedded;

import ac.simons.oembed.OembedEndpoint;
import ac.simons.oembed.OembedException;
import ac.simons.oembed.OembedService;
import com.github.njuro.jard.attachment.Attachment;
import com.github.njuro.jard.attachment.embedded.handlers.EmbeddedAttachmentHandler;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.http.impl.client.HttpClientBuilder;
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

    var service = new OembedService(HttpClientBuilder.create().build(), null, endpoints, "jard");
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
}
