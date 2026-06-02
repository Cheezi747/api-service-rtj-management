package se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.rtjmanagement.core.api.validation.groups.OnCreate;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api.model.Sotningsobjekt;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.service.SotningsobjektService;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;
import static se.sundsvall.rtjmanagement.Constants.NAMESPACE_REGEXP;
import static se.sundsvall.rtjmanagement.Constants.NAMESPACE_VALIDATION_MESSAGE;

@RestController
@Validated
@RequestMapping("/{municipalityId}/{namespace}/errands/{errandId}/sotningsobjekt")
@Tag(name = "Egensotning — sotningsobjekt",
	description = "Sotningsobjekt (eldstäder/anläggningar) på ett EGENSOTNING-ärende. En rad per objekt; speglar per-objekt-tabellen i beslutet.")
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class SotningsobjektResource {

	private final SotningsobjektService service;

	SotningsobjektResource(final SotningsobjektService service) {
		this.service = service;
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = ALL_VALUE)
	@Operation(summary = "Skapa sotningsobjekt", responses = {
		@ApiResponse(responseCode = "201", headers = @Header(name = LOCATION, schema = @Schema(type = "string")), description = "Successful operation", useReturnTypeSchema = true)
	})
	@Validated(OnCreate.class)
	ResponseEntity<Void> createSotningsobjekt(
		@Parameter(name = "municipalityId", example = "2281") @ValidMunicipalityId(groups = OnCreate.class) @PathVariable final String municipalityId,
		@Parameter(name = "namespace", example = "EGENSOTNING") @Pattern(regexp = NAMESPACE_REGEXP, message = NAMESPACE_VALIDATION_MESSAGE, groups = OnCreate.class) @PathVariable final String namespace,
		@Parameter(name = "errandId") @ValidUuid(groups = OnCreate.class) @PathVariable final String errandId,
		@Valid @NotNull @RequestBody final Sotningsobjekt sotningsobjekt) {

		final var id = service.create(municipalityId, namespace, errandId, sotningsobjekt);
		return created(fromPath("/{municipalityId}/{namespace}/errands/{errandId}/sotningsobjekt/{objektId}")
			.buildAndExpand(municipalityId, namespace, errandId, id).toUri())
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Lista sotningsobjekt", responses = {
		@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true)
	})
	ResponseEntity<List<Sotningsobjekt>> readSotningsobjekt(
		@Parameter(name = "municipalityId", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "namespace", example = "EGENSOTNING") @Pattern(regexp = NAMESPACE_REGEXP, message = NAMESPACE_VALIDATION_MESSAGE) @PathVariable final String namespace,
		@Parameter(name = "errandId") @ValidUuid @PathVariable final String errandId) {

		return ok(service.readAll(municipalityId, namespace, errandId));
	}

	@GetMapping(path = "/{objektId}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Hämta sotningsobjekt", responses = {
		@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Sotningsobjekt> readSotningsobjektById(
		@Parameter(name = "municipalityId", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "namespace", example = "EGENSOTNING") @Pattern(regexp = NAMESPACE_REGEXP, message = NAMESPACE_VALIDATION_MESSAGE) @PathVariable final String namespace,
		@Parameter(name = "errandId") @ValidUuid @PathVariable final String errandId,
		@Parameter(name = "objektId") @ValidUuid @PathVariable final String objektId) {

		return ok(service.read(municipalityId, namespace, errandId, objektId));
	}

	@PatchMapping(path = "/{objektId}", consumes = APPLICATION_JSON_VALUE, produces = ALL_VALUE)
	@Operation(summary = "Uppdatera sotningsobjekt (PATCH — endast icke-null-fält tillämpas)", responses = {
		@ApiResponse(responseCode = "204", description = "Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<Void> updateSotningsobjekt(
		@Parameter(name = "municipalityId", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "namespace", example = "EGENSOTNING") @Pattern(regexp = NAMESPACE_REGEXP, message = NAMESPACE_VALIDATION_MESSAGE) @PathVariable final String namespace,
		@Parameter(name = "errandId") @ValidUuid @PathVariable final String errandId,
		@Parameter(name = "objektId") @ValidUuid @PathVariable final String objektId,
		@Valid @NotNull @RequestBody final Sotningsobjekt patch) {

		service.update(municipalityId, namespace, errandId, objektId, patch);
		return noContent().header(CONTENT_TYPE, ALL_VALUE).build();
	}

	@DeleteMapping(path = "/{objektId}", produces = ALL_VALUE)
	@Operation(summary = "Ta bort sotningsobjekt", responses = {
		@ApiResponse(responseCode = "204", description = "Successful operation", useReturnTypeSchema = true)
	})
	ResponseEntity<Void> deleteSotningsobjekt(
		@Parameter(name = "municipalityId", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "namespace", example = "EGENSOTNING") @Pattern(regexp = NAMESPACE_REGEXP, message = NAMESPACE_VALIDATION_MESSAGE) @PathVariable final String namespace,
		@Parameter(name = "errandId") @ValidUuid @PathVariable final String errandId,
		@Parameter(name = "objektId") @ValidUuid @PathVariable final String objektId) {

		service.delete(municipalityId, namespace, errandId, objektId);
		return noContent().header(CONTENT_TYPE, ALL_VALUE).build();
	}
}
