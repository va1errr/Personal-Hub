package com.va1err.personalhub.api.inbox;

import com.va1err.personalhub.inbox.application.InboxService;
import com.va1err.personalhub.inbox.domain.InboxItem;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inbox")
public class InboxController {

    private final InboxService inboxService;

    public InboxController(InboxService inboxService) {
        this.inboxService = inboxService;
    }

    @PostMapping
    public InboxItemResponse addInboxItem(@Valid @RequestBody AddInboxItemRequest request) {
        InboxItem inboxItem = inboxService.addInboxItem(
            request.tgUserId(),
            request.content()
        );

        return InboxItemMapper.toResponse(inboxItem);
    }

}
