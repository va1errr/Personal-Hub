package com.va1err.personalhub.telegram.message;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Optional;

@ConditionalOnTelegramEnabled
@Component
public class MessageResponder {

    private final MessageSender sender;
    private final MessageDeleter deleter;

    public MessageResponder(
        MessageSender sender,
        MessageDeleter deleter
    ) {
        this.sender = sender;
        this.deleter = deleter;
    }

    public Optional<Message> respond(
        Message source,
        String text
    ) {
        return deleteSourceIfSent(
            source,
            sender.send(source.getChatId(), text)
        );
    }

    public Optional<Message> respond(
        Message source,
        String text,
        InlineKeyboardMarkup keyboard
    ) {
        return deleteSourceIfSent(
            source,
            sender.send(source.getChatId(), text, keyboard)
        );
    }

    private Optional<Message> deleteSourceIfSent(
        Message source,
        Optional<Message> response
    ) {
        response.ifPresent(_ ->
            deleter.delete(
                source.getChatId(),
                source.getMessageId()
            ));

        return response;
    }

}
