package top.itning.smpandroid.util;

import android.util.Base64;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.util.Date;
import java.util.Optional;

import top.itning.smpandroid.entity.LoginUser;
import top.itning.smpandroid.entity.Wrap;

/**
 * @author itning
 */
public class UserUtils {
    /**
     * 从JWT中解析LoginUser
     *
     * @param token JWT
     * @return LoginUser
     */
    @CheckResult
    @NonNull
    public static Optional<LoginUser> getLoginUser(@Nullable String token) {
        if (token == null) {
            return Optional.empty();
        }
        try {
            String json = new String(Base64.decode(token.split("\\.")[1], Base64.URL_SAFE));
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json1, type, context) -> new Date(json1.getAsLong())).create();
            Wrap wrap = gson.fromJson(json, Wrap.class);
            LoginUser loginUser = gson.fromJson(wrap.getLoginUser(), LoginUser.class);
            return Optional.ofNullable(loginUser);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
