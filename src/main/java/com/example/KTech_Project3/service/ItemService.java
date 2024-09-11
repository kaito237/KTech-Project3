package com.example.KTech_Project3.service;

import com.example.KTech_Project3.FileHandlerUtils;
import com.example.KTech_Project3.dto.ItemDto;
import com.example.KTech_Project3.dto.OfferDto;
import com.example.KTech_Project3.entity.Item;
import com.example.KTech_Project3.entity.Offer;
import com.example.KTech_Project3.entity.UserEntity;
import com.example.KTech_Project3.exception.ItemNotFoundException;
import com.example.KTech_Project3.repo.ItemRepository;
import com.example.KTech_Project3.repo.OfferRepository;
import com.example.KTech_Project3.repo.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OfferRepository offerRepository;
    private final FileHandlerUtils fileHandlerUtils;


    public String registerItem(ItemDto itemDto, String username) {
        // 아이템 등록 처리
        Item item = new Item();
        item.setTitle(itemDto.getTitle());
        item.setDescription(itemDto.getDescription());
        item.setMinimumPrice(itemDto.getMinimumPrice());
        item.setStatus("판매중"); // 처음 등록할 때 "판매중" 상태로 설정
        item.setUser(userRepository.findIdByUsername(username).orElse(null)); // 현재 사용자의 아이디 설정

        // 아이템 저장
        itemRepository.save(item);

        return "중거고래 상품을 등록하였습니다.";
    }

    public List<ItemDto> itemAllList() {
        // 모든 물품 정보를 가져와서 리스트로 저장
        List<Item> itemList = itemRepository.findAll();

        // 각 물품을 ItemDto로 변환하여 저장할 리스트 생성
        List<ItemDto> itemDtoList = new ArrayList<>();

        // 각 물품을 ItemDto로 변환하여 리스트에 추가
        for (Item item : itemList) {
            itemDtoList.add(ItemDto.fromEntity(item));
        }

        // 변환된 ItemDto 리스트 반환
        return itemDtoList;
    }

    public String updateItem(ItemDto itemDto, String username, String title) {
        // 아이템 내용 수정
        // 아이템 엔티티를 가져옴
        Optional<Item> optionalItem = itemRepository.findByTitle(title);

        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();

            // 현재 사용자가 해당 아이템의 소유자인지 확인
            if (!item.getUser().getUsername().equals(username)) {
                return "해당 상품을 업데이트할 권한이 없습니다.";
            }

            // 아이템 정보 수정
            item.setTitle(itemDto.getTitle());
            item.setDescription(itemDto.getDescription());
            item.setMinimumPrice(itemDto.getMinimumPrice());

            // 아이템 저장
            itemRepository.save(item);

            return "상품을 업데이트 했습니다.";
        } else {
            return username + "사용자의 " + title + "상품을 찾을 수 없습니다.";
        }
    }

    public String deleteItem(String  title, String username) {
        // 아이템 삭제
        // 아이템 엔티티를 가져옴
        Optional<Item> optionalItem = itemRepository.findByTitle(title);

        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();

            // 현재 사용자가 해당 아이템의 소유자인지 확인
            if (item.getUser().getUsername().equals(username)) {
                // 아이템 정보 삭제
                itemRepository.delete(item);
                return "상품을 삭제했습니다.";
            } else {
                return "이 상품을 삭제할 권한이 없습니다.";
            }
        } else {
            return title + " 상품을 찾을 수 없습니다.";
        }
    }

    public String buyRequestItem(String title, OfferDto offerDto, String username) {
        // 해당 itemId로 Item을 찾음
        Item item = itemRepository.findByTitle(title)
                .orElseThrow(() -> new RuntimeException("해당 상품을 찾을 수 없습니다."));

        // 현재 사용자가 해당 아이템의 소유자인지 확인 ,  일치하지 않을 경우 구매 제안
        if (!item.getUser().getUsername().equals(username)) {
            Offer offer = new Offer();
            // 구매 제안 등록 처리
            offer.setItem(item);
            // 구매 제안을 하는 사용자를 설정
            UserEntity user = userRepository.findIdByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            offer.setUser(user);
            if (offerDto.getOfferPrice() < item.getMinimumPrice()) {
                return "최소 가격보다 높은 금액을 제시해 주세요.";
            }
            offer.setOfferPrice(offerDto.getOfferPrice());
            offer.setStatus("구매 제안");
            // 구매 제안 저장
            offerRepository.save(offer);

            return "구매 제안이 등록되었습니다.";
        }else{
            return "자신의 상품에는 구매 제안을 할 수 없습니다.";
        }
    }

    // 등록한 사용자와 제안한 사용자만 구매제안 조회, username은 토큰을 입력한 사용자의 usernmae
    public List<OfferDto> readOffer(String title, String username) throws ItemNotFoundException {
        // id는 구매제안한 item id
        // id로 물품 조회
        Item item = itemRepository.findByTitle(title)
                .orElseThrow(() -> new ItemNotFoundException("해당 이름의 상품을 찾을 수 없습니다. 상품 이름:" + title));

        // username으로 user id 가져오기
        UserEntity user = userRepository.findIdByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        // 물품을 등록한 사용자가 요청한 경우
        if (item.getUser().getId().equals(user.getId())) {
            // 물품 등록자인 경우, 해당 물품의 모든 제안 조회
            List<Offer> offers = offerRepository.findByItemId(item.getId());
            if (offers.isEmpty()) {
                return Collections.emptyList();
            }
            return mapToOfferDtoList(offers);
        } else {
            // 구매 제안 사용자인 경우, 해당 물품에 대한 자신의 제안만 조회
            List<Offer> offers = offerRepository.findByItemIdAndUserId(item.getId(), user.getId());
            return mapToOfferDtoList(offers);
        }
    }

    private List<OfferDto> mapToOfferDtoList(List<Offer> offers) {
        List<OfferDto> offerDtos = new ArrayList<>();
        for (Offer offer : offers) {
            OfferDto offerDto = OfferDto.fromEntity(offer);
            offerDtos.add(offerDto);
        }
        return offerDtos;
    }

    public String offerAcceptRefuse(
            String title, String offerUser, Long price, String username, String acceptRefuse
    ) {
        // offerUser의 username을 가진 회원 찾기
        UserEntity user = userRepository.findIdByUsername(offerUser)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // offer에서 offerUser, price로 제안 검색
        Optional<Offer> optionalOfferoffer = offerRepository.findByUserIdAndOfferPrice(user.getId(),price);
        if (optionalOfferoffer.isPresent()) {
            // 해당 offer에 대한 물품 조회
            Optional<Item> optionalItem = itemRepository.findByTitle(title);
            if (optionalItem.isPresent()) {
                Item item = optionalItem.get();
                Offer offer = optionalOfferoffer.get();
                // 물품을 등록한 사용자와 현재 사용자가 같을 경우
                if (item.getUser().getUsername().equals(username)) {
                    // acceptRefuse를 offer의 status에 저장
                    offer.setStatus(acceptRefuse);
                    offerRepository.save(offer); // 변경사항 저장
                    return "구매 제안 상태가 업데이트 되었습니다.";
                }
            }
        }
        return "제안 상태를 업데이트하지 못했습니다. 제안을 찾을 수 없거나 승인되지 않았습니다.";
    }

    // 구매 확정 - 대상 물품의 상태는 판매 완료, 다른 구매 제안의 상태는 거절
    public String offerConfirm(String title, String username) {
        // username을 가진 회원 찾기
        UserEntity user = userRepository.findIdByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // title을 가진 item 찾기
        Optional<Item> optionalItem = itemRepository.findByTitle(title);
        if (!optionalItem.isPresent()) {
            return "물품을 찾을 수 없습니다.";
        }
        Item item = optionalItem.get();

        // 인증한 회원의 id와 item의 id, status가 "수락"인 offer 찾기
        Optional<Offer> optionalOffer = offerRepository.findByItemIdAndUserIdAndStatus(item.getId(), user.getId(), "수락");
        if (optionalOffer.isPresent()) {
            Offer offer = optionalOffer.get();

            // 제안의 상태를 "구매 확정"으로 변경
            offer.setStatus("구매 확정");
            offerRepository.save(offer);

            // 해당 물품의 상태를 "판매 완료"로 변경
            item.setStatus("판매 완료");
            itemRepository.save(item); // 변경사항 저장

            // itemId는 같지만 현재 사용자가 아닌 다른 사용자의 offer의 상태를 "거절"로 변경
            List<Offer> otherOffers = offerRepository.findByItemIdAndUserIdNot(item.getId(), user.getId());
            for (Offer otherOffer : otherOffers) {
                otherOffer.setStatus("거절");
                offerRepository.save(otherOffer); // 변경사항 저장
            }

            return "제안이 확인되었습니다.";
        } else {
            return "적절한 제안을 찾을 수 없습니다. 제안 상태가 '수락'되어야 합니다.";
        }
    }

    public String itemImg(String username, MultipartFile file, String title) {
        // username을 가진 회원찾기
        UserEntity user = userRepository.findIdByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 회원의 id와 title을 가진 item 찾기
        Optional<Item> optionalItem = itemRepository.findByTitleAndUserId(title, user.getId());

        if (!optionalItem.isPresent()) {
            return "해당 item에 대한 권한이 없습니다.";
        }

        Item item = optionalItem.get();            // 이미지 등록
        String requestPath = fileHandlerUtils.saveFile(String.format("item/%d/", item.getId()),
                "itemImg",file);

        item.setItemImg(requestPath);
        itemRepository.save(item);
        return "중고거래 물품 이미지 등록 성공";
    }
}
