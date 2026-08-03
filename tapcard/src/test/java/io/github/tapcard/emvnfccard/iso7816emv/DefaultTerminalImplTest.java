package io.github.tapcard.emvnfccard.iso7816emv;

import org.fest.assertions.Assertions;
import org.junit.Test;

import io.github.tapcard.emvnfccard.iso7816emv.impl.DefaultTerminalImpl;
import io.github.tapcard.emvnfccard.model.enums.CountryCodeEnum;
import io.github.tapcard.emvnfccard.model.enums.CurrencyEnum;
import io.github.tapcard.emvnfccard.utils.BytesUtils;

public class DefaultTerminalImplTest {

	@Test
	public void testDefaultFranceEuro() {
		DefaultTerminalImpl terminal = new DefaultTerminalImpl();
		Assertions.assertThat(terminal.constructValue(new TagAndLength(EmvTags.TERMINAL_COUNTRY_CODE, 2)))
				.isEqualTo(new byte[] { 0x02, 0x50 });
		Assertions.assertThat(terminal.constructValue(new TagAndLength(EmvTags.TRANSACTION_CURRENCY_CODE, 2)))
				.isEqualTo(BytesUtils.fromString("0978"));
	}

	@Test
	public void testUkraineDerivesUah() {
		DefaultTerminalImpl terminal = new DefaultTerminalImpl();
		terminal.setCountryCode(CountryCodeEnum.UA);

		Assertions.assertThat(terminal.getCountryCode()).isEqualTo(CountryCodeEnum.UA);
		// 804 = Ukraine, 980 = UAH
		Assertions.assertThat(terminal.constructValue(new TagAndLength(EmvTags.TERMINAL_COUNTRY_CODE, 2)))
				.isEqualTo(BytesUtils.fromString("0804"));
		Assertions.assertThat(terminal.constructValue(new TagAndLength(EmvTags.TRANSACTION_CURRENCY_CODE, 2)))
				.isEqualTo(BytesUtils.fromString("0980"));
	}

	@Test
	public void testExplicitCurrencyOverride() {
		DefaultTerminalImpl terminal = new DefaultTerminalImpl();
		terminal.setCountryCode(CountryCodeEnum.UA);
		terminal.setCurrency(CurrencyEnum.USD);

		Assertions.assertThat(terminal.constructValue(new TagAndLength(EmvTags.TERMINAL_COUNTRY_CODE, 2)))
				.isEqualTo(BytesUtils.fromString("0804"));
		Assertions.assertThat(terminal.constructValue(new TagAndLength(EmvTags.TRANSACTION_CURRENCY_CODE, 2)))
				.isEqualTo(BytesUtils.fromString("0840"));
	}
}
