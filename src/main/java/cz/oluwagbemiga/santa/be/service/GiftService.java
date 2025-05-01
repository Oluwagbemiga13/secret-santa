package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.entity.Gift;
import cz.oluwagbemiga.santa.be.entity.Person;
import cz.oluwagbemiga.santa.be.mapper.GiftMapper;
import cz.oluwagbemiga.santa.be.repository.GiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GiftService {

    private final GiftRepository giftRepository;
    private final GiftMapper giftMapper;
    private final PersonService personService;

    public List<GiftDTO> getAllGifts() {
        List<Gift> gifts = giftRepository.findAll();
        return gifts.stream().map(giftMapper::toDto).toList();
    }

    public GiftDTO getGiftById(UUID id) {
        Gift gift = giftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gift not found with ID: " + id));
        return giftMapper.toDto(gift);
    }

    public GiftDTO createGift() {
        Gift gift = new Gift();
        Gift saved = giftRepository.save(gift);
        return giftMapper.toDto(saved);
    }

    public GiftDTO updateGift(UUID id, GiftDTO giftDTO) {
        Gift existingGift = giftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gift not found with ID: " + id));
        existingGift.setName(giftDTO.name());
        existingGift.setDescription(giftDTO.description());
        Gift updatedGift = giftRepository.save(existingGift);

        Person person = personService.findByGiftId(id);
        person.setHasSelectedGift(true);
        personService.updatePerson(person);

        return giftMapper.toDto(updatedGift);
    }

    public void deleteGift(UUID id) {
        if (!giftRepository.existsById(id)) {
            throw new IllegalArgumentException("Gift not found with ID: " + id);
        }
        giftRepository.deleteById(id);
    }
}