package com.masterprojekat.music_online_classes.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    public static void validateUserInput(String regex, String userInput, String message) {
        Pattern validationPattern = Pattern.compile(regex);
        Matcher validationMatcher = validationPattern.matcher(userInput);
        if(!validationMatcher.matches()) {
            throw new IllegalArgumentException(message);
        }
    }
}
