package cz.oluwagbemiga.santa.be.mapper;

import cz.oluwagbemiga.santa.be.dto.SantasListDTO;
import cz.oluwagbemiga.santa.be.entity.SantasList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PersonMapper.class})
public interface SantasListMapper extends GenericMapper<SantasList, SantasListDTO> {

    @Override
    @Mapping(source = "owner.uuid", target = "ownerId")
    @Mapping(source = "budgetPerGift", target = "budgetPerGift")
        // Explicit mapping
    SantasListDTO toDto(SantasList entity);

    @Override
    @Mapping(source = "ownerId", target = "owner.uuid")
    @Mapping(source = "budgetPerGift", target = "budgetPerGift")
        // Explicit mapping
    SantasList toEntity(SantasListDTO dto);
}