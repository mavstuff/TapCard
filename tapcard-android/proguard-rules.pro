# NFCCardReader uses optional RxJava 1/2 bindings
-dontwarn io.github.tapcard.android.NFCCardReader

# EmvTags fields are compared by reference (==). R8/ProGuard must keep them
# or TLV parsing fails and cards are not read in release builds.
-keepclassmembers class io.github.tapcard.emvnfccard.iso7816emv.EmvTags {
    public static final <fields>;
}
