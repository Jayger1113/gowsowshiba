package com.gowsow.shiba.ui.login;

import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gowsow.shiba.R;
import com.gowsow.shiba.repository.login.LoginRepository;
import com.gowsow.shiba.util.NetworkUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Response;

public class LoginViewModel extends ViewModel {

    private static final String TAG = LoginViewModel.class.getSimpleName();
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        Log.d(TAG, "login");
        // can be launched in a separate asynchronous job
        Observable.fromCallable(() -> {
                    //TODO: encapsulate datasource
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("email", email);
                    jsonObject.put("mima", password);
                    Response response = NetworkUtils.postJson(jsonObject.toString(), "", NetworkUtils.OkHttpClientEnum.TIMEOUT10);
                    String responseString = response.peekBody(2048).string();
                    Log.d(TAG, "login response from server: " + responseString);
                    try {
                        JSONObject jsonFromServer = new JSONObject(responseString);
                        if (jsonFromServer.has("status")) {
                            return (jsonFromServer.getInt("status") == 0) ? true : false;
                        }
                    } catch (Exception e) {
                        Log.d("login Error: ", e.toString());
                    }
                    return false;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    //Use result for display corresponding UI
                    Log.d(TAG, "login result: " + result);
                    if (result.booleanValue()) {
                        loginResult.setValue(new LoginResult(new LoggedInUserView(email)));
                    } else {
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    }
                });
        //                    Result<LoggedInUser> loggedInUserResult = loginRepository.login(email, password);
//
//                    if (loggedInUserResult instanceof Result.Success) {
//                        LoggedInUser data = ((Result.Success<LoggedInUser>) loggedInUserResult).getData();
//                        loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
//                    } else {
//                        loginResult.setValue(new LoginResult(R.string.login_failed));
//                    }
    }

    public void loginDataChanged(String email, String password) {
        if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_email, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isEmailValid(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return !StringUtils.isEmpty(password);
    }
}