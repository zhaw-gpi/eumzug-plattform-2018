package ch.zhaw.gpi.eumzugplattform.enumerations;

/**
 * Alle Möglichkeiten die der HeaderType als ActionCode annehmen kann. Die Codes
 * werden in ein Enum verschachtelt, damit der Code lesbar ist und nicht einfach
 * Zahlen im Code stehen und niemand weiss, was diese bedeuten.
 */
public enum WebServiceHeaderActionCodeEnumeration {
    NEW("1"),
    RECALL("3"),
    CORRECTION("4"),
    REQUEST("5"),
    RESPONSE("6"),
    NEGATIVE_REPORT("8"),
    POSITIVE_REPORT("9"),
    FORWARD("10"),
    REMINDER("12");

    private final String code;

    WebServiceHeaderActionCodeEnumeration(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }

}
