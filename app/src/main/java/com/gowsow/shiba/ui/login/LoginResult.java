package com.gowsow.shiba.ui.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private LoggedInUserView loggedInUserView;
    @Nullable
    private LogOutUserView logOutUserView;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable LoggedInUserView loggedInUserView) {
        this.loggedInUserView = loggedInUserView;
    }

    LoginResult(@Nullable LogOutUserView logOutUserView) {
        this.logOutUserView = logOutUserView;
    }

    @Nullable
    LoggedInUserView getLoginUserView() {
        return loggedInUserView;
    }

    @Nullable
    LogOutUserView getLogoutView() {
        return logOutUserView;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}