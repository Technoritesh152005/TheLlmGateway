package com.llmgateway.shared;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.regex.Pattern;

public final class ApiKeyFormat {

    public static final String PREFIX = "sk-gw";
    public static final String Prefix = PREFIX;
    public static final int SECRET_LENGTH = 43;
    public static final int TOTAL_LENGTH = PREFIX.length() + SECRET_LENGTH;
    public static final int Display_Characters = 8;

    // Pattern is a class which helps to identify regex
    public static final Pattern WELL_FORMED = Pattern.compile("^" + Pattern.quote(PREFIX) + "[0-9A-Za-z]{" + SECRET_LENGTH + "}$");

    // Private constructor so class cannot be instantiated
    private ApiKeyFormat() {}

    public static Boolean isWellFormed(String key) {
        return key != null && key.length() == TOTAL_LENGTH && WELL_FORMED.matcher(key).matches();
    }

    // Converts the apikey to hash / hex format
    public static String sha256hex(String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(key.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is guaranteed by the Java platform", e);
        }
    }

    // Display prefix part of key
    public static String displayPrefix(String key) {
        return key.substring(0, PREFIX.length() + Display_Characters);
    }
}