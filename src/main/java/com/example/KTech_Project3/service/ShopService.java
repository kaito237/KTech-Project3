package com.example.KTech_Project3.service;

import com.example.KTech_Project3.dto.ShopDto;
import com.example.KTech_Project3.entity.OrderShopItem;
import com.example.KTech_Project3.entity.Shop;
import com.example.KTech_Project3.entity.ShopCategory;
import com.example.KTech_Project3.repo.OrderShopItemRepository;
import com.example.KTech_Project3.repo.ShopRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;
    private final OrderShopItemRepository orderShopItemRepository;

    public String updateShop(ShopDto shopDto, String username) {
        // username을 사용하여 사용자의 쇼핑몰을 찾음
        Optional<Shop> optionalShop = shopRepository.findByUserUsername(username);

        if (optionalShop.isPresent()) {
            Shop shop = optionalShop.get();

            // 쇼핑몰 정보 수정
            shop.setName(shopDto.getName());
            shop.setIntroduction(shopDto.getIntroduction());

            // category가 null이 아니고 빈 문자열이 아니며, enum 값 중 하나와 일치하는지 확인
            if (shopDto.getCategory() != null && !shopDto.getCategory().isEmpty()) {
                try {
                    ShopCategory category = ShopCategory.valueOf(shopDto.getCategory());
                    shop.setCategory(category);
                } catch (IllegalArgumentException e) {
                    return "지원하지 않는 카테고리입니다.";
                }
            } else {
                return "카테고리는 필수 입력 항목입니다.";
            }

            // 저장
            shopRepository.save(shop);

            return "쇼핑몰이 업데이트 되었습니다.";
        } else {
            return username + " 사용자의 쇼핑몰을 찾지 못했습니다.";
        }
    }

    public String shopApplyOpen(String username) {
        // username을 사용하여 사용자의 쇼핑몰을 찾음
        Optional<Shop> optionalShop = shopRepository.findByUserUsername(username);

        if (optionalShop.isPresent()) {
            Shop shop = optionalShop.get();

            // name, introduction, category가 모두 null이 아니고 비어있지 않은 경우에만 개설 신청 상태로 변경
            if (shop.getName() != null && !shop.getName().isEmpty()
                    && shop.getIntroduction() != null && !shop.getIntroduction().isEmpty()
                    && shop.getCategory() != null) {

                shop.setStatus("개설 신청");
                shopRepository.save(shop);
                return "쇼핑몰 개설 신청이 완료되었습니다.";
            } else {
                return "이름, 소개, 카테고리는 비어 있을 수 없습니다.";
            }
        } else {
            return username + " 사용자의 쇼핑몰을 찾지 못했습니다.";
        }
    }

    // 관리자, 개설 신청 쇼핑몰 사용자 - 개설신청 쇼핑몰 목록 읽기
    public List<ShopDto> applyRead(String username) {
        // 쇼핑몰 개설 신청 목록 가져와서 리스트로 저장
        List<Shop> shopList;
        String status1 = "개설 신청";
        String status2 = "오픈";

        // 만약 사용자가 관리자이면 모든 개설 신청 목록을 반환
        if ("admin".equals(username)) {
            shopList = shopRepository.findByStatus(status1);
        } else {
            // 관리자가 아닌 경우 해당 사용자의 쇼핑몰 개설 신청 및 오픈 목록을 반환
            shopList = shopRepository.findByUserUsernameAndStatusIn(username, Arrays.asList(status1, status2));
        }

        // 각 쇼핑몰을 ShopDto로 변환하여 저장할 리스트 생성
        List<ShopDto> shopDtoList = new ArrayList<>();

        // 각 쇼핑몰을 ShopDto로 변환하여 리스트에 추가
        for (Shop shop : shopList) {
            shopDtoList.add(ShopDto.fromEntity(shop));
        }

        return shopDtoList;
    }

    public String acceptRefuse(ShopDto shopDto) {
        // shop name으로 쇼핑몰을 찾음
        Optional<Shop> optionalShop = shopRepository.findByName(shopDto.getName());

        if (optionalShop.isPresent()) {
            Shop shop = optionalShop.get();

            // status가 개설 신청일 경우 허가 또는 불허
            if (shop.getStatus().equals("개설 신청")) {
                // 허가
                if (shopDto.getOpenAcceptRefuse().equals("허가")) {
                    shop.setOpenAcceptRefuse(shopDto.getOpenAcceptRefuse());
                    shop.setRefuseReason(null);
                    shop.setStatus("오픈");
                }
                // 불허, 불허일 경우 이유도 작성
                else if (shopDto.getOpenAcceptRefuse().equals("불허")) {
                    shop.setOpenAcceptRefuse(shopDto.getOpenAcceptRefuse());
                    shop.setRefuseReason(shopDto.getRefuseReason());
                    shopRepository.save(shop);
                    return "쇼핑몰 불허 완료";
                }
            }
            shopRepository.save(shop);
            return "쇼핑몰 허가 완료";
        } else {
            return shopDto.getName() + " 쇼핑몰을 찾지못했습니다.";
        }
    }

    public String shopApplyClose(String username, ShopDto shopDto) {
        // shop name으로 쇼핑몰을 찾음
        Optional<Shop> optionalShop = shopRepository.findByUserUsername(username);

        // shop이 존재한다면 폐쇄요청 신청
        if (optionalShop.isPresent()) {
            Shop shop = optionalShop.get();

            // 폐쇄 요청 사유 없으면 실패
            if (shopDto.getClosureReason() == null || shopDto.getClosureReason().isEmpty()) {
                return "폐쇄 요청 실패: 폐쇄 사유를 작성해야 합니다.";
            }

            shop.setClosureRequest("폐쇄 요청");
            shop.setClosureReason(shopDto.getClosureReason());
            shopRepository.save(shop);
            return "폐쇄 요청이 완료 되었습니다.";
        } else {
            return "폐쇄 요청 실패: " + username+"의 쇼핑몰을 찾을 수 없습니다.";
        }
    }

    // 관리자 - 폐쇄 요청 확인
    public List<ShopDto> applyCloseRead() {
        String closureRequest = "폐쇄 요청";

        // 폐쇄 요청 상태인 쇼핑몰 목록 조회
        List<Shop> shopList = shopRepository.findByClosureRequest(closureRequest);

        // 각 쇼핑몰을 ShopDto로 변환하여 저장할 리스트 생성
        List<ShopDto> shopDtoList = new ArrayList<>();

        // 각 쇼핑몰을 ShopDto로 변환하여 리스트에 추가
        for (Shop shop : shopList) {
            shopDtoList.add(ShopDto.fromEntity(shop));
        }

        return shopDtoList;
    }

    public String closeAcceptRefuse(ShopDto shopDto){
        // name으로 shop 찾기
        Optional<Shop> optionalShop = shopRepository.findByName(shopDto.getName());

        if (optionalShop.isPresent()) {
            Shop shop = optionalShop.get();

            // shop의 closureStatus가 "폐쇄 요청"이라면
            if ("폐쇄 요청".equals(shop.getClosureRequest())) {
                // shop의 closureStatus를 "폐쇄"로 변경
                shop.setClosureRequest("수락");
                shop.setOpenAcceptRefuse("거절");
                shop.setRefuseReason("쇼핑몰의 폐쇄 요청");
                shop.setStatus("폐쇄");
                shopRepository.save(shop);
                return shopDto.getName() + " 스토어의 폐쇄 요청을 수락했습니다.";
            } else {
                return shopDto.getName() + " 스토어의 폐쇄 요청이 아닙니다.";
            }
        } else {
            return "존재하지 않는 스토어입니다.";
        }
    }

    // 쇼핑몰 조회
    public List<ShopDto> searchShop(String name, ShopCategory category) {
        // 1. 이름과 카테고리로 쇼핑몰 검색
        if (name != null && category != null) {
            List<Shop> shops = shopRepository.findByNameAndCategory(name, category);
            return shops.stream()
                    .map(ShopDto::fromEntity) // 올바른 방법으로 수정
                    .collect(Collectors.toList());
        }
        // 2. 이름으로 쇼핑몰 검색 - 성공
        else if (name != null) {
            Optional<List<ShopDto>> shopDtoList = shopRepository.findByName(name)
                    .map(shop -> {
                        List<ShopDto> shopDtos = new ArrayList<>();
                        shopDtos.add(ShopDto.fromEntity(shop));
                        return shopDtos;
                    });
            return shopDtoList.orElse(Collections.emptyList());
        }
        // 3. 카테고리로 쇼핑몰 검색 -성공
        else if (category != null) {
            return shopRepository.findByCategory(category)
                    .stream()
                    .map(ShopDto::fromEntity)
                    .collect(Collectors.toList());
        }
        // 4. 주문 시간(dateTime)을 기준으로 최근 주문 목록 조회
        else {
            List<OrderShopItem> recentOrderShopItems = orderShopItemRepository.findAllByOrderByDateTimeDesc();

            // 주문이 존재하는 경우 해당 주문에 매핑된 쇼핑몰 반환
            if (!recentOrderShopItems.isEmpty()) {
                return recentOrderShopItems.stream()
                        .map(orderShopItemEntity -> ShopDto.fromEntity(orderShopItemEntity.getShopItem().getShop()))
                        .collect(Collectors.toList());
            }
            // 5. 주문이 없는 경우 모든 쇼핑몰 반환
            else {
                return shopRepository.findAll()
                        .stream()
                        .map(ShopDto::fromEntity)
                        .collect(Collectors.toList());
            }
        }
    }
}