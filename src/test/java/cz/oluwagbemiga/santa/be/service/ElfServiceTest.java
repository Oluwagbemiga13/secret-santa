package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.entity.ListStatus;
import cz.oluwagbemiga.santa.be.entity.Person;
import cz.oluwagbemiga.santa.be.entity.SantasList;
import cz.oluwagbemiga.santa.be.repository.SantasListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ElfServiceTest {

    @Mock
    private SantasListRepository santasListRepository;

    @Mock
    private GiftService giftService;

    @Mock
    private SantasListService santasListService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ElfService elfService;

    private SantasList santasList;
    private Person person;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        UUID listId = UUID.randomUUID();
        UUID personId = UUID.randomUUID();

        person = Person.builder()
                .id(personId)
                .name("John Doe")
                .hasSelectedGift(true)
                .build();

        santasList = SantasList.builder()
                .id(listId)
                .name("Test List")
                .persons(List.of(person))
                .status(ListStatus.PEOPLE_SELECTING_GIFTS)
                .isLocked(false)
                .build();
    }

    @Test
    void testShuffleEligibleLists() {
        when(santasListRepository.findByIsLockedFalse()).thenReturn(List.of(santasList));
        when(santasListService.getAllByStatus(ListStatus.GIFTS_SELECTED)).thenReturn(List.of(santasList));

        elfService.shuffleEligibleLists();

        verify(santasListRepository, times(1)).findByIsLockedFalse();
        verify(santasListRepository, times(1)).save(santasList);
        verify(emailService, times(1)).sendResults(santasList.getId());
        verify(santasListService, times(1)).updateStatus(santasList.getId(), ListStatus.SENT_TO_SANTA);
    }

    @Test
    void testIsEligableForShuffle() {
        boolean result = elfService.isEligableForShuffle(santasList);

        assertTrue(result);
    }

    @Test
    void testShuffle() {
        elfService.shuffle(santasList);

        assertTrue(santasList.isLocked());
        assertEquals(person, santasList.getPersons().get(0).getRecipient());
    }

    @Test
    void testSendEligibleInstructions() {
        when(santasListService.getAllByStatus(ListStatus.GIFTS_SELECTED)).thenReturn(List.of(santasList));

        elfService.sendEligibleInstructions();

        verify(emailService, times(1)).sendResults(santasList.getId());
        verify(santasListService, times(1)).updateStatus(santasList.getId(), ListStatus.SENT_TO_SANTA);
    }
}