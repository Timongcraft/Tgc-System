package timongcraft.system.util;

//Maybe make all messages to a variable
public enum Messages {
    NAME("value");

    String message;

    Messages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
