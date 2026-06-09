package se.sundsvall.rtjmanagement.permit.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;
import se.sundsvall.dept44.common.validators.annotation.OneOf;
import se.sundsvall.rtjmanagement.core.api.validation.groups.OnCreate;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

/**
 * An issued LBE permit (tillstånd) on an errand — the structured giltighetstid, villkor och status that
 * the flat {@code Decision} cannot hold. {@code validUntil} is computed from {@code permitType} +
 * {@code validFrom} when omitted (5 år brandfarlig / högst 3 år explosiv, förlängt till nästa fasta datum).
 */
@Schema(description = "Utfärdat tillstånd (LBE) med giltighetstid, villkor och status.")
public class Permit {

	@Schema(description = "Unikt id", examples = "cb20c51f-fcf3-42c0-b613-de563634a8ec", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private String id;

	@Schema(description = "Typ av tillstånd", examples = "BRANDFARLIG_VARA", allowableValues = {
		"BRANDFARLIG_VARA", "EXPLOSIV_VARA"
	})
	@NotBlank(groups = OnCreate.class)
	@OneOf(value = {
		"BRANDFARLIG_VARA", "EXPLOSIV_VARA"
	}, nullable = true)
	private String permitType;

	@Schema(description = "Giltig från (beslutsdatum). Default: dagens datum.", examples = "2026-06-03")
	@DateTimeFormat(iso = DATE)
	private LocalDate validFrom;

	@Schema(description = "Giltig till och med. Beräknas från permitType + validFrom om den utelämnas.", examples = "2031-09-01")
	@DateTimeFormat(iso = DATE)
	private LocalDate validUntil;

	@Schema(description = "Villkor för tillståndet", examples = "Lossning av drivmedel får endast ske dagtid.")
	@Size(max = 4096)
	private String conditions;

	@Schema(description = "Status", examples = "ACTIVE", allowableValues = {
		"ACTIVE", "REVOKED"
	})
	@OneOf(value = {
		"ACTIVE", "REVOKED"
	}, nullable = true)
	private String status;

	@Schema(description = "Skapad", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime created;

	@Schema(description = "Ändrad", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime modified;

	public static Permit create() {
		return new Permit();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Permit withId(final String id) {
		this.id = id;
		return this;
	}

	public String getPermitType() {
		return permitType;
	}

	public void setPermitType(final String permitType) {
		this.permitType = permitType;
	}

	public Permit withPermitType(final String permitType) {
		this.permitType = permitType;
		return this;
	}

	public LocalDate getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(final LocalDate validFrom) {
		this.validFrom = validFrom;
	}

	public Permit withValidFrom(final LocalDate validFrom) {
		this.validFrom = validFrom;
		return this;
	}

	public LocalDate getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(final LocalDate validUntil) {
		this.validUntil = validUntil;
	}

	public Permit withValidUntil(final LocalDate validUntil) {
		this.validUntil = validUntil;
		return this;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(final String conditions) {
		this.conditions = conditions;
	}

	public Permit withConditions(final String conditions) {
		this.conditions = conditions;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public Permit withStatus(final String status) {
		this.status = status;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public Permit withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public Permit withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Permit that = (Permit) o;
		return Objects.equals(id, that.id) && Objects.equals(permitType, that.permitType)
			&& Objects.equals(validFrom, that.validFrom) && Objects.equals(validUntil, that.validUntil)
			&& Objects.equals(conditions, that.conditions) && Objects.equals(status, that.status)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, permitType, validFrom, validUntil, conditions, status, created, modified);
	}

	@Override
	public String toString() {
		return "Permit{id='" + id + "', permitType='" + permitType + "', validFrom=" + validFrom
			+ ", validUntil=" + validUntil + ", conditions='" + conditions + "', status='" + status
			+ "', created=" + created + ", modified=" + modified + '}';
	}
}
