package com.gowsow.shiba.ui.login.test;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNot.not;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class LoginUiTest {

    private static final String TAG = LoginUiTest.class.getSimpleName();
    private static final String PACKAGE_NAME = "com.gowsow.shiba";
    private static final int LAUNCH_TIMEOUT = 5000;
    private UiDevice mDevice;
    private String email = "gigigaga1113@gmail.com";
    private String password = "mock";

    @Before
    public void startMainActivityFromHomeScreen() throws Exception {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            throw new Exception("email or password null");
        }
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();

        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch app
        Context context = getApplicationContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(PACKAGE_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void login_success() throws Exception {
        //Given
        mDevice.findObject(By.res(PACKAGE_NAME, "email"))
                .setText(email);
        mDevice.findObject(By.res(PACKAGE_NAME, "password"))
                .setText(password);
        //when
        mDevice.findObject(By.res(PACKAGE_NAME, "login"))
                .click();
        Thread.sleep(5000);
        //Assert
        UiObject2 loginStatusResult = mDevice.findObject(By.res(PACKAGE_NAME, "login_status"));
        assertThat(loginStatusResult.getText(), not(containsString("failed")));
    }

    @Test
    public void login_fail() throws Exception {
        //Given
        mDevice.findObject(By.res(PACKAGE_NAME, "email"))
                .setText(email);
        mDevice.findObject(By.res(PACKAGE_NAME, "password"))
                .setText("incorrect");
        //when
        mDevice.findObject(By.res(PACKAGE_NAME, "login"))
                .click();
        Thread.sleep(5000);
        //Assert
        UiObject2 loginStatusResult = mDevice.findObject(By.res(PACKAGE_NAME, "login_status"));
        assertThat(loginStatusResult.getText(), containsString("failed"));
    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = getApplicationContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }
}
