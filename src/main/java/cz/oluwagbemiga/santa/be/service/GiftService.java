package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.entity.Gift;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import cz.oluwagbemiga.santa.be.entity.Person;
import cz.oluwagbemiga.santa.be.exception.InvalidRequestException;
import cz.oluwagbemiga.santa.be.mapper.GiftMapper;
import cz.oluwagbemiga.santa.be.repository.GiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public GiftDTO createGift(int budget, LocalDate expirationDate) {
        Gift gift = new Gift();
        gift.setBudget(budget);
        gift.setExpirationDate(expirationDate);
        Gift saved = giftRepository.save(gift);
        return giftMapper.toDto(saved);
    }

    public List<GiftDTO> getAllByStatus(GiftStatus status) {
        List<Gift> allByStatus = giftRepository.getAllByStatus(status);
        if (allByStatus.isEmpty())
            throw new InvalidRequestException("No gifts found with status: " + status.getValue());
        else return allByStatus.stream()
//                .sorted(Comparator.comparing(Gift::getExpirationDate))
                .map(giftMapper::toDto).toList();
    }

    public void updateStatus(UUID giftId, GiftStatus status) {
        Gift gift = giftRepository.findById(giftId).orElseThrow(
                () -> new InvalidRequestException("Gift not found with ID: " + giftId));
        gift.setStatus(status);
        giftRepository.save(gift);
    }

    public GiftDTO updateGift(GiftDTO giftDTO) {
        Gift existingGift = giftRepository.findById(giftDTO.id())
                .orElseThrow(() -> new InvalidRequestException("Gift not found with ID: " + giftDTO.id()));
        existingGift.setName(giftDTO.name());
        existingGift.setDescription(giftDTO.description());
        existingGift.setAffiliateLink(giftDTO.affiliateLink());
        existingGift.setBudget(giftDTO.budgetPerGift());
        existingGift.setStatus(giftDTO.status());
        return giftMapper.toDto(giftRepository.save(existingGift));
    }

    public GiftDTO updateLink(UUID giftId, String affiliateLink) {
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new InvalidRequestException("Gift not found with ID: " + giftId));
        gift.setAffiliateLink(affiliateLink);
        gift.setStatus(GiftStatus.LINKED);
        return giftMapper.toDto(giftRepository.save(gift));
    }

    /**
     * Fills the desired gift for a person. This method updates the gift's details and sets its status to SELECTED!
     *
     * @param id
     * @param giftDTO
     * @return
     */
    public GiftDTO fillDesiredGift(UUID id, GiftDTO giftDTO) {
        Gift existingGift = giftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gift not found with ID: " + id));
        existingGift.setName(giftDTO.name());
        existingGift.setDescription(giftDTO.description());
        existingGift.setStatus(GiftStatus.SELECTED);
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