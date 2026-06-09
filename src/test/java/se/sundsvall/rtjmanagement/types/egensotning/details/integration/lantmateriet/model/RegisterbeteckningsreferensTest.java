package se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterbeteckningsreferensTest {

	@Test
	void accessors() {
		final var referens = new Registerbeteckningsreferens("id1", "enh1", "SUNDSVALL STENSTADEN 1:23");

		assertThat(referens.beteckningsid()).isEqualTo("id1");
		assertThat(referens.registerenhet()).isEqualTo("enh1");
		assertThat(referens.beteckning()).isEqualTo("SUNDSVALL STENSTADEN 1:23");
		assertThat(referens).isEqualTo(new Registerbeteckningsreferens("id1", "enh1", "SUNDSVALL STENSTADEN 1:23"));
	}
}
