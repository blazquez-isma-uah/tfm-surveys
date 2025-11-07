// com.tfm.bandas.surveys.web.EtagUtils.java
package com.tfm.bandas.surveys.utils;

import com.tfm.bandas.surveys.exception.PreconditionRequiredException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public final class EtagUtils {
  private EtagUtils() {}

  // Formato débil: W/"<version>"
  public static String toEtag(int version) {
    return "W/\"" + version + "\"";
  }

  // Acepta W/"n" o "n"
  public static int parseIfMatchToVersion(String ifMatch) {
    if (ifMatch == null || ifMatch.isBlank())
      throw new PreconditionRequiredException("If-Match header is required");
    String v = ifMatch.trim();
    if ("*".equals(v)) {
      throw new IllegalArgumentException("If-Match * not allowed for this operation");
    }
    if (v.startsWith("W/")) v = v.substring(2).trim();
    if (v.startsWith("\"") && v.endsWith("\"")) v = v.substring(1, v.length()-1);
    try {
      return Integer.parseInt(v);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid If-Match value: " + ifMatch);
    }
  }

  public static <T> ResponseEntity<T> withEtag(ResponseEntity.BodyBuilder builder, int version, T body) {
    return builder.header(HttpHeaders.ETAG, toEtag(version)).body(body);
  }
}
