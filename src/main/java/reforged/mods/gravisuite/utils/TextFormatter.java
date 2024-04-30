package reforged.mods.gravisuite.utils;

public enum TextFormatter {
    BLACK("0"),
    DARK_BLUE("1"),
    DARK_GREEN("2"),
    DARK_AQUA("3"),
    DARK_RED("4"),
    DARK_PURPLE("5"),
    GOLD("6"),
    GRAY("7"), // default text color
    DARK_GRAY("8"),
    BLUE("9"),
    GREEN("a"),
    AQUA("b"),
    RED("c"),
    LIGHT_PURPLE("d"),
    YELLOW("e"),
    WHITE("f");

    private final String colorCode;

    TextFormatter(String colorIndex) {
        this.colorCode = "\247" + colorIndex;
    }

    public String literal(String text) {
        return this.colorCode + text + "\2477";
    }

    public String format(String text) {
        return this.colorCode + LangHelper.format(text) + "\2477";
    }

    public String format(String text, Object... args) {
        return this.colorCode + LangHelper.format(text, args) + "\2477";
    }
}
