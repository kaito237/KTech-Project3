package com.example.KTech_Project3.service;

import com.example.KTech_Project3.constant.OrderStatus;
import com.example.KTech_Project3.dto.ProductsDto;
import com.example.KTech_Project3.dto.ShopDto;
import com.example.KTech_Project3.entity.Products;
import com.example.KTech_Project3.entity.Shop;
import com.example.KTech_Project3.entity.UserEntity;
import com.example.KTech_Project3.repo.ProductsRepository;
import com.example.KTech_Project3.repo.ShopRepository;
import com.example.KTech_Project3.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final ProductsRepository productsRepository;

    // 쇼핑몰 정보 수정
    public ShopDto updateShop(Long shopId, ShopDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            Shop shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            // 로그인한 사용자가 쇼핑몰의 주인인지 확인하기
            if (!user.getShops().contains(shop)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }

            shop.setShopName(dto.getShopName());
            shop.setIntroduction(dto.getIntroduction());
//            shop.setCategory(dto.getCategory());

            return ShopDto.fromEntity(shopRepository.save(shop));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // TODO 문제점: 신청하는 건데 새로운 쇼핑몰이 개설된다.
    // 쇼핑몰 개설 신청
    public ShopDto requestOpen(ShopDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            // 쇼핑몰의 정보가 모두 작성되어있는지 확인
            log.info("실행 확인");
            Shop shop = Shop.builder()
                    .shopName(dto.getShopName())
                    .introduction(dto.getIntroduction())
//                    .category(dto.getCategory())
                    .build();

            user.getShops().add(shop);
            userRepository.save(user);

            return ShopDto.fromEntity(shopRepository.save(shop));

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // 개설 신청 목록
    public List<ShopDto> shopList() {
        return shopRepository.findAll().stream()
                .map(ShopDto::fromEntity)
                .toList();
    }


    // 개설 허가, 불가
    public ShopDto refusal(Long shopId, ShopDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);
        if (optionalUser.isPresent()) {

            Optional<Shop> optionalShop = shopRepository.findById(shopId);
            if (optionalShop.isPresent()) {
                Shop shop = optionalShop.get();
                if (dto.getShopResponse().equals("허가")) {
                    shop.setShopResponse(dto.getShopResponse());
                    shop.setShopStatus(OrderStatus.OPEN.name());

                    return ShopDto.fromEntity(shopRepository.save(shop));
                } else if (dto.getShopResponse().equals("불가") && dto.getReason() != null) {
                    shop.setShopResponse(dto.getShopResponse());
                    shop.setReason(dto.getReason());

                    return ShopDto.fromEntity(shopRepository.save(shop));
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // TODO 문제점: 쇼핑몰 신청 요청과 같은 문제
    // 쇼핑몰 페쇄 요청
    public ShopDto requestDel(Long shopId, ShopDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            Optional<Shop> optionalShop = shopRepository.findById(shopId);
            if (optionalShop.isPresent()) {
                if (dto.getDeleteReason() != null) {
                    Shop shop = Shop.builder()
                            .deleteReason(dto.getDeleteReason())
                            .build();

                    user.getShops().add(shop);
                    userRepository.save(user);

                    return ShopDto.fromEntity(shopRepository.save(shop));
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // 페쇄 수락
    public void deleteShop(Long shopId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);
        if (optionalUser.isPresent()) {
            shopRepository.deleteById(shopId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // 상품 등록
    public ProductsDto create(Long shopId, ProductsDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            Shop shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            // 로그인한 사용자가 쇼핑몰의 주인인지 확인하기
            if (!user.getShops().contains(shop)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }

            Products products =Products.builder()
                    .name(dto.getName())
                    .image(dto.getImage())
                    .description(dto.getDescription())
                    .price(dto.getPrice())
                    .stock(dto.getStock())
                    .shop(shop)
                    .build();

            return ProductsDto.fromEntity(productsRepository.save(products));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // 상품 수정
    public ProductsDto update(Long shopId, Long goodsId, ProductsDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            Shop shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            if (!user.getShops().contains(shop)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }

            Products products = productsRepository.findById(goodsId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            products.setName(dto.getName());
            products.setImage(dto.getImage());
            products.setDescription(dto.getDescription());
            products.setPrice(dto.getPrice());
            products.setStock(dto.getStock());

            return ProductsDto.fromEntity(productsRepository.save(products));

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // 상품 삭제
    public void delete(Long shopId, Long goodsId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            Shop shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            if (!user.getShops().contains(shop)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }

            productsRepository.deleteById(goodsId);

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // TODO 조건 없을 경우 최근 거래가 있던 순으로 조회가 문제
    // 쇼핑몰 조회
    public List<ShopDto> search(String shopName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);
        if (optionalUser.isPresent()) {

            List<Shop> shops;
            if (shopName != null ) {
                shops = shopRepository.findByShopName(shopName);
            } else {
                shops = shopRepository.findAllByOrderByIdDesc();
            }
            return shops.stream()
                    .map(ShopDto::fromEntity)
                    .toList();

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}
