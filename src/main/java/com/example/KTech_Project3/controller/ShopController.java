package com.example.KTech_Project3.controller;

import com.example.KTech_Project3.dto.ShopDto;
import com.example.KTech_Project3.entity.ShopCategory;
import com.example.KTech_Project3.exception.AuthenticationFailedException;
import com.example.KTech_Project3.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("shops")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    // 현재 인증된 사용자 정보를 가져오는 메서드
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        } else {
            throw new AuthenticationFailedException("인증 실패: 사용자 이름이나 비밀번호가 잘못되었습니다.");
        }
    }

    // 쇼핑몰 생성은 business 사용자가 되면 자동 생성
    // 쇼핑몰 정보 작성자 수정
    @PostMapping("/update")
    public String updateShop (
            @RequestBody ShopDto shopDto
    ) {
        String username = getCurrentUsername();
        return shopService.updateShop(shopDto, username);
    }

    // 쇼핑몰 개설 신청
    @PostMapping("/apply/open")
    public String shopApplyOpen (){
        String username = getCurrentUsername();
        return shopService.shopApplyOpen(username);
    }

    // 관리자, 개설 신청자(자기 쇼핑몰) - 개설 신청된 쇼핑몰 목록 확인
    @GetMapping("/apply/read")
    public List<ShopDto> applyRead() {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 사용자 ROLE 확인
            if (userDetails.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {

                // ROLE이 ADMIN일 경우 바로 목록 조회
                String username = "admin";
                return shopService.applyRead(username);
            }
            else if (userDetails.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_BUSINESS"))) {

                // ROLE이 BUSINESS일 경우 자기 쇼핑몰만 조회
                String username = userDetails.getUsername();
                return shopService.applyRead(username);
            } else {
                // 다른 권한을 가진 경우 처리
                throw new AuthenticationFailedException("접근이 승인되지 않았습니다.");
            }
        } else {
            // 인증되지 않은 경우 에러 처리
            throw new AuthenticationFailedException("인증이 필요합니다.");
        }
    }

    // 관리자 개설신청 허가/ 불허가
    @PostMapping("/apply/open/acceptRefuse")
    public String acceptRefuse(
            @RequestBody ShopDto shopDto
    ) {
        return shopService.acceptRefuse(shopDto);
    }

    // 쇼핑몰 폐쇄 신청
    @PostMapping("/apply/close")
    public String ApplyClose(
            @RequestBody ShopDto shopDto
    ) {
        String username = getCurrentUsername();
        return shopService.shopApplyClose(username, shopDto);
    }

    // 관리자 - 폐쇄 요청 확인
    @GetMapping("/apply/close/read")
    public List<ShopDto> applyCloseRead() {
        return shopService.applyCloseRead();
    }

    // 관리자 - 폐쇄 요청 수락
    @PostMapping("/apply/close/acceptRefuse")
    public String closeAcceptRefuse(
            @RequestBody ShopDto shopDto
    ){
        return shopService.closeAcceptRefuse(shopDto);
    }

    // 쇼핑몰 조회
    @GetMapping("/search")
    public List<ShopDto> searchShop (
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ShopCategory category
    ) {
        // 조건 (이름 , 쇼핑몰 분류(category))
        // 1. 조건 없이 조회할 경우,  가장 최근 거래가 있던 쇼핑몰 순서로 조회
        // 2. 조건이 있을 경우 조건으로 쇼핑몰 검색
        return shopService.searchShop(name, category);
    }
}