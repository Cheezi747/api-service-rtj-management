package se.sundsvall.rtjmanagement.notes.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNote(
	@NotBlank @Size(max = 8192) String body,
	@Size(max = 64) String author) {}
