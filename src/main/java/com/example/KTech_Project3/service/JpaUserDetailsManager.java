package com.example.KTech_Project3.service;

import com.example.KTech_Project3.FileHandlerUtils;
import com.example.KTech_Project3.entity.CustomUserDetails;
import com.example.KTech_Project3.entity.UserEntity;
import com.example.KTech_Project3.exception.AuthenticationFacade;
import com.example.KTech_Project3.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Service
public class JpaUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;
    private final AuthenticationFacade authFacade;
    private final FileHandlerUtils fileHandlerUtils;


    public JpaUserDetailsManager (
            UserRepository userRepository,
            PasswordEncoder passwordEncoder, AuthenticationFacade authFacade, FileHandlerUtils fileHandlerUtils
    ) {
        this.userRepository = userRepository;
        this.authFacade = authFacade;
        this.fileHandlerUtils = fileHandlerUtils;

        // 이미 관리자 계정이 존재하는지 확인
        if (!userExists("admin")) {
            // 관리자 계정 생성
            createUser(CustomUserDetails.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .authorities("ROLE_ADMIN")
                    .build());
        }
    }

    @Override
    // Spring Security에서 인증을 처리할 때 사용하는 메서드
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser
                = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new UsernameNotFoundException(username);

        UserEntity userEntity = optionalUser.get();
        return CustomUserDetails.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .authorities(userEntity.getAuthorities())
                .build();
    }

    @Override
    // 편의를 위해 같은 규약으로 회원가입을 하는 메서드
    public void createUser(UserDetails user) {
        if (userExists(user.getUsername())) // 이미 같은 이름의 사용자가 있다는 뜻 -> 오류
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        try{
            CustomUserDetails userDetails =
                    (CustomUserDetails) user;
            UserEntity newUser = UserEntity.builder()
                    .username(userDetails.getUsername())
                    .password(userDetails.getPassword())
                    .email(userDetails.getEmail())
                    .phone(userDetails.getPhone())
                    .authorities("ROLE_INACTIVE") // 회원가입만 할 경우 비활성 사용자
                    .build();

            // 사용자가 "admin"일 경우 권한을 "ROLE_ADMIN"으로 설정
            if (newUser.getUsername().equals("admin")) {
                newUser.setAuthorities("ROLE_ADMIN");
            }

            userRepository.save(newUser);
        }catch (ClassCastException e){
            log.error("Failed Cast to: {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateUser(UserDetails user) {
        // 수정하려는 사용자 확인
        if (!userExists(user.getUsername())) {
            throw new UsernameNotFoundException(user.getUsername());
        }

        // 사용자 정보 업데이트
        try {
            CustomUserDetails userDetails = (CustomUserDetails) user;
            UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException(user.getUsername()));

            // 업데이트할 사용자 정보 추출
            String nickname = userDetails.getNickname();
            String name = userDetails.getName();
            Integer age = userDetails.getAge();
            String email = userDetails.getEmail();
            String phone = userDetails.getPhone();

            // 엔티티에 새로운 정보 반영
            userEntity.setNickname(nickname);
            userEntity.setName(name);
            userEntity.setAge(age);
            userEntity.setEmail(email);
            userEntity.setPhone(phone);


            // 사용자의 권한을 ROLE_USER로 변경
            userEntity.setAuthorities("ROLE_USER");

            // 이미 사업자 전환 신청이 수락된 사용자가 수정할 경우 권한 유지
            if (userEntity.getApply() != null  && userEntity.getApply().equals("ACCEPT")){
                userEntity.setAuthorities("ROLE_BUSINESS");
            }

            // 엔티티 저장
            userRepository.save(userEntity);
        } catch (ClassCastException e) {
            log.error("Failed Cast to: {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String profileImg (MultipartFile file, UserDetails user) {
        if (!userExists(user.getUsername())) {
            throw new UsernameNotFoundException(user.getUsername());
        }
        // 사용자 정보 업데이트
        try {
            CustomUserDetails userDetails = (CustomUserDetails) user;
            UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException(user.getUsername()));


            String requestPath = fileHandlerUtils.saveFile(String.format("users/%d/", userEntity.getId()),
                    "profile",file);


            userEntity.setProfileImg(requestPath);
            userRepository.save(userEntity);
            return "프로필 사진 등록 성공";
        }catch (ClassCastException e) {
            log.error("Failed Cast to: {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public void BusinessUser(UserDetails user) {
        // 수정하려는 사용자 확인
        if (!userExists(user.getUsername())) {
            throw new UsernameNotFoundException(user.getUsername());
        }

        // 사용자 정보 업데이트
        try {
            CustomUserDetails userDetails = (CustomUserDetails) user;
            UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException(user.getUsername()));

            String businessNumber = userDetails.getBusinessNumber();
            String apply = userDetails.getApply();
            userEntity.setBusinessNumber(businessNumber);
            userEntity.setApply(apply);

            // userEntity.setAuthorities("ROLE_BUSINESS"); // 승인되면
            userRepository.save(userEntity);
        }catch (ClassCastException e) {
            log.error("Failed Cast to: {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }


    // 나중에 구현
    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }
}