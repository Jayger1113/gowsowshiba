package com.gowsow.shiba.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LogOutUserView {
    private String displayName;

    LogOutUserView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}