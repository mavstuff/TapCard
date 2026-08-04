package io.github.tapcard.emvnfccard.utils;

import org.fest.assertions.Assertions;
import org.junit.Test;

import java.util.Arrays;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public class AtrTest {

	@Test
	public void testDescriptionFromATR() {

		Assertions.assertThat(AtrUtils.getDescription("3B 02 14 50")).isEqualTo(Arrays.asList("Schlumberger Multiflex 3k"));
		Assertions.assertThat(AtrUtils.getDescription(null)).isEqualTo(null);
		Assertions
				.assertThat(AtrUtils.getDescription("3B 07 64 11.........."))
				.isEqualTo(
						Arrays.asList("HID Corporate 1000 Format",
								"http://www.hidglobal.com/sites/hidglobal.com/files/resource_files/omnikey_contactless_developer_guide.pdf"));
		Assertions
				.assertThat(AtrUtils.getDescription("3F 05 DC 20 FC 00 01"))
				.isEqualTo(Arrays.asList("DigiCash Facility Card"));
	}

	@Test
	public void testDescriptionFromATS() {

		Assertions.assertThat(AtrUtils.getDescriptionFromAts("20 63 CB A3 A0")).isEqualTo(
				Arrays.asList("VISA card from Banque Populaire"));
		Assertions.assertThat(AtrUtils.getDescription(null)).isEqualTo(null);
	}
}
