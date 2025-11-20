package com.todate.backend.user.service;

import com.todate.backend.course.repository.UserCourseRepository;
import com.todate.backend.user.domain.Relationship;
import com.todate.backend.user.domain.User;
import com.todate.backend.user.dto.request.PatchUserRequest;
import com.todate.backend.user.dto.response.UserResponse;
import com.todate.backend.user.repository.RelationshipRepository;
import com.todate.backend.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RelationshipRepository relationshipRepository;
    private final UserCourseRepository userCourseRepository;

    public UserResponse getUser(String userId) {
        User user = findUser(userId);
        long numOfCourses = userCourseRepository.countByUser(user);
        String partner = relationshipRepository.findByPartner1UsernameOrPartner2Username(userId,
                userId)
            .map(relationship -> extractPartnerUsername(relationship, userId))
            .orElse(null);

        return UserResponse.of(user.getName(), partner, numOfCourses);
    }

    @Transactional
    public void updateUser(PatchUserRequest request) {
        User user = findUser(request.getUserId());

        boolean hasChange = false;
        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
            hasChange = true;
        }

        if (request.getPartner() != null) {
            updateRelationship(user, request.getPartner());
            hasChange = true;
        }

        if (!hasChange) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "변경할 정보가 없습니다.");
        }
    }

    private void updateRelationship(User user, String partnerId) {
        if (!StringUtils.hasText(partnerId)) {
            removeRelationship(user.getUsername());
            return;
        }

        if (user.getUsername().equals(partnerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인을 파트너로 지정할 수 없습니다.");
        }

        User partner = findUser(partnerId);
        Optional<Relationship> currentRelationship = relationshipRepository
            .findByPartner1UsernameOrPartner2Username(user.getUsername(), user.getUsername());

        if (currentRelationship.isPresent()
            && isSamePair(currentRelationship.get(), user.getUsername(), partner.getUsername())) {
            return;
        }

        removeRelationship(user.getUsername());
        removeRelationship(partner.getUsername());

        Relationship relationship = new Relationship();
        relationship.setPartner1(user);
        relationship.setPartner2(partner);
        relationshipRepository.save(relationship);
    }

    private boolean isSamePair(Relationship relationship, String username, String partnerId) {
        String partnerUsername = extractPartnerUsername(relationship, username);
        return partnerId.equals(partnerUsername);
    }

    private void removeRelationship(String username) {
        relationshipRepository.findByPartner1UsernameOrPartner2Username(username, username)
            .ifPresent(relationshipRepository::delete);
    }

    private User findUser(String userId) {
        return userRepository.findByUsername(userId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    private String extractPartnerUsername(Relationship relationship, String userId) {
        if (relationship.getPartner1().getUsername().equals(userId)) {
            return relationship.getPartner2().getUsername();
        }
        return relationship.getPartner1().getUsername();
    }
}
