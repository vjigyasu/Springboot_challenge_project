package com.spring.customer.response;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {

        if (returnType.getParameterType().equals(ApiResponse.class)) {
            return false;
        }

        if (returnType.getParameterType().equals(byte[].class)) {
            return false;
        }

        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {


        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger")) {
            return body;
        }


        if (body instanceof ApiResponse<?>) {
            return body;
        }

        int status = 200;
        if (response instanceof ServletServerHttpResponse servletResponse) {
            HttpServletResponse httpResponse = servletResponse.getServletResponse();
            status = httpResponse.getStatus();
        }

        if (body instanceof ResponseEntity<?> entity) {
            return ResponseEntity
                    .status(entity.getStatusCode())
                    .body(new ApiResponse<>(
                            entity.getBody(),
                            "Success",
                            entity.getStatusCode().value()
                    ));
        }

        return new ApiResponse<>(body, "Success", status);
    }
}
