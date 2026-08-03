# Suppress buggy report
# See more at http://stackoverflow.com/questions/33047806/proguard-duplicate-definition-of-library-class
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

# EmvTags fields are compared by reference (==). R8/ProGuard must keep them
# or TLV parsing fails and cards are not read in release builds.
-keepclassmembers class io.github.tapcard.emvnfccard.iso7816emv.EmvTags {
    public static final <fields>;
}
