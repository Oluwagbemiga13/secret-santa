package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.entity.Gift;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import cz.oluwagbemiga.santa.be.mapper.GiftMapper;
import cz.oluwagbemiga.santa.be.repository.GiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class GiftServiceTest {

    @Mock
    private GiftRepository giftRepository;

    @Mock
    private GiftMapper giftMapper;

    @Mock
    private PersonService personService;

    @InjectMocks
    private GiftService giftService;

    private Gift gift;
    private GiftDTO giftDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        UUID giftId = UUID.randomUUID();
        gift = Gift.builder()
                .id(giftId)
                .name("Gift Name")
                .description("Gift Description")
                .affiliateLink("Affiliate Link")
                .budget(100)
                .expirationDate(LocalDate.now().plusDays(30))
                .status(GiftStatus.CREATED)
                .build();

        giftDTO = new GiftDTO(
                giftId,
                "Gift Name",
                "Gift Description",
                "Affiliate Link",
                100,
                GiftStatus.CREATED,
                LocalDate.now().plusDays(30)
        );
    }

    @Test
    void testGetAllGifts() {
        // Arrange
        when(giftRepository.findAll()).thenReturn(List.of(gift));
        when(giftMapper.toDto(gift)).thenReturn(giftDTO);

        // Act
        List<GiftDTO> result = giftService.getAllGifts();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Gift Name", result.get(0).name());
        verify(giftRepository, times(1)).findAll();
        verify(giftMapper, times(1)).toDto(gift);
    }

    @Test
    void testGetGiftById() {
        // Arrange
        when(giftRepository.findById(gift.getId())).thenReturn(Optional.of(gift));
        when(giftMapper.toDto(gift)).thenReturn(giftDTO);

        // Act
        GiftDTO result = giftService.getGiftById(gift.getId());

        // Assert
        assertNotNull(result);
        assertEquals("Gift Name", result.name());
        verify(giftRepository, times(1)).findById(gift.getId());
        verify(giftMapper, times(1)).toDto(gift);
    }

    @Test
    void testCreateGift() {
        // Arrange
        when(giftRepository.save(any(Gift.class))).thenReturn(gift);
        when(giftMapper.toDto(gift)).thenReturn(giftDTO);

        // Act
        GiftDTO result = giftService.createGift(100, LocalDate.now().plusDays(30));

        // Assert
        assertNotNull(result);
        assertEquals(100, result.budgetPerGift());
        verify(giftRepository, times(1)).save(any(Gift.class));
        verify(giftMapper, times(1)).toDto(gift);
    }

    @Test
    void testGetAllByStatus() {
        // Arrange
        when(giftRepository.getAllByStatus(GiftStatus.CREATED)).thenReturn(List.of(gift));
        when(giftMapper.toDto(gift)).thenReturn(giftDTO);

        // Act
        List<GiftDTO> result = giftService.getAllByStatus(GiftStatus.CREATED);

        // Assert
        assertEquals(1, result.size());
        assertEquals(GiftStatus.CREATED, result.get(0).status());
        verify(giftRepository, times(1)).getAllByStatus(GiftStatus.CREATED);
        verify(giftMapper, times(1)).toDto(gift);
    }

    @Test
    void testUpdateLink() {
        // Arrange
        String newAffiliateLink = "New Affiliate Link";
        gift.setAffiliateLink(newAffiliateLink);
        gift.setStatus(GiftStatus.LINKED);

        GiftDTO updatedGiftDTO = new GiftDTO(
                gift.getId(),
                gift.getName(),
                gift.getDescription(),
                newAffiliateLink,
                gift.getBudget(),
                GiftStatus.LINKED,
                gift.getExpirationDate()
        );

        when(giftRepository.findById(gift.getId())).thenReturn(Optional.of(gift));
        when(giftRepository.save(gift)).thenReturn(gift);
        when(giftMapper.toDto(gift)).thenReturn(updatedGiftDTO);

        // Act
        GiftDTO result = giftService.updateLink(gift.getId(), newAffiliateLink);

        // Assert
        assertNotNull(result);
        assertEquals(newAffiliateLink, result.affiliateLink());
        assertEquals(GiftStatus.LINKED, result.status());
        verify(giftRepository, times(1)).findById(gift.getId());
        verify(giftRepository, times(1)).save(gift);
        verify(giftMapper, times(1)).toDto(gift);
    }
}