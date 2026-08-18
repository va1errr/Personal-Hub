package com.va1err.personalhub.telegram.handler;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ConditionalOnTelegramEnabled
@Component
public class CommandHandler implements MessageHandler {

    private static final Logger log =
        LoggerFactory.getLogger(CommandHandler.class);

    private final Map<String, Command> commands;
    private final UnknownCommandHandler unknownCommandHandler;

    public CommandHandler(
        List<Command> commands,
        UnknownCommandHandler unknownCommandHandler
    ) {
        this.commands = commands.stream()
            .collect(Collectors.toUnmodifiableMap(
                Command::name,
                Function.identity()
            ));

        this.unknownCommandHandler = unknownCommandHandler;
    }

    @Override
    public void handle(Message message) {
        String commandName = extractCommandName(message.getText());

        Command command = commands.get(commandName);

        if (command == null) {
            unknownCommandHandler.handle(message, commandName);
            return;
        }

        command.execute(message);
    }

    private String extractCommandName(String text) {
        String commandName = text.strip().split("\\s+", 2)[0];
        int mentionIndex = commandName.indexOf("@");

        return mentionIndex >= 0
            ? commandName.substring(0, mentionIndex)
            : commandName;
    }

}
