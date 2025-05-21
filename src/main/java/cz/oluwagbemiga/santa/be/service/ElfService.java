package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.entity.ListStatus;
import cz.oluwagbemiga.santa.be.entity.Person;
import cz.oluwagbemiga.santa.be.entity.SantasList;
import cz.oluwagbemiga.santa.be.repository.SantasListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElfService {

    private final SantasListRepository santasListRepository;

    private final SantasListService santasListService;

    private final EmailService emailService;

    @Value("${secret-santa.fe.base-url}")
    private String baseUrl;

    @Value("${secret-santa.scheduling.check-interval}")
    private int checkInterval;




    /**
     * This method is periodically called to process all SantasLists that are not locked.
     */
    @Scheduled(fixedRateString = "${secret-santa.scheduling.check-interval}")
    public void shuffleEligibleLists() {
        log.info("Starting scheduled processing of all SantasLists");
        List<SantasList> allLists = santasListRepository.findByIsLockedFalse();
        if(allLists.isEmpty()) {
            log.info("No SantasLists found to process");
            return;
        }
        for (SantasList list : allLists) {
            try {
                if (isEligableForShuffle(list)) {
                    shuffle(list);
                    list.setStatus(ListStatus.GIFTS_SELECTED);
                    santasListRepository.save(list);
                    log.info("Successfully processed SantasList with ID: {}", list.getId());
                }
                else {
                    log.debug("SantasList with ID: {} is not eligible for shuffle", list.getId());
                }
            } catch (Exception e) {
                log.error("Error processing SantasList with ID: {}", list.getId(), e);
            }
        }
        sendEligibleInstructions();
    }

    /**
     * This method checks if all persons in the SantasList have selected a gift.
     *
     * @param santasList
     * @return true when all has selected
     */
    public boolean isEligableForShuffle(SantasList santasList) {
        return santasList.getPersons().stream()
                .allMatch(Person::isHasSelectedGift);
    }

    /**
     * This method shuffles the list of persons and assigns each person a recipient.
     * The last person in the list will be assigned the first person as their recipient.
     *
     * @param santasList The SantasList object containing the list of persons to shuffle.
     */
    public void shuffle(SantasList santasList) {
        List<Person> in = santasList.getPersons();
        Collections.shuffle(in);

        IntStream.range(0, in.size())
                .forEach(i -> {
                    if (i == in.size() - 1) {
                        log.debug("Index {} out of bounds for length {}. Getting index 0 instead.", in.size(), in.size());
                        in.get(i).setRecipient(in.get(0));
                    } else {
                        in.get(i).setRecipient(in.get(i + 1));
                    }
                    log.debug("Assigning recipient with UUID: {} to UUID: {}", in.get(i + 1).getId(), in.get(i).getId());
                });
    santasList.setLocked(true);
    }

    public void sendEligibleInstructions(){
        List<SantasList> eligibleLists = santasListService.getAllByStatus(ListStatus.GIFTS_SELECTED);
        eligibleLists
                .forEach(e -> {
                    emailService.sendResults(e.getId());
                    log.info("Sent results to all persons in SantasList with ID: {}", e.getId());
                    santasListService.updateStatus(e.getId(),ListStatus.SENT_TO_SANTA);
                });

    }


}
