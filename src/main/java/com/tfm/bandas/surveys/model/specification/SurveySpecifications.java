// com.tfm.bandas.surveys.search.SurveySpecifications.java
package com.tfm.bandas.surveys.model.specification;

import com.tfm.bandas.surveys.model.entity.SurveyEntity;
import com.tfm.bandas.surveys.utils.SurveyStatus;
import com.tfm.bandas.surveys.utils.SurveyType;
import org.springframework.data.jpa.domain.Specification;

public final class SurveySpecifications {

  private SurveySpecifications() {}

  public static Specification<SurveyEntity> all() {
    return (root, cq, cb) -> cb.conjunction();
  }

  /** Búsqueda libre en title/description (case-insensitive). */
  public static Specification<SurveyEntity> text(String q) {
    if (q == null || q.isBlank()) return null;
    String like = "%" + q.toLowerCase() + "%";
    return (root, cq, cb) -> cb.or(
        cb.like(cb.lower(root.get("title")), like),
        cb.like(cb.lower(root.get("description")), like)
    );
  }

  public static Specification<SurveyEntity> titleContains(String title) {
    if (title == null || title.isBlank()) return null;
    String like = "%" + title.toLowerCase() + "%";
    return (root, cq, cb) -> cb.like(cb.lower(root.get("title")), like);
  }

  public static Specification<SurveyEntity> descriptionContains(String desc) {
    if (desc == null || desc.isBlank()) return null;
    String like = "%" + desc.toLowerCase() + "%";
    return (root, cq, cb) -> cb.like(cb.lower(root.get("description")), like);
  }

  public static Specification<SurveyEntity> eventIdEquals(String eventId) {
    if (eventId == null || eventId.isBlank()) return null;
    return (root, cq, cb) -> cb.equal(root.get("eventId"), eventId);
  }

  public static Specification<SurveyEntity> statusEquals(SurveyStatus status) {
    if (status == null) return null;
    return (root, cq, cb) -> cb.equal(root.get("status"), status);
  }

  public static Specification<SurveyEntity> surveyTypeEquals(SurveyType surveyType) {
    if (surveyType == null) return null;
    return (root, cq, cb) -> cb.equal(root.get("surveyType"), surveyType);
  }

  // Si quieres filtrar por ventana:
  public static Specification<SurveyEntity> opensAtFrom(java.time.Instant from) {
    if (from == null) return null;
    return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("opensAt"), from);
  }

  public static Specification<SurveyEntity> opensAtTo(java.time.Instant to) {
    if (to == null) return null;
    return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("opensAt"), to);
  }

  public static Specification<SurveyEntity> closesAtFrom(java.time.Instant from) {
    if (from == null) return null;
    return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("closesAt"), from);
  }

  public static Specification<SurveyEntity> closesAtTo(java.time.Instant to) {
    if (to == null) return null;
    return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("closesAt"), to);
  }
}
