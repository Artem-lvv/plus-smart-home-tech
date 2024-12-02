package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Pageable;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.model.SetProductQuantityStateRequest;
import ru.yandex.practicum.model.productEntity.Product;
import ru.yandex.practicum.model.productEntity.ProductCategory;
import ru.yandex.practicum.model.productEntity.QuantityState;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ProductRepository productRepository;

    @Qualifier("mvcConversionService")
    private final ConversionService cs;

    @Override
    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        Product productEntity = cs.convert(productDto, Product.class);
        productEntity = productRepository.save(productEntity);
        ProductDto newProductDto = cs.convert(productEntity, ProductDto.class);

        log.info("Created new product: {}", productEntity);
        log.info("Convert ProductEntity to ProductDto: {}. \n {}", productEntity, newProductDto);

        return newProductDto;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        Optional<Product> productById = productRepository.findById(productId);
        if (productById.isEmpty()) {
            throw new EntityNotFoundException("Product", productId.toString());
        }
        ProductDto productDto = cs.convert(productById.get(), ProductDto.class);

        log.info("Get product by id: {}. \n Product: {}", productId, productById);
        log.info("Convert Product to ProductDto: {}. \n {}", productById, productDto);

        return productDto;
    }

    @Override
    public List<ProductDto> getProducts(String category, Pageable pageable) {
        org.springframework.data.domain.Pageable pageRequest = PageRequest.of(
                pageable.getPage(),
                pageable.getSize(),
                Sort.Direction.DESC,
                pageable.getSort().toArray(new String[0]));

        Page<Product> productEntities = productRepository.findByProductCategory(category, pageRequest);

        List<ProductDto> productDtos = productEntities.stream()
                .map(productEntity -> cs.convert(productEntity, ProductDto.class))
                .toList();

        log.info("Get products by category: {}. Total products found: {}", category, productDtos.size());

        return productDtos;
    }

    @Override
    @Transactional
    public Boolean removeProductFromStore(UUID productId) {
        Optional<Product> productEntityOptional = productRepository.findById(productId);
        if (productEntityOptional.isPresent()) {
            productRepository.deleteById(productId);
            log.info("Removed product with id: {}", productId);
            return true;
        } else {
            log.warn("Product with id: {} not found, cannot remove", productId);
            throw new EntityNotFoundException("Product", productId.toString());
        }
    }

    @Override
    @Transactional
    public Boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        Optional<Product> productEntityOptional = productRepository.findById(request.getProductId());
        if (productEntityOptional.isPresent()) {
            Product productEntity = productEntityOptional.get();
            productEntity.setQuantityState(ru.yandex.practicum.model.productEntity.QuantityState
                    .fromValue(request.getQuantityState().getValue()));
            productRepository.save(productEntity);

            log.info("Updated quantity for product id: {} to {}", request.getProductId(), request.getQuantityState());
            return true;
        } else {
            log.warn("Product with id: {} not found, cannot update quantity", request.getProductId());
            throw new EntityNotFoundException("Product", request.getProductId().toString());
        }
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Optional<Product> productEntityOptional = productRepository.findById(productDto.getProductId().get());
        if (productEntityOptional.isPresent()) {
            Product productEntity = productEntityOptional.get();
            productEntity.setProductName(productDto.getProductName());
            productEntity.setDescription(productDto.getDescription());
            productEntity.setPrice(productDto.getPrice());
            productEntity.setQuantityState(QuantityState.fromValue(productDto.getQuantityState().getValue()));
            productEntity.setProductCategory(ProductCategory.fromValue(productDto.getProductCategory().getValue()));

            Product updatedEntity = productRepository.save(productEntity);
            ProductDto updatedProductDto = cs.convert(updatedEntity, ProductDto.class);

            log.info("Updated product: {}", updatedProductDto);
            return updatedProductDto;
        } else {
            log.warn("Product with id: {} not found, cannot update", productDto.getProductId());
            throw new EntityNotFoundException("Product", productDto.getProductId().get().toString());
        }
    }
}
