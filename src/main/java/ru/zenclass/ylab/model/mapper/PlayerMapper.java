package ru.zenclass.ylab.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.dto.PlayerDTO;

/**
 * Интерфейс маппера для конвертации объектов между {@link Player} и его DTO представлениями.
 * Использует библиотеку MapStruct для автоматического создания реализации на основе аннотаций.
 */
@Mapper(componentModel = "spring")
public interface PlayerMapper {

    PlayerMapper INSTANCE = Mappers.getMapper(PlayerMapper.class);

    /**
     * Конвертирует сущность {@link Player} в DTO {@link PlayerDTO}.
     *
     * @param player сущность игрока
     * @return объект DTO игрока {@link PlayerDTO}
     */
    PlayerDTO toDTO(Player player);

    /**
     * Конвертирует DTO {@link RegisterPlayerDTO} в сущность {@link Player}.
     * При этом ID игрока игнорируется, так как предполагается, что новая сущность будет создана.
     *
     * @param dto объект DTO для регистрации игрока
     * @return сущность игрока {@link Player}
     */
    @Mapping(target = "id", ignore = true)
    Player toPlayerEntity(RegisterPlayerDTO dto);
}

