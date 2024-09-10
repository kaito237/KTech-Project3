package com.example.KTech_Project3.service;


import com.example.KTech_Project3.constant.ItemSellStatus;
import com.example.KTech_Project3.constant.Role;
import com.example.KTech_Project3.dto.ItemDto;
import com.example.KTech_Project3.dto.OrderDto;
import com.example.KTech_Project3.entity.Item;
import com.example.KTech_Project3.entity.Order;
import com.example.KTech_Project3.entity.UserEntity;
import com.example.KTech_Project3.repo.ItemRepository;
import com.example.KTech_Project3.repo.OrderRepository;
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
public class ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    // 물품 등록
    public ItemDto create(ItemDto dto) {
        // 현재 로그인한 사용자의 정보(권한) 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            // 권한 바꾸기
            user.setBusinessStatus(Role.ROLE_SELLER.name());
            userRepository.save(user);

            if (user.getBusinessStatus().equals(Role.ROLE_SELLER.name())) {
                Item item = Item.builder()
                        .sellerName(currentUsername)
                        .title(dto.getTitle())
                        .description(dto.getDescription())
                        .price(dto.getPrice())
                        .status(ItemSellStatus.SELL.name())
                        .user(user)
                        .build();

                return ItemDto.fromEntity(itemRepository.save(item));
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // 물품 전체 보기
    public List<ItemDto> readAll() {
        return itemRepository.findAll().stream()
                .map(ItemDto::fromEntity)
                .toList();
    }

    // 물품 하나 보기
    public ItemDto readOne(Long id) {
        return itemRepository.findById(id)
                .map(ItemDto::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // 물품 수정 - 작성자만 가능
    public ItemDto update(Long itemId, ItemDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // User와 N:1관계 제거 필요...
        if (item.getUser().getBusinessStatus().equals(Role.ROLE_SELLER.name())) {
            if (item.getUser().getUsername().equals(currentUsername)) {
                item.setTitle(dto.getTitle());
                item.setDescription(dto.getDescription());
                item.setTitleImage(dto.getTitleImage());
                item.setPrice(dto.getPrice());

                return ItemDto.fromEntity(itemRepository.save(item));
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 물품의 판매자가 아닙니다.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "판매자만 수정할 수 있습니다.");
        }
    }

    // 물품 삭제 - 작성자만 가능
    public void delete(Long itemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (item.getUser().getBusinessStatus().equals(Role.ROLE_SELLER.name())) {
            if (item.getUser().getUsername().equals(currentUsername)) {
                itemRepository.deleteById(itemId);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 물품의 판매자가 아닙니다.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "판매자만 삭제할 수 있습니다.");
        }
    }

    // 구매 제안 등록
    public OrderDto createOffer(Long itemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            // 해당 아이템 정보 가져오기
            Optional<Item> optionalItem = itemRepository.findById(itemId);
            if (optionalItem.isPresent()) {
                Item item = optionalItem.get();

                if (user.getBusinessStatus().equals(Role.ROLE_SELLER.name())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "판매자는 제안할 수 없습니다.");
                }

                user.setBusinessStatus(Role.ROLE_OFFER.name());
                userRepository.save(user);

                log.info("구매 제안자: {}", currentUsername);
                log.info("판매자: {}", item.getSellerName());

                Order offer = Order.builder()
                        .item(item)
                        .itemName(item.getTitle())
                        .offerName(currentUsername)
                        .user(item.getUser())
                        .build();
                // 아이템의 구매제안을 아이템에 추가
                item.getOrders().add(offer);
                itemRepository.save(item);

                return OrderDto.fromEntity(orderRepository.save(offer));
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "물품이 존재하지 않습니다.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다.");
        }
    }

    // 구매 제안 조회(판매자는 구매 제안 모두 조회)
    public List<OrderDto> readSeller() {
        return orderRepository.findAll().stream()
                .map(OrderDto::fromEntity)
                .toList();
    }

    // 구매 제안 조회(제안 등록한 사용자는 자신의 제안 물품만 조회)
    public List<OrderDto> readOffer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            List<Order> orderOffers = orderRepository.findByOfferName(user.getUsername());

            if (!orderOffers.isEmpty()) {
                return orderOffers.stream()
                        .map(OrderDto::fromEntity)
                        .toList();
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "제안 목록이 없습니다.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // 판매자의 구매 제안 응답
    public ItemDto updateResponse(Long itemId, Long offerId, ItemDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            log.info("권한: {}", user.getBusinessStatus());

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            if (!item.getOrders().isEmpty()) {
                log.info("실행 확인");
                Order offer = orderRepository.findById(offerId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

                if (dto.getResponse().equals("수락")) {
                    offer.setOfferStatus("수락");
                    log.info("판매자 item 수락 응답 상태: {}", item.getResponse());
                    orderRepository.save(offer);

                    item.setResponse(dto.getResponse());
                    return ItemDto.fromEntity(itemRepository.save(item));
                } else if (dto.getResponse().equals("거절")) {
                    offer.setOfferStatus("거절");
                    log.info("판매자 item 거절 응답 상태: {}", item.getResponse());
                    orderRepository.save(offer);
                    item.setResponse(dto.getResponse());
                    return ItemDto.fromEntity(itemRepository.save(item));
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

    // 구매 제안 확정 사용자의 응답
    public OrderDto updateStatus(Long itemId, Long offerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<UserEntity> optionalUser = userRepository.findByUserName(currentUsername);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            log.info("권한: {}", user.getBusinessStatus());

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            if (!item.getOrders().isEmpty()) {
                log.info("실행 확인");
                Order offer = orderRepository.findById(offerId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

                if (offer.getOfferStatus().equals("수락")) {
                    offer.setOfferStatus("확정");

                    item.setStatus(ItemSellStatus.SOLD_OUT.name());
                    itemRepository.save(item);
                    // TODO 다른 제안 상태들은 거절로 변경...

                    return OrderDto.fromEntity(orderRepository.save(offer));
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}