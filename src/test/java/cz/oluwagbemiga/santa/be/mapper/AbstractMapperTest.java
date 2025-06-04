package cz.oluwagbemiga.santa.be.mapper;


import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Abstract class for testing mappers. All mapper test classes should be extending this class.
 *
 * @param <E> Entity type
 * @param <D> DTO type
 */
@Slf4j
public abstract class AbstractMapperTest<E, D> {

    protected abstract GenericMapper<E, D> mapper();

    protected abstract E entityInstance();

    protected abstract D dtoInstance();


    protected void testMapper() {
        D actualDto = mapper().toDto(entityInstance());
        assertDtoEquals(dtoInstance(), actualDto);

        D actualDto_1 = mapper().toDto(List.of(entityInstance())).get(0);
        assertDtoEquals(dtoInstance(), actualDto_1);

        E actualEntity = mapper().toEntity(dtoInstance());
        assertEntityEquals(entityInstance(), actualEntity);

        E actualEntity_1 = mapper().toEntity(List.of(dtoInstance())).get(0);
        assertEntityEquals(entityInstance(), actualEntity_1);
    }

    protected void assertDtoEquals(D expected, D actual) {
        assertThat(actual).isEqualTo(expected);
    }

    protected void assertEntityEquals(E expected, E actual) {
        assertThat(actual).isEqualTo(expected);
    }
}

