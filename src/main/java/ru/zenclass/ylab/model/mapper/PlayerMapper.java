package ru.zenclass.ylab.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.dto.PlayerDTO;

@Mapper
public interface PlayerMapper {

    PlayerMapper INSTANCE = Mappers.getMapper(PlayerMapper.class);

    PlayerDTO toDTO(Player player);

    Player toEntity(PlayerDTO playerDTO);

    RegisterPlayerDTO toRegisterPlayerDTO(Player entity);
    @Mapping(target = "id", ignore = true)
    Player toPlayerEntity(RegisterPlayerDTO dto);
}

