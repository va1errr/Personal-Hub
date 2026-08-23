package com.va1err.personalhub.telegram.command;

import com.va1err.personalhub.shared.exception.DuplicateTgUserIdException;
import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.message.MessageResponder;
import com.va1err.personalhub.telegram.message.MessageSender;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import com.va1err.personalhub.telegram.state.TimezoneInputState;
import com.va1err.personalhub.user.application.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Optional;

@ConditionalOnTelegramEnabled
@Component
public class StartCommand implements Command {

    private static final Logger log =
        LoggerFactory.getLogger(StartCommand.class);

    private final UserService userService;
    private final MessageSender messageSender;
    private final MessageResponder messageResponder;
    private final TimezoneInputState timezoneInputState;

    public StartCommand(
        UserService userService,
        MessageSender messageSender,
        MessageResponder messageResponder,
        TimezoneInputState timezoneInputState
    ) {
        this.userService = userService;
        this.messageSender = messageSender;
        this.messageResponder = messageResponder;
        this.timezoneInputState = timezoneInputState;
    }

    @Override
    public String name() {
        return "/start";
    }

    @Override
    public void execute(Message message) {
        Long tgUserId = message.getFrom().getId();
        Long chatId = message.getChatId();

        RegistrationResult result = register(message);

        Optional<Message> responseMessage = messageResponder.respond(
            message,
            result.responseText()
        );

        if (responseMessage.isEmpty() || !result.newlyRegistered()) {
            return;
        }

        Optional<Message> timezonePrompt =
            messageSender.send(chatId, TelegramMessages.initializeTimezone());
        timezonePrompt.ifPresent(prompt ->
            timezoneInputState.begin(
                tgUserId,
                chatId,
                prompt.getMessageId(),
                TimezoneInputState.Operation.INITIALIZE
            )
        );
    }

    private RegistrationResult register(Message message) {
        Long tgUserId = message.getFrom().getId();
        String tgUsername = message.getFrom().getUserName();
        String firstName = message.getFrom().getFirstName();
        String lastName = message.getFrom().getLastName();

        try {
            userService.registerUser(tgUserId, tgUsername);

            return new RegistrationResult(
                true,
                TelegramMessages.registrationCompleted(
                    firstName,
                    lastName
                )
            );
        } catch (DuplicateTgUserIdException exception) {
            return new RegistrationResult(
                false,
                TelegramMessages.alreadyRegistered(
                    firstName,
                    lastName
                )
            );
        } catch (RuntimeException exception) {
            log.error(
                "Registration failed for Telegram user {}",
                tgUserId,
                exception
            );

            return new RegistrationResult(
                false,
                TelegramMessages.systemUnavailable()
            );
        }
    }

    private record RegistrationResult(
        boolean newlyRegistered,
        String responseText
    ) {
    }

}
