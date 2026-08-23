package com.va1err.personalhub.telegram.callback;

import java.util.Arrays;
import java.util.Optional;

public enum SettingsCallbackAction {

    TIMEZONE("settings:timezone"),
    HEALTH_CHECK("settings:healthCheck"),
    BACK("settings:back");

    private final String data;

    SettingsCallbackAction(String data) {
        this.data = data;
    }

    public String data() {
        return data;
    }

    public static Optional<SettingsCallbackAction> from(String data) {
        return Arrays.stream(values())
            .filter(action -> action.data().equals(data))
            .findFirst();
    }

}
