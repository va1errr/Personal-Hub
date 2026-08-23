package com.va1err.personalhub.telegram.callback.handler;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.callback.CallbackQueryAcknowledger;
import com.va1err.personalhub.telegram.callback.SettingsCallbackAction;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ConditionalOnTelegramEnabled
@Component
public class CallbackQueryHandler {

    private final CallbackQueryAcknowledger callbackQueryAcknowledger;
    private final Map<SettingsCallbackAction, SettingsCallbackHandler> handlers;
    private final ActiveInboxNavigationCallbackHandler activeInboxNavigationCallbackHandler;
    private final InboxItemCallbackHandler inboxItemCallbackHandler;

    public CallbackQueryHandler(
        CallbackQueryAcknowledger callbackQueryAcknowledger,
        List<SettingsCallbackHandler> handlers,
        ActiveInboxNavigationCallbackHandler activeInboxNavigationCallbackHandler,
        InboxItemCallbackHandler inboxItemCallbackHandler
    ) {
        this.callbackQueryAcknowledger = callbackQueryAcknowledger;

        this.handlers = handlers.stream()
            .collect(Collectors.toUnmodifiableMap(
                SettingsCallbackHandler::action,
                Function.identity()
            ));
        this.activeInboxNavigationCallbackHandler = activeInboxNavigationCallbackHandler;
        this.inboxItemCallbackHandler = inboxItemCallbackHandler;
    }

    public void handle(CallbackQuery callbackQuery) {
        callbackQueryAcknowledger.acknowledge(callbackQuery.getId());

        if (callbackQuery.getMessage() == null) {
            return;
        }

        Long tgUserId = callbackQuery.getFrom().getId();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        String callbackData = callbackQuery.getData();

        if (callbackData == null) {
            return;
        }

        if (callbackData.startsWith("settings:")) {
            handleSettingsHandler(callbackData, tgUserId, chatId, messageId);
            return;
        }
        if (callbackData.startsWith("inbox:page:")) {
            activeInboxNavigationCallbackHandler.handle(tgUserId, chatId, messageId, callbackData);
        }
        if (callbackData.startsWith("inbox:item:")) {
            inboxItemCallbackHandler.handle(tgUserId, chatId, messageId, callbackData);
        }
    }

    private void handleSettingsHandler(
        String callbackData,
        Long tgUserId,
        Long chatId,
        Integer messageId
    ) {
        SettingsCallbackAction action =
            SettingsCallbackAction.from(callbackData).orElse(null);

        if (action == null) {
            return;
        }

        SettingsCallbackHandler handler = handlers.get(action);

        if (handler == null) {
            return;
        }

        handler.handle(new SettingsCallbackContext(
            tgUserId,
            chatId,
            messageId
        ));
    }

}
