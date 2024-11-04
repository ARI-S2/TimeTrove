package com.timetrove.Project.service;


import com.timetrove.Project.common.enumType.ErrorCode;
import com.timetrove.Project.common.exception.EntityNotFoundException;
import com.timetrove.Project.domain.User;

import com.timetrove.Project.dto.user.UserDto;
import com.timetrove.Project.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private final UserRepository userRepository;
    private static final String USER_CACHE_NAME = "userCache";

    /**
     * 사용자 정보 조회 메서드 (캐시 사용)
     * @param: Long userCode - 사용자 코드
     * @return UserDto
     */
    @Cacheable(value = USER_CACHE_NAME, key = "#userCode", unless = "#result == null")
    public UserDto getUserById(Long userCode) {
        log.debug("Cache miss 발생한 사용자: {}", userCode);
        return UserDto.convertUserToDto(findUserByIdOrThrow(userCode));
    }

    /**
     * 사용자 정보 수정 메서드
     * @param: Long userCode - 사용자 코드
     * @param: MultipartFile profileImage - 프로필 이미지 파일
     * @param: String kakaoNickname - 카카오 닉네임
     * @return 수정된 UserDto
     */
    @Transactional
    @CachePut(value = USER_CACHE_NAME, key = "#userCode")
    public UserDto updateUserInfo(Long userCode, MultipartFile profileImage, String kakaoNickname) {
        User user = findUserByIdOrThrow(userCode);

        boolean isUpdated = false;

        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImageUrl = saveProfileImage(profileImage);
            user.setKakaoProfileImg(profileImageUrl);
            isUpdated = true;
        }

        if (!Objects.equals(user.getKakaoNickname(), kakaoNickname)) {
            user.setKakaoNickname(kakaoNickname);
            isUpdated = true;
        }

        if (isUpdated) {
            user = userRepository.save(user);
        }

        return UserDto.convertUserToDto(user);
    }

    /**
     * 사용자 캐시 삭제 메서드
     * @param: Long userCode - 사용자 코드
     */
    @CacheEvict(value = USER_CACHE_NAME, key = "#userCode")
    public void deleteUserCache(Long userCode) {
        log.debug("사용자에 대한 {} 캐시 삭제 ", userCode);
    }

    /**
     * 프로필 이미지 저장 메서드
     * @param: MultipartFile profileImage - 프로필 이미지 파일
     * @return 저장된 파일 경로 문자열
     */
    private String saveProfileImage(MultipartFile profileImage) {
        try {
            String folder = "uploads/";
            Path folderPath = Paths.get(folder);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            String fileName = UUID.randomUUID() +
                    getFileExtension(profileImage.getOriginalFilename());
            Path filePath = folderPath.resolve(fileName);
            Files.write(filePath, profileImage.getBytes());

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    /**
     * 파일 확장자 가져오는 메서드
     * @param: String fileName - 파일명
     * @return 파일 확장자 문자열
     */
    private String getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".")))
                .orElse("");
    }

    /**
     * 사용자 프로필 이미지 바이너리 데이터 반환 메서드
     * @param: Long userCode - 사용자 코드
     * @return 프로필 이미지 바이너리 데이터
     * @throws IOException 파일 읽기 실패 시 예외 발생
     */
    public byte[] getUserProfileImage(Long userCode) throws IOException {
        User user = findUserByIdOrThrow(userCode);
        String profileImagePath = user.getKakaoProfileImg();
        if (profileImagePath != null && !profileImagePath.isEmpty()) {
            Path imagePath = Paths.get(profileImagePath);

            if (Files.exists(imagePath)) {
                return Files.readAllBytes(imagePath);
            }
        }
        return new byte[0];
    }

    /**
     * 사용자 조회 메서드
     * 사용자가 없을 시 예외 발생 (USER_NOT_FOUND)
     * @param: Long userCode - 사용자 코드
     * @return User 객체
     */
    private User findUserByIdOrThrow(Long userCode) {
        return userRepository.findById(userCode)
                .orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));
    }
}