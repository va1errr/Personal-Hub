package com.va1err.personalhub.inbox.application;

import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.inbox.infrastructure.InboxItemRepository;
import com.va1err.personalhub.shared.exception.UserNotFoundException;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.infrastructure.UserRepository;
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
            .orElseThrow(() -> new UserNotFoundException(tgUserId));

        InboxItem inboxItem = InboxItem.add(
            user,
            content
        );

        return inboxItemRepository.save(inboxItem);
    }

}
