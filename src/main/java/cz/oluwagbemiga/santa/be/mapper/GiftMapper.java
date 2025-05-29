package cz.oluwagbemiga.santa.be.mapper;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.entity.Gift;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GiftMapper extends GenericMapper<Gift, GiftDTO> {

    @Override
    @Mapping(target = "budgetPerGift", source = "budget")
    GiftDTO toDto(Gift entity);

    @Override
    @Mapping(target = "budget", source = "budgetPerGift")
    Gift toEntity(GiftDTO dto);
}