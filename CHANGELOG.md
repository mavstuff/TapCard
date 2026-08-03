Change Log
==========

Version 2.2.0 *(2026-08-03)*
----------------------------

Author: Artem Moroz \<artem.moroz@gmail.com\>

Changes since [TapCard/TapCard](https://github.com/TapCard/TapCard) `2.1.3`:

* **Build / Android**: modernized toolchain for SDK 36 (Gradle 8.13, AGP 8.9, Java 17); AndroidX; `minSdk` 21.
* **Terminal config**: injectable `ITerminal` / `DefaultTerminalImpl` so PDOL country and currency can be set (e.g. Ukraine / UAH) instead of hardcoded France / EUR; wired through `EmvParser` and `NFCCardReader.setTerminal(...)`.
* **ProGuard / R8**: consumer keep rule for `EmvTags` static fields so release builds keep reading cards.
* **NFC**: PendingIntent mutability flags for API 31+; launcher activities marked `exported`.
* **Samples**: AndroidX migration for full demo and simple apps; Retrolambda / dexcount / jcenter removed.

Version 2.1.3 *(2017-?-?)*
----------------------------

Move library source to gradle build.
