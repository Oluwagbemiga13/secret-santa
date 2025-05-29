package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.AdminStatistics;
import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import cz.oluwagbemiga.santa.be.repository.GiftRepository;
import cz.oluwagbemiga.santa.be.repository.PersonRepository;
import cz.oluwagbemiga.santa.be.repository.SantasListRepository;
import cz.oluwagbemiga.santa.be.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SantasListRepository santasListRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private GiftRepository giftRepository;

    @Mock
    private GiftService giftService;

    @InjectMocks
    private AdminService adminService;

    private UUID giftId;
    private GiftDTO giftDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        giftId = UUID.randomUUID();
        giftDTO = new GiftDTO(
                giftId,
                "Gift Name",
                "Gift Description",
                null,
                100,
                GiftStatus.SELECTED,
                LocalDate.now().plusDays(30)
        );
    }

    @Test
    void testGetStatistics() {
        when(userRepository.count()).thenReturn(10L);
        when(santasListRepository.count()).thenReturn(5L);
        when(personRepository.count()).thenReturn(20L);
        when(giftRepository.count()).thenReturn(15L);

        AdminStatistics statistics = adminService.getStatistics();

        assertEquals(10L, statistics.totalUsers());
        assertEquals(5L, statistics.totalSantasLists());
        assertEquals(20L, statistics.totalPersons());
        assertEquals(15L, statistics.totalGifts());
        verify(userRepository, times(1)).count();
        verify(santasListRepository, times(1)).count();
        verify(personRepository, times(1)).count();
        verify(giftRepository, times(1)).count();
    }

    @Test
    void testGetAllGiftsByStatus() {
        GiftStatus status = GiftStatus.SELECTED;
        when(giftService.getAllByStatus(status)).thenReturn(List.of(giftDTO));

        List<GiftDTO> gifts = adminService.getAllGiftsByStatus(status);

        assertEquals(1, gifts.size());
        assertEquals("Gift Name", gifts.get(0).name());
        verify(giftService, times(1)).getAllByStatus(status);
    }

    @Test
    void testAddAffiliateLink() {
        GiftDTO updatedGiftDTO = new GiftDTO(
                giftDTO.id(),
                giftDTO.name(),
                giftDTO.description(),
                "New Affiliate Link",
                giftDTO.budgetPerGift(),
                giftDTO.status(),
                giftDTO.expirationDate()
        );
        when(giftService.updateLink(giftDTO.id(), updatedGiftDTO.affiliateLink())).thenReturn(updatedGiftDTO);

        GiftDTO result = adminService.addAffiliateLink(updatedGiftDTO);

        assertEquals("New Affiliate Link", result.affiliateLink());
        verify(giftService, times(1)).updateLink(giftDTO.id(), updatedGiftDTO.affiliateLink());
    }
}