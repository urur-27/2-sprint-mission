package com.sprint.mission.discodeit.security;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionInvalidateManager {

    private final SessionRegistry sessionRegistry;

    public void invalidateIfPresent(UUID userId) {
        // 세션 정보 찾아서 무효화
        List<Object> principals = sessionRegistry.getAllPrincipals();

        for(Object principal : principals) {
            if(principal instanceof CustomUserDetails details
                && details.getUserId().equals(userId)) {

                List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                for(SessionInformation session : sessions) {
                    session.expireNow(); // 현재 세션 강제 만료
                }
            }
        }
    }
}
