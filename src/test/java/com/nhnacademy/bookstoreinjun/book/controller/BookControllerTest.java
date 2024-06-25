package com.nhnacademy.bookstoreinjun.book.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.bookstoreinjun.config.SecurityConfig;
import com.nhnacademy.bookstoreinjun.controller.BookController;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.entity.Product;
import com.nhnacademy.bookstoreinjun.exception.AladinJsonProcessingException;
//import com.nhnacademy.bookstoreinjun.filter.EmailHeaderFilter;
import com.nhnacademy.bookstoreinjun.exception.InvalidSortNameException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundIdException;
import com.nhnacademy.bookstoreinjun.exception.PageOutOfRangeException;
import com.nhnacademy.bookstoreinjun.repository.ProductRepository;
import com.nhnacademy.bookstoreinjun.service.productCategoryRelation.ProductCategoryRelationService;
import com.nhnacademy.bookstoreinjun.service.productTag.ProductTagService;
import com.nhnacademy.bookstoreinjun.service.aladin.AladinService;
import com.nhnacademy.bookstoreinjun.service.book.BookService;
import com.nhnacademy.bookstoreinjun.service.productCategory.ProductCategoryService;
import com.nhnacademy.bookstoreinjun.service.tag.TagService;
import java.time.LocalDate;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AladinService aladinService;

    @MockBean
    BookService bookService;

    @MockBean
    ProductCategoryService productCategoryService;

    @MockBean
    ProductCategoryRelationService productCategoryRelationService;

    @MockBean
    TagService tagService;

    @MockBean
    ProductTagService productTagService;

    @MockBean
    private ProductRepository productRepository;

//    @MockBean
//    EmailHeaderFilter emailHeaderFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void test1() throws Exception {
        mockMvc.perform(get("/api/product/admin/book")
                        .param("title","이해"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/json"));

        verify(aladinService,times(1)).getAladdinBookPage(any(), eq("이해"));
    }

    @DisplayName("도서 상품 등록 성공 테스트")
    @Test
    @WithMockUser(roles = "ADMIN")
    public void test2() throws Exception {
        when(productRepository.save(any())).thenReturn(Product.builder().productId(123L).build());

        BookProductRegisterRequestDto bookProductRegisterRequestDto = BookProductRegisterRequestDto.builder()
                .title("test title")
                .pubDate(LocalDate.now())
                .publisher("test publisher")
                .author("test author")
                .cover("test cover")
                .isbn("123456789a")
                .isbn13("123456789abcd")
                .productName("test product name")
                .productDescription("test product description")
                .productInventory(0)
                .productPriceStandard(1)
                .productPriceSales(1)
                .packable(false)
                .categories(Arrays.asList("test category1","test category2"))
                .tags(Arrays.asList("test tag1","test tag2"))
                .build();

        String json = objectMapper.writeValueAsString(bookProductRegisterRequestDto);

        mockMvc.perform(post("/api/product/admin/book/register")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(bookService,times(1)).saveBook(any());
    }

    @DisplayName("도서 상품 등록 실패 테스트 - 상품명 제약 조건 위반 (최소 2 글자)")
    @Test
    @WithMockUser(roles = "ADMIN")
    public void test5() throws Exception {
        when(productRepository.save(any())).thenReturn(Product.builder().productId(123L).build());

        BookProductRegisterRequestDto bookProductRegisterRequestDto = BookProductRegisterRequestDto.builder()
                .title("test title")
                .pubDate(LocalDate.now())
                .publisher("test publisher")
                .author("test author")
                .cover("test cover")
                .isbn("123456789a")
                .isbn13("123456789abcd")
                .productName("T")
                .productDescription("test product description")
                .productInventory(0)
                .productPriceStandard(1)
                .productPriceSales(1)
                .packable(false)
                .categories(Arrays.asList("test category1","test category2"))
                .tags(Arrays.asList("test tag1","test tag2"))
                .build();

        String json = objectMapper.writeValueAsString(bookProductRegisterRequestDto);

        mockMvc.perform(post("/api/product/admin/book/register")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(bookService,times(0)).saveBook(any());
    }




    @DisplayName("알라딘 북 리스트 가져오기 성공.")
    @Test
    @WithMockUser(roles = "ADMIN")
    public void test3() throws Exception {
        mockMvc.perform(get("/api/product/admin/book")
                        .param("title","이해"))
                .andExpect(status().isOk());
    }

    @DisplayName("알라딘 북 리스트 가져오기 실패.")
    @Test
    @WithMockUser(roles = "ADMIN")
    public void test4() throws Exception {
        when(aladinService.getAladdinBookPage(any(), eq("이해"))).thenThrow(new AladinJsonProcessingException("error"));

        mockMvc.perform(get("/api/product/admin/book")
                        .param("title","이해"))
                .andExpect(status().is5xxServerError());
    }

    @DisplayName("개별 도서 조회 성공")
    @Test
    @WithMockUser(roles = "CLIENT")
    public void getIndividualBookTestSuccess() throws Exception {
        mockMvc.perform(get("/api/product/book/1"))
                .andExpect(status().isOk());
    }

    @DisplayName("개별 도서 조회 실패")
    @Test
    @WithMockUser(roles = "CLIENT")
    public void getIndividualBookTestFailure() throws Exception {
        when(bookService.getBookByBookId(1L)).thenThrow(NotFoundIdException.class);

        mockMvc.perform(get("/api/product/book/1"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("도서 페이지 조회 성공")
    @WithMockUser(roles = "CLIENT")
    @Test
    public void getBookPageSuccess() throws Exception {
        PageRequestDto dto = PageRequestDto.builder().build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(get("/api/product/books")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("도서 페이지 조회 실패 - 잘못된 정렬 조건")
    @WithMockUser(roles = "CLIENT")
    @Test
    public void getBookPageFailureByWrongSortValue() throws Exception {
        when(bookService.getBookPage(any())).thenThrow(InvalidSortNameException.class);

        PageRequestDto dto = PageRequestDto.builder()
                .sort("wrong sort")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(get("/api/product/books")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("도서 페이지 조회 실패 - 초과 페이지 요청")
    @WithMockUser(roles = "CLIENT")
    @Test
    public void getBookPageFailureByOutOfPageRange() throws Exception {
        when(bookService.getBookPage(any())).thenThrow(PageOutOfRangeException.class);

        PageRequestDto dto = PageRequestDto.builder()
                .sort("wrong sort")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(get("/api/product/books")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


}