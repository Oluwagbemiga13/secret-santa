package cz.oluwagbemiga.santa.be.service;


import cz.oluwagbemiga.santa.be.dto.AdminStatistics;
import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import cz.oluwagbemiga.santa.be.repository.GiftRepository;
import cz.oluwagbemiga.santa.be.repository.PersonRepository;
import cz.oluwagbemiga.santa.be.repository.SantasListRepository;
import cz.oluwagbemiga.santa.be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SantasListRepository santasListRepository;
    private final PersonRepository personRepository;
    private final GiftRepository giftRepository;
    private final GiftService giftService;


    public AdminStatistics getStatistics() {
        long totalUsers = userRepository.count();
        long totalSantasLists = santasListRepository.count();
        long totalPersons = personRepository.count();
        long totalGifts = giftRepository.count();
        return new AdminStatistics(totalUsers, totalSantasLists, totalPersons, totalGifts);
    }

    public List<GiftDTO> getAllGiftsByStatus(GiftStatus status) {
        return giftService.getAllByStatus(status);
    }

    public GiftDTO addAffiliateLink(GiftDTO giftDTO) {
        return giftService.updateLink(giftDTO.id(), giftDTO.affiliateLink());
    }

}