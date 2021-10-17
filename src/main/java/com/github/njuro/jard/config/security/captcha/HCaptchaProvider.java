package com.github.njuro.jard.config.security.captcha;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HCaptchaProvider implements CaptchaProvider {

  private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
  private static final URI VERIFY_URL = URI.create("https://hcaptcha.com/siteverify");

  @Value("${app.hcaptcha.secret:0x0000000000000000000000000000000000000000}")
  private String hCaptchaSecret;

  private final ObjectMapper objectMapper;

  @Autowired
  public HCaptchaProvider(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public CaptchaVerificationResult verifyCaptchaToken(String captchaToken) {
    if (hCaptchaSecret == null || hCaptchaSecret.isBlank()) {
      return new HCaptchaVerificationResult("Missing hCaptcha secret");
    }

    String payload = "response=%s&secret=%s".formatted(captchaToken, hCaptchaSecret);
    var request =
        HttpRequest.newBuilder()
            .timeout(Duration.ofSeconds(5))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .uri(VERIFY_URL)
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();

    try {
      var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (!HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
        return new HCaptchaVerificationResult("CAPTCHA verification server returned error");
      }

      return objectMapper.readValue(response.body(), HCaptchaVerificationResult.class);
    } catch (JsonProcessingException ex) {
      log.error("Reading response failed", ex);
      return new HCaptchaVerificationResult(
          "Failed to read response from CAPTCHA verification server");
    } catch (IOException ex) {
      log.error("Sending request failed", ex);
      return new HCaptchaVerificationResult("Failed to send CAPTCHA verification request");
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      return new HCaptchaVerificationResult("Sending of CAPTCHA request was interrupted");
    }
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  static class HCaptchaVerificationResult implements CaptchaVerificationResult {

    private boolean success;

    @JsonProperty("error-codes")
    private List<String> errorCodes = new ArrayList<>();

    public HCaptchaVerificationResult(String errorMessage) {
      success = false;
      errorCodes = Collections.singletonList(errorMessage);
    }

    @Override
    public boolean isVerified() {
      return success;
    }

    @Override
    public List<String> getErrors() {
      return errorCodes;
    }
  }
}
