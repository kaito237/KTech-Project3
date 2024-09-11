package com.example.KTech_Project3.service;

import com.example.KTech_Project3.FileHandlerUtils;
import com.example.KTech_Project3.dto.ShopItemDto;
import com.example.KTech_Project3.entity.OrderShopItem;
import com.example.KTech_Project3.entity.Shop;
import com.example.KTech_Project3.entity.ShopItem;
import com.example.KTech_Project3.entity.UserEntity;
import com.example.KTech_Project3.repo.OrderShopItemRepository;
import com.example.KTech_Project3.repo.ShopItemRepository;
import com.example.KTech_Project3.repo.ShopRepository;
import com.example.KTech_Project3.repo.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ShopItemService {
    private final ShopItemRepository shopItemRepository;
    private final ShopRepository shopRepository;
    private final OrderShopItemRepository orderShopItemRepository;
    private final UserRepository userRepository;
    private final FileHandlerUtils fileHandlerUtils;

    public String registerShopItem (ShopItemDto shopItemDto, String username) {
        // 사용자 이름으로 상점을 찾음
        Optional<Shop> optionalShop = shopRepository.findByUserUsername(username);

        if (optionalShop.isPresent()) {
            Shop shop = optionalShop.get();

            // 새로운 상품 생성
            ShopItem shopItem = ShopItem.builder()
                    .name(shopItemDto.getName())
                    .description(shopItemDto.getDescription())
                    .price(shopItemDto.getPrice())
                    .stock(shopItemDto.getStock())
                    .shop(shop) // 상점 정보 설정
                    .build();

            // 상점 저장 (이 때 상품도 함께 저장됨)
            shopItemRepository.save(shopItem);

            return "상품 등록 완료";
        } else {
            return "사용자의 상점을 찾을 수 없습니다.";
        }
    }

    // 상품 정보 수정
    @Transactional
    public String updateShopItem(ShopItemDto shopItemDto, String username, String name) {
        // 사용자 이름으로 상점을 찾음
        Optional<Shop> optionalShop = shopRepository.findByUserUsername(username);

        if (optionalShop.isPresent()) {
            Shop shop = optionalShop.get();

            // 해당 상점의 아이템을 찾음
            Optional<ShopItem> optionalShopItem = shopItemRepository.findByShopAndName(shop, name);

            if (optionalShopItem.isPresent()) {
                ShopItem shopItem = optionalShopItem.get();

                // 새로운 정보로 엔티티 업데이트
                shopItem.setName(shopItemDto.getName());
                shopItem.setDescription(shopItemDto.getDescription());
                shopItem.setPrice(shopItemDto.getPrice());
                shopItem.setStock(shopItemDto.getStock());

                // 엔티티 저장
                shopItemRepository.save(shopItem);

                return "상품 정보가 업데이트되었습니다.";
            } else {
                return "해당 상품을 찾을 수 없습니다.";
            }
        } else {
            return "사용자의 상점을 찾을 수 없습니다.";
        }
    }

    // 쇼핑몰 상품 삭제
    public String deleteShopItem (String name, String username) {
        // name은 shptiem의 이름, username은 shop의 소유자
        // 사용자 이름을 기반으로 해당 사용자의 쇼핑몰을 찾음
        Optional<Shop> optionalShop = shopRepository.findByUserUsername(username);

        if (optionalShop.isPresent()) {
            // 쇼핑몰을 찾음
            Shop shop = optionalShop.get();
            // 쇼핑몰에 속한 상품을 찾아서 삭제
            Optional<ShopItem> optionalShopItem = shopItemRepository.findByShopAndName(shop, name);
            if (optionalShopItem.isPresent()) {
                // 상품을 찾고 삭제
                ShopItem shopItem = optionalShopItem.get();
                shopItemRepository.delete(shopItem);
                return "쇼핑몰 상품 삭제 완료";
            } else {
                return "해당 이름의 상품을 찾을 수 없습니다.";
            }
        } else {
            return "해당 사용자의 쇼핑몰을 찾을 수 없습니다.";
        }
    }

    // 상품검색
    // 이름,가격 범위를 기준으로 상품 검색, 조회되는 상품이 등록된 쇼핑몰에 대한 정보가 함께 제공
    public List<Object[]> searchShopItem(String name, Integer minPrice, Integer maxPrice) {
        List<ShopItem> foundItems;

        if (name != null && minPrice != null && maxPrice != null) {
            // 이름과 가격 범위로 상품 검색
            foundItems = shopItemRepository.findByNameContainingAndPriceBetween(name, minPrice, maxPrice);
        } else if (name != null) {
            // 이름으로만 상품 검색
            foundItems = shopItemRepository.findByNameContaining(name);
        } else if (minPrice != null && maxPrice != null) {
            // 가격 범위로 상품 검색
            foundItems = shopItemRepository.findByPriceBetween(minPrice, maxPrice);
        } else {
            // 모든 상품 검색
            foundItems = shopItemRepository.findAll();
        }

        // 검색된 상품 및 쇼핑몰 정보를 함께 반환
        List<Object[]> result = new ArrayList<>();
        for (ShopItem item : foundItems) {
            Object[] itemWithShopInfo = new Object[6];
            itemWithShopInfo[0] = "상품 이름: " + item.getName();
            itemWithShopInfo[1] = "상품 가격: " + item.getPrice();
            itemWithShopInfo[2] = "상품 소개: " + item.getDescription();
            itemWithShopInfo[3] = "쇼핑몰 이름: " + item.getShop().getName();
            itemWithShopInfo[4] = "쇼핑몰 소개: " + item.getShop().getIntroduction();
            itemWithShopInfo[5] = "쇼핑몰 분류: " + item.getShop().getCategory();
            result.add(itemWithShopInfo);
        }
        return result;
    }

    // 쇼핑몰 상품 구매 요청
    public String buyRequest(String name,String shopName, Integer amount) {
        Optional<Shop> shop = shopRepository.findByName(shopName);
        // shopItem 에서 name과 amount로 검색,
        // 1. 상품 이름과 쇼핑몰 id로 상품 검색
        Optional<ShopItem> optionalShopItem = shopItemRepository.findByNameAndShopId(name,shop.get().getId() );

        if (optionalShopItem.isPresent()) {
            ShopItem shopItem = optionalShopItem.get();

            // 2. 검색된 상품이 있으면 수량 비교
            if (shopItem.getStock() >= amount) {
                // 현재 사용자 정보를 가져오는 방식으로 수정
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    String username = userDetails.getUsername();

                    // userRepository를 사용하여 현재 사용자의 정보를 가져옴
                    Optional<UserEntity> optionalCurrentUser = userRepository.findByUsername(username);

                    if (optionalCurrentUser.isPresent()) {
                        UserEntity currentUser = optionalCurrentUser.get();

                        // 3. 주문 생성
                        OrderShopItem orderShopItem = OrderShopItem.builder()
                                .shop(shopItem.getShop())
                                .shopItem(shopItem)
                                .amount(amount)
                                // total price는 구매 요청 후 금액 전달할 예정
                                .status("구매 요청")
                                .user(currentUser) // 현재 사용자 객체의 id
                                .dateTime(LocalDateTime.now())
                                .build();

                        // 주문 저장
                        orderShopItemRepository.save(orderShopItem);

                        return "구매 요청이 완료되었습니다." + shopItem.getPrice() * amount + "원 입니다.";
                    }
                    else {
                        return "사용자 정보를 찾을 수 없습니다. 구매 요청을 실패했습니다.";
                    }
                } else {
                    return "사용자 정보를 찾을 수 없습니다.";
                }
            } else {
                // 4. 주문 불가능한 경우
                return "수량이 부족합니다.";
            }
        } else {
            // 4. 상품이 없는 경우
            return "일치하는 상품이 없습니다.";
        }
    }

    // 주문에 맞는 금액 송금
    public String sendMoney(Integer totalPrice, String username){
        // username으로 아래의 조건을 만족하는 주문 내역 가져오기
        Optional<OrderShopItem> optionalLatestOrder = orderShopItemRepository.findFirstByUserUsernameAndStatusOrderByIdDesc(username, "구매 요청");

        // 가장 최근의 주문 내역 가져오기
        if (optionalLatestOrder.isPresent()) {
            OrderShopItem latestOrder = optionalLatestOrder.get();

            // 주문 내역의 총 주문 금액 계산
            Integer totalOrderPrice = latestOrder.getAmount() * latestOrder.getShopItem().getPrice();

            if (totalPrice.compareTo(totalOrderPrice) >= 0) {
                // 금액이 일치할 경우 송금 성공
                // totalPrice 저장
                latestOrder.setTotalPrice(totalPrice);
                orderShopItemRepository.save(latestOrder);

                return "금액을 보냈습니다.";
            } else {
                return "금액이 부족합니다. 주문 금액은 " + totalOrderPrice + " 입니다.";
            }
        } else {
            // "구매 요청" 상태인 주문 내역이 없을 경우
            return "구매 요청 중인 주문 내역이 없습니다.";
        }
    }

    public String requestCheck(String username) {
        // username을 가진 사용자의 쇼핑몰 찾음
        Optional<Shop> optionalShop = shopRepository.findByUserUsername(username);

        if (optionalShop.isPresent()) {
            Shop shop = optionalShop.get();

            // username을 가진 쇼핑몰의 id를 가진 orderShopItem 찾음
            List<OrderShopItem> orderShopItems = orderShopItemRepository.findByShopIdAndStatus(shop.getId(), "구매 요청");

            // 주문 내역이 없을 경우
            if (orderShopItems.isEmpty()) {
                return "구매 요청이 없습니다.";
            }

            // totalPirce가 null아 이나고 status 가 "구매 요청" 일 경우
            for (OrderShopItem orderShopItem : orderShopItems) {
                if (orderShopItem.getTotalPrice() != null) {
                    // status를 "요청 수락" 으로 바꾸고,
                    orderShopItem.setStatus("요청 수락");
                    orderShopItemRepository.save(orderShopItem);

                    // orderShopItem의 amount 만큼 username을 가진 쇼핑몰의 shop item id의 stock을 감소시킴
                    ShopItem shopItem = orderShopItem.getShopItem();
                    if (shopItem != null) {
                        Integer amount = orderShopItem.getAmount();
                        if (amount != null && amount > 0) {
                            shopItem.setStock(shopItem.getStock() - amount);
                            shopItemRepository.save(shopItem);
                        }
                    }
                }
                else{
                    return "금액이 입금되지 않았습니다.";
                }
            }
            return "구매 요청을 수락하고 재고를 갱신했습니다.";
        } else {
            return "해당 사용자가 소유한 쇼핑몰이 없습니다.";
        }
    }

    // 구매 요청 취수
    public String requestCancel(String itemName, String shopName, String username) {
        // username으로 orderShopItem에서 주문 내역 검색
        // 주문 내역이 있을 경우
        // status가 "구매 요청" 이라면 totalPrice를 null로 바꾸고, status를 "구매 취소"로 바꿈
        // status가 "요청 수락" 이라면 "요청이 수락되어 구매를 취소할 수 없습니다" 출력
        // 주문 내역이 없을 경우
        // username + "주문 내역이 없습니다." 출력

        // username, itemName, shopName으로 orderShopItem에서 주문 내역 검색
        // 1. shop name에 해당하는 shop 찾기
        Optional<Shop> shop = shopRepository.findByName(shopName);
        // 2, shop item에서 itemname에 해당하는 shop item 찾기
        Optional<ShopItem> shopItem = shopItemRepository.findByNameAndShopId(itemName, shop.get().getId());

        // 주문 내역에서 찾기 ( shop id , shop item id, user id) 를 만족해야 함
        // username을 가진 회원 찾기
        UserEntity user = userRepository.findIdByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        List<OrderShopItem> orderShopItems = orderShopItemRepository.findByShopIdAndShopItemIdAndUserId(shop.get().getId(), shopItem.get().getId(), user.getId());

        // 주문 내역이 있을 경우
        if (!orderShopItems.isEmpty()) {
            for (OrderShopItem orderShopItem : orderShopItems) {
                // status가 "구매 요청" 이라면 totalPrice를 null로 바꾸고, status를 "구매 취소"로 바꿈
                if ("구매 요청".equals(orderShopItem.getStatus())) {
                    orderShopItem.setTotalPrice(0);
                    orderShopItem.setStatus("구매 취소");
                    orderShopItemRepository.save(orderShopItem);
                } else if ("요청 수락".equals(orderShopItem.getStatus())) {
                    // status가 "요청 수락" 이라면 "요청이 수락되어 구매를 취소할 수 없습니다" 출력
                    return "요청이 수락되어 구매를 취소할 수 없습니다.";
                }
            }return "구매 요청을 취소하였습니다.";
        } else {
            // 주문 내역이 없을 경우
            return username + " 사용자의 해당 주문 내역이 없습니다.";
        }
    }

    public String shopItemImg(String username, MultipartFile file, String shopItemName, String shopName) {
        // shopName으로 shop 찾기
        Optional<Shop> optionalShop = shopRepository.findByName(shopName);
        // username으로 user 찾기
        UserEntity user = userRepository.findIdByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        if (optionalShop.isEmpty())
            return "해당하는 쇼핑몰이 존재하지 않습니다.";


        if (!Objects.equals(optionalShop.get().getUser().getId(), user.getId()))
            return "해당하는 쇼핑몰의 사업자만 이미지 등록이 가능합니다.";

        Shop shop = optionalShop.get();
        Optional<ShopItem> optionalShopItem = shopItemRepository.findByNameAndShopId(shopItemName, shop.getId());
        ShopItem shopItem = optionalShopItem.get();

        String requestPath = fileHandlerUtils.saveFile(String.format("shopItem/%d/", shopItem.getId()),
                "shop-itemImg",file);

        shopItem.setShopItemImg(requestPath);
        shopItemRepository.save(shopItem);
        return "쇼핑몰 상품 이미지 등록 성공";



    }
}