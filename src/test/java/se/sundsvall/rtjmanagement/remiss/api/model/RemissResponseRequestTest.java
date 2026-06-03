package se.sundsvall.rtjmanagement.remiss.api.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemissResponseRequestTest {

	@Test
	void accessor() {
		final var request = new RemissResponseRequest("Miljökontoret har inget att invända.");

		assertThat(request.responseText()).isEqualTo("Miljökontoret har inget att invända.");
	}

	@Test
	void equalsHashCodeToString() {
		final var one = new RemissResponseRequest("svar");
		final var two = new RemissResponseRequest("svar");

		assertThat(one).isEqualTo(two).hasSameHashCodeAs(two);
		assertThat(one).hasToString(two.toString());
		assertThat(one).isNotEqualTo(new RemissResponseRequest("annat"));
	}
}
