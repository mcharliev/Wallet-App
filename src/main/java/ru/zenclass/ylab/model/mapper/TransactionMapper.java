package ru.zenclass.ylab.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.zenclass.ylab.model.dto.TransactionDTO;
import ru.zenclass.ylab.model.entity.Transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "localDateTime", source = "entity.localDateTime", qualifiedByName = "formatLocalDateTime")
    TransactionDTO toDTO(Transaction entity);

    Transaction toEntity(TransactionDTO dto);

    @Named("formatLocalDateTime")
    default String formatLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }
}
