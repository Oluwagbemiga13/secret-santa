package cz.oluwagbemiga.santa.be.service;


import cz.oluwagbemiga.santa.be.dto.AdminStatistics;
import cz.oluwagbemiga.santa.be.repository.GiftRepository;
import cz.oluwagbemiga.santa.be.repository.PersonRepository;
import cz.oluwagbemiga.santa.be.repository.SantasListRepository;
import cz.oluwagbemiga.santa.be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SantasListRepository santasListRepository;
    private final PersonRepository personRepository;
    private final GiftRepository giftRepository;

    public AdminStatistics getStatistics() {
        long totalUsers = userRepository.count();
        long totalSantasLists = santasListRepository.count();
        long totalPersons = personRepository.count();
        long totalGifts = giftRepository.count();
        return new AdminStatistics(totalUsers, totalSantasLists, totalPersons,totalGifts);
    }
}