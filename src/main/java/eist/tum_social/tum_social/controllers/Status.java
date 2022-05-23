package eist.tum_social.tum_social.controllers;

public class Status {
    public static final Status SUCCESS = new Status();

    private String errorMessage = "";

    public static Status ERROR(String errorMessage) {
        Status error = new Status();
        error.errorMessage = errorMessage;
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
