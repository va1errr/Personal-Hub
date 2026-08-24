package com.va1err.personalhub.telegram.ui;

import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ConditionalOnTelegramEnabled
@Component
public class ActiveInboxItemsKeyboardFactory {

    public InlineKeyboardMarkup main(Slice<InboxItem> activeInbox, int page) {
        List<InlineKeyboardRow> rows = activeInbox.stream()
            .map(inboxItem -> {
                var button = InlineKeyboardButton.builder()
                    .text(limitContent(inboxItem.getContent(), 40))
                    .callbackData("inbox:item:" + inboxItem.getId() + ":page:" + page)
                    .build();

                return new InlineKeyboardRow(button);
            })
            .collect(Collectors.toCollection(ArrayList::new));

        var previousButton = InlineKeyboardButton.builder()
            .text("← Previous")
            .callbackData("inbox:page:" + (page - 1))
            .style("primary")
            .build();

        var nextButton = InlineKeyboardButton.builder()
            .text("Next →")
            .callbackData("inbox:page:" + (page + 1))
            .style("primary")
            .build();

        var navigationRow = new InlineKeyboardRow();

        if (activeInbox.getNumber() > 0) {
            navigationRow.add(previousButton);
        }

        if (activeInbox.hasNext()) {
            navigationRow.add(nextButton);
        }

        if (!navigationRow.isEmpty()){
            rows.add(navigationRow);
        }

        return InlineKeyboardMarkup.builder()
            .keyboard(rows)
            .build();
    }

    public InlineKeyboardMarkup back(int page) {
        var backButton = InlineKeyboardButton.builder()
            .text("← Back")
            .callbackData("inbox:page:" + page)
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(new InlineKeyboardRow(backButton)))
            .build();
    }

    public InlineKeyboardMarkup process(Long id, int page) {
        var toTaskButton = InlineKeyboardButton.builder()
            .text("✅ Convert to Task")
            .callbackData("inbox:toTask:item:" + id)
            .build();

        var toNoteButton = InlineKeyboardButton.builder()
            .text("\uD83D\uDCDD Convert to Note")
            .callbackData("inbox:toNote:item:" + id)
            .build();

        var backButton = InlineKeyboardButton.builder()
            .text("← Back")
            .callbackData("inbox:page:" + page)
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                new InlineKeyboardRow(toTaskButton),
                new InlineKeyboardRow(toNoteButton),
                new InlineKeyboardRow(backButton)
            ))
            .build();
    }

    private String limitContent(String content, int maxLength) {
        if (content == null || maxLength == 0) {
            return "";
        }

        if (maxLength < 0) {
            throw new IllegalStateException("Maximum length value must not be negative!");
        }

        int length = content.codePointCount(0, content.length());

        if (length <= maxLength) {
            return content;
        }

        int endIndex = content.offsetByCodePoints(0, maxLength - 1);

        return content.substring(0, endIndex) + "…";
    }

}
