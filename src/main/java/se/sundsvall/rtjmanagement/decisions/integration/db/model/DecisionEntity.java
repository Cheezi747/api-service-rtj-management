package se.sundsvall.rtjmanagement.decisions.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "decision",
	indexes = {
		@Index(name = "idx_decision_errand_id", columnList = "errand_id")
	})
public class DecisionEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "errand_id", nullable = false, length = 255)
	private String errandId;

	@Column(name = "decision_type")
	private String decisionType;

	@Column(name = "value")
	private String value;

	@Column(name = "description", length = 4096)
	private String description;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created")
	private OffsetDateTime created;

	@PrePersist
	void prePersist() {
		if (created == null) {
			created = OffsetDateTime.now(ZoneId.systemDefault());
		}
	}

	public static DecisionEntity create() {
		return new DecisionEntity();
	}

	public String getId() {
		return id;
	}

	public String getErrandId() {
		return errandId;
	}

	public String getDecisionType() {
		return decisionType;
	}

	public String getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setErrandId(final String errandId) {
		this.errandId = errandId;
	}

	public void setDecisionType(final String decisionType) {
		this.decisionType = decisionType;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public DecisionEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public DecisionEntity withErrandId(final String errandId) {
		this.errandId = errandId;
		return this;
	}

	public DecisionEntity withDecisionType(final String decisionType) {
		this.decisionType = decisionType;
		return this;
	}

	public DecisionEntity withValue(final String value) {
		this.value = value;
		return this;
	}

	public DecisionEntity withDescription(final String description) {
		this.description = description;
		return this;
	}

	public DecisionEntity withCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public DecisionEntity withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof final DecisionEntity other))
			return false;
		return Objects.equals(id, other.id) && Objects.equals(errandId, other.errandId)
			&& Objects.equals(decisionType, other.decisionType) && Objects.equals(value, other.value)
			&& Objects.equals(description, other.description) && Objects.equals(createdBy, other.createdBy)
			&& Objects.equals(created, other.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, errandId, decisionType, value, description, createdBy, created);
	}

	@Override
	public String toString() {
		return "DecisionEntity{id='" + id + "', errandId='" + errandId + "', decisionType='" + decisionType
			+ "', value='" + value + "', createdBy='" + createdBy + "', created=" + created + '}';
	}
}
