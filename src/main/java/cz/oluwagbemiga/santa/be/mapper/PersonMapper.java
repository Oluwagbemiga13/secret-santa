package cz.oluwagbemiga.santa.be.mapper;

import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.entity.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonMapper extends GenericMapper<Person, PersonDTO> {

    @Override
    @Mapping(source = "recipient.id", target = "recipientId")
    PersonDTO toDto(Person entity);

    @Override
    @Mapping(source = "recipientId", target = "recipient.id")
    Person toEntity(PersonDTO dto);
}