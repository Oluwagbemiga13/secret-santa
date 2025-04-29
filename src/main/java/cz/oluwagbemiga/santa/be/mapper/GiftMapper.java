package cz.oluwagbemiga.santa.be.mapper;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.entity.Gift;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GiftMapper extends GenericMapper<Gift, GiftDTO> {

    @Override
    GiftDTO toDto(Gift entity);

    @Override
    Gift toEntity(GiftDTO dto);
}