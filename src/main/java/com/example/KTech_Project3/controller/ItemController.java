package com.example.KTech_Project3.controller;

import com.example.KTech_Project3.dto.ItemDto;
import com.example.KTech_Project3.dto.OfferDto;
import com.example.KTech_Project3.exception.AuthenticationFailedException;
import com.example.KTech_Project3.exception.ItemNotFoundException;
import com.example.KTech_Project3.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

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

    // 중고 거래 상품 등록
    @PostMapping("/register")
    public String registerItem(@RequestBody ItemDto itemDto) {
        String username = getCurrentUsername();
        return itemService.registerItem(itemDto, username);
    }

    // 등록된 물품 정보 보기
    @GetMapping("/itemAllList")
    public List<ItemDto> ItemAllList() {
        return itemService.itemAllList();
    }

    // 중고거래 상품 업데이트
    @PostMapping("/update")
    public String updateItem(
            @RequestParam String title,
            @RequestBody ItemDto itemDto
    ){
        String username = getCurrentUsername();
        return itemService.updateItem(itemDto, username, title);
    }

    // 중고거래 상품 삭제
    @PostMapping("/delete")
    public String deleteItem (@RequestParam String title){
        String username = getCurrentUsername();
        return itemService.deleteItem(title, username);
    }

    // 중고거래 상품 거래 제안 :
    @PostMapping("/buyRequest")
    public String buyRequestItem(
            @RequestParam String title,
            @RequestBody OfferDto offerDto
    ) {
        String username = getCurrentUsername();
        return itemService.buyRequestItem(title, offerDto, username);
    }

    // 중고거래 제안 읽기
    @GetMapping("/offer/read")
    public List<OfferDto> readOffer(
            @RequestParam String title
    ) throws ItemNotFoundException {
        String username = getCurrentUsername();
        return itemService.readOffer(title, username);
    }

    // 중고거래 제안 수락/거절
    @PostMapping("/offer/accept-refuse")
    public String offerAcceptRefuse(
            @RequestParam String title,
            @RequestParam String offerUser,
            @RequestParam Long price,
            @RequestParam String acceptRefuse
    ) {
        String username = getCurrentUsername();
        return itemService.offerAcceptRefuse(title, offerUser, price, username, acceptRefuse);
    }

    // 중고거래 제안 확인
    @PostMapping("/offer/confirm")
    public String offerConfirm(
            @RequestParam String title
    ) {
        String username = getCurrentUsername();
        return itemService.offerConfirm(title, username);
    }

    // 중고거래 물품 대표 이미지 등록
    @PutMapping(
            value = "itemImg",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )public String itemImg(
            @RequestParam("file")
            MultipartFile file,
            @RequestParam("title")
            String title
    ) {
        String username = getCurrentUsername();
        return itemService.itemImg(username, file, title);

    }

}
