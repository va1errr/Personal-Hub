package com.va1err.personalhub.telegram.callback.handler;

import com.va1err.personalhub.telegram.callback.SettingsCallbackAction;

public interface SettingsCallbackHandler {

    SettingsCallbackAction action();

    void handle(SettingsCallbackContext context);

}
