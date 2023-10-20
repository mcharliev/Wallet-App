package ru.zenclass.ylab.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.dto.PlayerDTO;

@Mapper
public interface PlayerMapper {

    PlayerMapper INSTANCE = Mappers.getMapper(PlayerMapper.class);

    PlayerDTO toDTO(Player player);

    Player toEntity(PlayerDTO playerDTO);
}

