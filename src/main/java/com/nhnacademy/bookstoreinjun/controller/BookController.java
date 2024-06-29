package com.nhnacademy.bookstoreinjun.controller;

import com.nhnacademy.bookstoreinjun.dto.book.BookProductGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.book.aladin.AladinBookResponseDto;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductRegisterResponseDto;
import com.nhnacademy.bookstoreinjun.dto.error.ErrorResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductUpdateResponseDto;
import com.nhnacademy.bookstoreinjun.exception.AladinJsonProcessingException;
import com.nhnacademy.bookstoreinjun.service.aladin.AladinService;
import com.nhnacademy.bookstoreinjun.service.book.BookService;
//import com.nhnacademy.bookstoreinjun.validator.ProductCategoryRegisterAndUpdateRequestDtoValidator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
//@Controller
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class BookController {

    private final AladinService aladinService;

    private final BookService bookService;


    private final HttpHeaders header = new HttpHeaders() {{
        setContentType(MediaType.APPLICATION_JSON);
    }};


    @ExceptionHandler(AladinJsonProcessingException.class)
    public ResponseEntity<ErrorResponseDto> exceptionHandler(AladinJsonProcessingException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), header, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PostMapping("/admin/book/register")
    public ResponseEntity<ProductRegisterResponseDto> saveBookProduct(
            @Valid @RequestBody BookProductRegisterRequestDto bookProductRegisterRequestDto){
        return new ResponseEntity<>(bookService.saveBook(bookProductRegisterRequestDto), header, HttpStatus.CREATED);
    }


    @PutMapping("/admin/book/update")
    public ResponseEntity<ProductUpdateResponseDto> updateBook(
            @Validated @RequestBody BookProductUpdateRequestDto bookProductUpdateRequestDto){
        return new ResponseEntity<>(bookService.updateBook(bookProductUpdateRequestDto), header, HttpStatus.OK);
    }


    @GetMapping("/book")
    public ResponseEntity<Page<AladinBookResponseDto>> getBooks(
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam("title") String title) {
        return new ResponseEntity<>(aladinService.getAladdinBookPage(pageRequestDto, title), header, HttpStatus.OK);
    }


    @GetMapping("/books")
    public ResponseEntity<Page<BookProductGetResponseDto>> getAllBookPage(
           @Valid @ModelAttribute PageRequestDto pageRequestDto){
        return new ResponseEntity<>(bookService.getBookPage(pageRequestDto), header, HttpStatus.OK);
    }

    @GetMapping("/books/containing")
    public ResponseEntity<Page<BookProductGetResponseDto>> getAladinBookPage(
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam("title") String title){
        return new ResponseEntity<>(bookService.getNameContainingBookPage(pageRequestDto, title), header, HttpStatus.OK);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<BookProductGetResponseDto> getSingleBookInfo(
            @Min(1) @PathVariable long bookId){
        return new ResponseEntity<>(bookService.getBookByBookId(bookId), header, HttpStatus.OK);
    }

    @GetMapping("/books/categoryFilter")
    public ResponseEntity<Page<BookProductGetResponseDto>> getBookPageFilterByCategory(
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam("categoryName") Set<String> categoryNameSet,
            @RequestParam(value = "isAnd", required = false, defaultValue = "true") Boolean conditionIsAnd){
        return new ResponseEntity<>(bookService.getBookPageFilterByCategories(pageRequestDto, categoryNameSet, conditionIsAnd), header, HttpStatus.OK);
    }

//    @GetMapping("/books/tagFilter")
//    public ResponseEntity<Page<BookProductGetResponseDto>> getBookPageFilterByTag(
//            @Valid @ModelAttribute PageRequestDto pageRequestDto,
//            @RequestParam("tagName") Set<String> tagNameSet,
//            @RequestParam(value = "isAnd", required = false, defaultValue = "true") Boolean conditionIsAnd
//    ){
//        return new ResponseEntity<>(bookService.getBookPageFilterByTags(pageRequestDto, tagNameSet, conditionIsAnd), header, HttpStatus.OK);
//    }

    @GetMapping("/books/tagFilter")
    public ResponseEntity<Page<BookProductGetResponseDto>> getFilteredBookPage(
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam(value = "tagName", required = false) Set<String> tagNameSet,
            @RequestParam(value = "categoryName", required = false) Set<String> categoryNameSet,
            @RequestParam(value = "isAnd", required = false, defaultValue = "true") Boolean conditionIsAnd
    ){
        return new ResponseEntity<>(bookService.getBookPageFilterByTags(pageRequestDto, categoryNameSet, tagNameSet, conditionIsAnd), header, HttpStatus.OK);
    }
}
