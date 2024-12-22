package com.example.cbox.service;

import com.example.cbox.IntegrationTest;
import com.example.cbox.IntegrationTestBase;
import com.example.cbox.dto.create.UserAuthDto;
import com.example.cbox.mapper.FileMapper;
import com.example.cbox.repository.FileRepository;
import com.example.cbox.repository.LinkRepository;
import com.example.cbox.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
@ActiveProfiles("test")
public class FileServiceTest extends IntegrationTestBase {

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LinkRepository linkRepository;

    private FileService fileService;

    @BeforeEach
    void setFileService() {
        fileService = new FileService(fileMapper, fileRepository, userRepository, linkRepository);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2})
    void findById(Long id) {
        var user = fileService.findById(new UserAuthDto(null, null, null, null), id);

        System.out.println("a");

        assertNotNull(user);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2})
    void delete(Long id) {
//        assertTrue(fileService.delete(id));
    }

    @ParameterizedTest
    @ValueSource(strings = {"aboba", "privet.txt"})
    void deleteByLink(String link) {
        var user = new UserAuthDto(UUID.fromString("8ca8d838-9072-4721-8fcc-1d58c9aa5ce7"), "as", "asd", new ArrayList<>());
        var b = fileService.deleteByLink(user, link);

        assertTrue(b);
    }

    @Test
    void findAll() {
        var all = fileService.findAll(1, 10);

        assertNotNull(all);
        assertEquals(3, all.getTotalElements());
    }
}
