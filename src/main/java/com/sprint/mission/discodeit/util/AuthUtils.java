package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.security.CustomUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

public class AuthUtils {

    public static boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
            authentication.isAuthenticated() &&
            !(authentication instanceof AnonymousAuthenticationToken);
    }

    public static UUID getCurrentUserId() {
        if (!isLoggedIn()) {
            throw new RestException(ResultCode.UNAUTHORIZED_USER);
        }
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        return userDetails.getUser().getId();
    }

    public static User getCurrentUser() {
        if (!isLoggedIn()) {
            throw new RestException(ResultCode.UNAUTHORIZED_USER);
        }
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        return userDetails.getUser();
    }

}
