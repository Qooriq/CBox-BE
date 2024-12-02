package com.example.cbox.mapper;

import com.example.cbox.config.MapperConfigurationn;
import com.example.cbox.dto.create.FileCreateEditDto;
import com.example.cbox.dto.create.FileEditDto;
import com.example.cbox.dto.read.FileReadDto;
import com.example.cbox.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfigurationn.class)
public interface FileMapper {

    FileReadDto toFileReadDto(File file);

    File toFile(FileEditDto fileReadDto);

    File toFile(FileCreateEditDto fileCreateEditDto);

    void map(@MappingTarget File to, FileEditDto from);

    void map(@MappingTarget File to, FileCreateEditDto from);
}
