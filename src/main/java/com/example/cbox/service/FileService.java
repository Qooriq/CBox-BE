package com.example.cbox.service;

import com.example.cbox.annotation.TransactionalService;
import com.example.cbox.dto.create.FileCreateEditDto;
import com.example.cbox.dto.create.FileRenameDto;
import com.example.cbox.dto.create.UserAuthDto;
import com.example.cbox.dto.read.FileGetDto;
import com.example.cbox.dto.read.FileReadDto;
import com.example.cbox.entity.File;
import com.example.cbox.entity.LinkRestrictionBypass;
import com.example.cbox.entity.User;
import com.example.cbox.mapper.FileMapper;
import com.example.cbox.repository.FileRepository;
import com.example.cbox.repository.LinkRepository;
import com.example.cbox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@TransactionalService
@RequiredArgsConstructor
public class FileService {
    @Value("./files")
    String bucket = "./files";

    private final FileMapper fileMapper;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final LinkRepository linkRepository;


    public Page<FileReadDto> findAll(Integer page, Integer limit) {
        PageRequest req = PageRequest.of(page - 1, limit);
        return fileRepository.findAll(req)
                .map(fileMapper::toFileReadDto);
    }

    @SneakyThrows
    public Optional<FileGetDto> findById(UserAuthDto dto, Long id) {
        return Optional.of(new FileGetDto(fileRepository.findById(id)
                .map(file -> {
                    try {
                        return new FileInputStream(bucket + "/" + dto.id().toString() + "/" + file.getLink());
                    } catch (FileNotFoundException e) {
                        return null;
                    }
                })
                .get().readAllBytes()));
    }

    @Transactional
    public boolean delete(Long id) {
        var file = fileRepository.findById(id);
        fileRepository.deleteById(id);
        return file.isEmpty();
    }

    @Transactional
    public boolean deleteByLink(UserAuthDto dto, String fileName) {
        String path = bucket + "/" + dto.id().toString() + "/" + fileName;
        var file = fileRepository.findByLink(path);
        fileRepository.deleteByLink(bucket + "/" + fileName);
        return file.isPresent();
    }

    @SneakyThrows
    @Transactional
    public FileReadDto create(UserAuthDto dto, FileCreateEditDto fileDto) {
        Path path = Path.of(bucket, dto.id().toString(), fileDto.file().getOriginalFilename());

        File curFile = File.builder().link(fileDto.file().getOriginalFilename())
                .accessType(fileDto.accessType()).build();
        curFile.setCreatedBy(dto.id().toString());
        curFile.setModifiedBy(dto.id().toString());
        curFile.setCreatedAt(Instant.now());
        curFile.setModifiedAt(Instant.now());

        User userEntity = userRepository.findById(dto.id()).get();

        LinkRestrictionBypass link = LinkRestrictionBypass.builder().userId(userEntity)
                .fileId(curFile).build();
        fileRepository.save(curFile);
        linkRepository.save(link);

        var inputStream = fileDto.file().getInputStream();
        try (inputStream) {
            Files.createDirectories(path.getParent());
            Files.write(path, inputStream.readAllBytes(), CREATE, TRUNCATE_EXISTING);
        }
        return Optional.of(curFile)
                .map(fileMapper::toFileReadDto)
                .orElseThrow();
    }

    @Transactional
    @SneakyThrows
    public Optional<FileReadDto> update(UserAuthDto userDto, Long id, FileCreateEditDto dto) {
        Path path = Path.of(bucket, userDto.id().toString(), dto.file().getOriginalFilename());
        var inputStream = dto.file().getInputStream();
        try (inputStream) {
            Files.write(path, inputStream.readAllBytes(), CREATE, TRUNCATE_EXISTING);
        }
        return fileRepository.findById(id)
                .map(fileMapper::toFileReadDto);
    }

    public Page<FileReadDto> findAllUserFiles(UserAuthDto user, Integer page, Integer limit) {
        PageRequest req = PageRequest.of(page - 1, limit);
        return fileRepository.findAllByCreatedBy(user.id().toString(), req)
                .map(fileMapper::toFileReadDto);
    }

    public Optional<FileReadDto> findFileByLink(String link) {
        return fileRepository.findByLink(link)
                .map(fileMapper::toFileReadDto);
    }

    @Transactional
    public Optional<FileReadDto> renameById(UserAuthDto dto, FileRenameDto fileDto) {
        return fileRepository.findById(fileDto.id())
                .map(file -> {

                    var curFile = new java.io.File(bucket + "/" + dto.id().toString() + "/" + file.getLink());
                    var newFile = new java.io.File(bucket + "/" + dto.id().toString() + "/" + fileDto.name());
                    curFile.renameTo(newFile);

                    file.setLink(fileDto.name());

                    return file;
                })
                .map(fileRepository::save)
                .map(fileMapper::toFileReadDto);
    }

}
