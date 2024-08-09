package br.acc.bank.util;

import org.modelmapper.ModelMapper;

public class MapperConverter {
     private static final ModelMapper modelMapper = new ModelMapper();

    // Converter de Model para DTO
    public static <D, T> D convertToDto(T entity, Class<D> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    // Converter de DTO para Model
    public static <D, T> T convertToEntity(D dto, Class<T> entityClass) {
        return modelMapper.map(dto, entityClass);
    }
}
