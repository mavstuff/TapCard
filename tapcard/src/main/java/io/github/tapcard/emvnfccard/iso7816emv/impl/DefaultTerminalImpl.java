/*
 * Copyright (C) 2013 MILLAU Julien
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tapcard.emvnfccard.iso7816emv.impl;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.tapcard.emvnfccard.iso7816emv.EmvTags;
import io.github.tapcard.emvnfccard.iso7816emv.ITerminal;
import io.github.tapcard.emvnfccard.iso7816emv.TagAndLength;
import io.github.tapcard.emvnfccard.iso7816emv.TerminalTransactionQualifiers;
import io.github.tapcard.emvnfccard.model.enums.CountryCodeEnum;
import io.github.tapcard.emvnfccard.model.enums.CurrencyEnum;
import io.github.tapcard.emvnfccard.model.enums.TransactionTypeEnum;
import io.github.tapcard.emvnfccard.utils.BytesUtils;

/**
 * Default terminal implementation. Country (and derived currency) can be
 * changed via {@link #setCountryCode(CountryCodeEnum)} /
 * {@link #setCurrency(CurrencyEnum)}.
 */
public class DefaultTerminalImpl implements ITerminal {

	private static final SecureRandom random = new SecureRandom();

	/**
	 * Terminal country code (default: France, matching historical behavior)
	 */
	private CountryCodeEnum countryCode = CountryCodeEnum.FR;

	/**
	 * Transaction currency. When null, currency is derived from
	 * {@link #countryCode} via {@link CurrencyEnum#find(CountryCodeEnum, CurrencyEnum)}.
	 */
	private CurrencyEnum currency;

	@Override
	public byte[] constructValue(final TagAndLength pTagAndLength) {
		byte ret[] = new byte[pTagAndLength.getLength()];
		byte val[] = null;
		if (pTagAndLength.getTag() == EmvTags.TERMINAL_TRANSACTION_QUALIFIERS) {
			TerminalTransactionQualifiers terminalQual = new TerminalTransactionQualifiers();
			terminalQual.setContactlessEMVmodeSupported(true);
			terminalQual.setReaderIsOfflineOnly(true);
			val = terminalQual.getBytes();
		} else if (pTagAndLength.getTag() == EmvTags.TERMINAL_COUNTRY_CODE) {
			val = BytesUtils.fromString(leftPad(String.valueOf(countryCode.getNumeric()),
					pTagAndLength.getLength() * 2, '0'));
		} else if (pTagAndLength.getTag() == EmvTags.TRANSACTION_CURRENCY_CODE) {
			CurrencyEnum currencyCode = getEffectiveCurrency();
			val = BytesUtils.fromString(leftPad(String.valueOf(currencyCode.getISOCodeNumeric()),
					pTagAndLength.getLength() * 2, '0'));
		} else if (pTagAndLength.getTag() == EmvTags.TRANSACTION_DATE) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.US);
			val = BytesUtils.fromString(sdf.format(new Date()));
		} else if (pTagAndLength.getTag() == EmvTags.TRANSACTION_TYPE) {
			val = new byte[] { (byte) TransactionTypeEnum.PURCHASE.getKey() };
		} else if (pTagAndLength.getTag() == EmvTags.AMOUNT_AUTHORISED_NUMERIC) {
			val = BytesUtils.fromString("00");
		} else if (pTagAndLength.getTag() == EmvTags.TERMINAL_TYPE) {
			val = new byte[] { 0x22 };
		} else if (pTagAndLength.getTag() == EmvTags.TERMINAL_CAPABILITIES) {
			val = new byte[] { (byte) 0xE0, (byte) 0xA0, 0x00 };
		} else if (pTagAndLength.getTag() == EmvTags.ADDITIONAL_TERMINAL_CAPABILITIES) {
			val = new byte[] { (byte) 0x8e, (byte) 0, (byte) 0xb0, 0x50, 0x05 };
		} else if (pTagAndLength.getTag() == EmvTags.DS_REQUESTED_OPERATOR_ID) {
			val = BytesUtils.fromString("7345123215904501");
		} else if (pTagAndLength.getTag() == EmvTags.UNPREDICTABLE_NUMBER) {
			random.nextBytes(ret);
		}
		if (val != null) {
			System.arraycopy(val, 0, ret, 0, Math.min(val.length, ret.length));
		}
		return ret;
	}

	/**
	 * Setter for the field countryCode. Currency is derived from country unless
	 * an explicit currency was set with {@link #setCurrency(CurrencyEnum)}.
	 *
	 * @param countryCode
	 *            the countryCode to set
	 */
	public void setCountryCode(final CountryCodeEnum countryCode) {
		if (countryCode != null) {
			this.countryCode = countryCode;
		}
	}

	/**
	 * Getter for the field countryCode
	 *
	 * @return the countryCode
	 */
	public CountryCodeEnum getCountryCode() {
		return countryCode;
	}

	/**
	 * Setter for an explicit transaction currency. Pass {@code null} to fall
	 * back to deriving currency from {@link #getCountryCode()}.
	 *
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(final CurrencyEnum currency) {
		this.currency = currency;
	}

	/**
	 * Getter for the field currency (may be null if derived from country)
	 *
	 * @return the currency
	 */
	public CurrencyEnum getCurrency() {
		return currency;
	}

	private CurrencyEnum getEffectiveCurrency() {
		if (currency != null) {
			return currency;
		}
		return CurrencyEnum.find(countryCode, CurrencyEnum.EUR);
	}

	private static String leftPad(final String value, final int size, final char padChar) {
		if (value == null) {
			return null;
		}
		int pads = size - value.length();
		if (pads <= 0) {
			return value;
		}
		StringBuilder builder = new StringBuilder(size);
		for (int i = 0; i < pads; i++) {
			builder.append(padChar);
		}
		builder.append(value);
		return builder.toString();
	}
}
