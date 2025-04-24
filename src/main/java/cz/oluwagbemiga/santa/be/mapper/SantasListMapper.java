package cz.oluwagbemiga.santa.be.mapper;

import cz.oluwagbemiga.santa.be.dto.SantasListDTO;
import cz.oluwagbemiga.santa.be.entity.SantasList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PersonMapper.class})
public interface SantasListMapper extends GenericMapper<SantasList, SantasListDTO> {

    @Override
    @Mapping(source = "owner.uuid", target = "ownerId")
    SantasListDTO toDto(SantasList entity);

    @Override
    @Mapping(source = "ownerId", target = "owner.uuid")
    SantasList toEntity(SantasListDTO dto);
}