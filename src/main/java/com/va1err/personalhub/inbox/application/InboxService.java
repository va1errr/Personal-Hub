package com.va1err.personalhub.inbox.application;

import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.inbox.domain.InboxItemStatus;
import com.va1err.personalhub.inbox.infrastructure.InboxItemRepository;
import com.va1err.personalhub.shared.exception.InboxItemNotFoundException;
import com.va1err.personalhub.shared.exception.TgUserNotFoundException;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.infrastructure.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InboxService {

    private final InboxItemRepository inboxItemRepository;
    private final UserRepository userRepository;

    public InboxService(
        InboxItemRepository inboxItemRepository,
        UserRepository userRepository
    ) {
        this.inboxItemRepository = inboxItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public InboxItem addInboxItem(Long tgUserId, String content) {
        User user = userRepository.findByTgUserId(tgUserId)
            .orElseThrow(() -> new TgUserNotFoundException(tgUserId));

        InboxItem inboxItem = InboxItem.add(
            user,
            content
        );

        return inboxItemRepository.save(inboxItem);
    }

    @Transactional(readOnly = true)
    public Slice<InboxItem> getActiveInbox(
        Long tgUserId,
        int page,
        int size
    ) {
        Pageable pageable = PageRequest.of(
            page,
            Math.min(size, 20),
            Sort.by(
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("id")
            )
        );

        return inboxItemRepository.findByUser_tgUserIdAndStatus(
            tgUserId,
            InboxItemStatus.ACTIVE,
            pageable
        );
    }

    @Transactional(readOnly = true)
    public InboxItem getInboxItem(Long id, Long tgUserId) {
        InboxItem inboxItem =
            inboxItemRepository.findByIdAndUser_tgUserId(id, tgUserId)
                .orElseThrow(() -> new InboxItemNotFoundException(id));

        return inboxItem;
    }

}
