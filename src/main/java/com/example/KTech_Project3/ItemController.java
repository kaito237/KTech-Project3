package com.example.KTech_Project3;

import com.example.KTech_Project3.dto.ItemDto;
import com.example.KTech_Project3.dto.OrderDto;
import com.example.KTech_Project3.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping("/create")
    public ItemDto create(
            @RequestBody ItemDto dto
    ) {
        return service.create(dto);
    }

    @GetMapping
    public List<ItemDto> readAll() {
        return service.readAll();
    }

    @GetMapping("/{itemId}")
    public ItemDto readOne(
            @PathVariable("itemId") Long itemId
    ) {
        return service.readOne(itemId);
    }

    @PutMapping("/{itemId}/update")
    public ItemDto update(
            @PathVariable("itemId") Long itemId,
            @RequestBody ItemDto dto
    ) {
        return service.update(itemId, dto);
    }

    @DeleteMapping("/{itemId}/delete")
    public void delete(
            @PathVariable("itemId") Long itemId
    ) {
        service.delete(itemId);
    }

    @PostMapping("/{itemId}/offers")
    public OrderDto offer(
            @PathVariable("itemId") Long itemId
    ) {
        return service.createOffer(itemId);
    }

    @GetMapping("/offer/read-offer")
    public List<OrderDto> readOffer() {
        return service.readOffer();
    }

    @GetMapping("/offer/read-seller")
    public List<OrderDto> readSeller() {
        return service.readSeller();
    }

    @PutMapping("/{itemId}/response/{offerId}")
    public ItemDto updateResponse(
            @PathVariable("itemId") Long itemId,
            @PathVariable("offerId") Long offerId,
            @RequestBody ItemDto dto
    ) {
        return service.updateResponse(itemId, offerId, dto);
    }

    @PutMapping("/{itemId}/status/{offerId}")
    public OrderDto updateStatus(
            @PathVariable("itemId") Long itemId,
            @PathVariable("offerId") Long offerId
    ) {
        return service.updateStatus(itemId, offerId);


    }
}
