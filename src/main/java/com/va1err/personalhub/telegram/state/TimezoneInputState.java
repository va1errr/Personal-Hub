package com.va1err.personalhub.telegram.state;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ConditionalOnTelegramEnabled
@Component
public class TimezoneInputState {

    private final Map<Long, PendingTimezone> pending =
        new ConcurrentHashMap<>();

    public void begin(
        Long tgUserId,
        Long chatId,
        Integer messageId,
        Operation operation
    ) {
        pending.put(tgUserId, new PendingTimezone(chatId, messageId, operation));
    }

    public boolean isAwaiting(Long tgUserId) {
        return pending.containsKey(tgUserId);
    }

    public Optional<PendingTimezone> find(Long tgUserId) {
        return Optional.ofNullable(pending.get(tgUserId));
    }

    public void clear(Long tgUserId) {
        pending.remove(tgUserId);
    }

    public record PendingTimezone(
        Long chatId,
        Integer messageId,
        Operation operation) {

    }

    public enum Operation {
        INITIALIZE,
        UPDATE
    }

}
