package se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.model;

/**
 * En träff från Lantmäteriets registerbeteckning-API. {@code beteckning} är den kanoniska
 * fastighetsbeteckningen (t.ex. {@code "SUNDSVALL STENSTADEN 1:23"}) vars inledande ord är
 * registerområdet/kommunen — det som distriktskontrollen (I4) använder.
 */
public record Registerbeteckningsreferens(String beteckningsid, String registerenhet, String beteckning) {
}
