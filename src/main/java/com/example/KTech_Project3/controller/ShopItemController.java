package com.example.KTech_Project3.controller;

import com.example.KTech_Project3.dto.ShopItemDto;
import com.example.KTech_Project3.exception.AuthenticationFailedException;
import com.example.KTech_Project3.service.ShopItemService;
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
@RequestMapping("shop/items")
@RequiredArgsConstructor
public class ShopItemController {
    private final ShopItemService shopItemService;

    // 현재 인증된 사용자 정보를 가져오는 메서드
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        } else {
            throw new AuthenticationFailedException("Authentication failed. Invalid username or password.");
        }
    }

    // 쇼핑몰에 상품 등록
    @PostMapping("/register")
    public String registerShopItem (@RequestBody ShopItemDto shopItemDto) {
        String username = getCurrentUsername();
        return shopItemService.registerShopItem(shopItemDto, username);
    }

    // 상품 수정
    @PostMapping("/update")
    public String updateShopItem(
            @RequestParam String name,
            @RequestBody ShopItemDto shopItemDto
    ) {
        String username = getCurrentUsername();
        return shopItemService.updateShopItem(shopItemDto, username, name);
    }

    // 상품 삭제
    @PostMapping("/delete")
    public String deleteShopItem (
            @RequestParam String name
    ) {
        String username = getCurrentUsername();
        return shopItemService.deleteShopItem(name, username);
    }

    // 쇼핑몰 상품 검색
    // 이름,가격 범위를 기준으로 상품 검색, 조회되는 상품이 등록된 쇼핑몰에 대한 정보가 함께 제공
    @GetMapping("/search")
    public List<Object[]> searchShopItem(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice
    ) {
        return shopItemService.searchShopItem(name, minPrice, maxPrice);
    }

    // 쇼핑몰 상품 구매 요청
    @PostMapping("/buyRequest")
    public String buyRequestShopItem(
            @RequestParam String name,
            @RequestParam String shopName,
            @RequestParam Integer amount
    ) {
        // 상품(name)과 구매 수량을(amount) 기준으로 구매 요청
        return shopItemService.buyRequest(name, shopName, amount);
    }

    // username으로 주문 찾고 주문이 있으면 금액 전송
    @PostMapping("/buyRequest/sendMoney")
    public String sendMoney(
            @RequestParam Integer totalPrice
    ) {
        String username = getCurrentUsername();
        return shopItemService.sendMoney(totalPrice, username);
    }

    //쇼핑몰 주인이 orderShopItem의  totalPrice 확인, 비어있지 않고 status = "구매 요청"이라면,
    // status를 "요청 수락"으로 바꾸고 orderShopItem에 있는 amount만큼 재고를 감소시키기
    @GetMapping("/buyRequest/check")
    public String requestCheck() {
        String username = getCurrentUsername();
        return shopItemService.requestCheck(username);
    }

    // 구매 요청 수락 전 - 구매 요청 취소 / 수락 후 - 구매 요청 취소 불가능
    // 구매 취소 할 아이템과 샵 이름 포함
    @GetMapping("/buyRequest/cancel")
    public String requestCancel(
            @RequestParam String itemName,
            @RequestParam String shopName
    ) {
        String username = getCurrentUsername();
        return shopItemService.requestCancel(itemName, shopName, username);
    }

    @PutMapping(
            value = "itemImg",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )public String shopItemImg(
            @RequestParam("file")
            MultipartFile file,
            @RequestParam("shopItemName")
            String shopItemName,
            @RequestParam("shopName")
            String shopName
    ){
        String username = getCurrentUsername();
        return shopItemService.shopItemImg(username, file, shopItemName, shopName);
    }
}