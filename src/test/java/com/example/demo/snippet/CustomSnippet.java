package com.example.demo.snippet;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.AbstractBodySnippet;
import org.springframework.restdocs.payload.AbstractFieldsSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.restdocs.request.AbstractParametersSnippet;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.snippet.SnippetException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomSnippet {
    public static AbstractFieldsSnippet requestFieldsCustom(String name, Map<String, Object> attributes, FieldDescriptor... descriptors) {
        boolean ignoreUndocumented = false;
        List<FieldDescriptor> descriptorsList = Arrays.asList(descriptors);

        return new AbstractFieldsSnippet(name, descriptorsList, attributes, ignoreUndocumented) {
            @Override
            protected MediaType getContentType(Operation operation) {
                return operation.getRequest().getHeaders().getContentType();
            }

            @Override
            protected byte[] getContent(Operation operation) {
                return operation.getRequest().getContent();
            }
        };
    }

    public static AbstractFieldsSnippet responseFieldsCustom(String name, Map<String, Object> attributes, FieldDescriptor... descriptors) {
        boolean ignoreUndocumented = false;
        List<FieldDescriptor> descriptorsList = Arrays.asList(descriptors);

        return new AbstractFieldsSnippet(name, descriptorsList, attributes, ignoreUndocumented) {
            @Override
            protected MediaType getContentType(Operation operation) {
                return operation.getResponse().getHeaders().getContentType();
            }

            @Override
            protected byte[] getContent(Operation operation) {
                return operation.getResponse().getContent();
            }
        };
    }

    public static AbstractBodySnippet requestBodyCustom(String name, PayloadSubsectionExtractor<?> subsectionExtractor, Map<String, Object> attributes) {
        return new AbstractBodySnippet(name, subsectionExtractor, attributes) {
            @Override
            protected byte[] getContent(Operation operation) {
                return operation.getRequest().getContent();
            }

            @Override
            protected MediaType getContentType(Operation operation) {
                return operation.getRequest().getHeaders().getContentType();
            }
        };
    }

    public static AbstractBodySnippet responseBodyCustom(String name, PayloadSubsectionExtractor<?> subsectionExtractor, Map<String, Object> attributes) {
        return new AbstractBodySnippet(name, subsectionExtractor, attributes) {
            @Override
            protected byte[] getContent(Operation operation) {
                return operation.getResponse().getContent();
            }

            @Override
            protected MediaType getContentType(Operation operation) {
                return operation.getResponse().getHeaders().getContentType();
            }
        };
    }

    public static AbstractParametersSnippet requestParametersCustom(String name, Map<String, Object> attributes, ParameterDescriptor... descriptors) {
        boolean ignoreUndocumented = false;
        List<ParameterDescriptor> descriptorsList = Arrays.asList(descriptors);

        return new AbstractParametersSnippet(name, descriptorsList, attributes, ignoreUndocumented) {
            @Override
            protected Set<String> extractActualParameters(Operation operation) {
                return operation.getRequest().getParameters().keySet();
            }

            @Override
            protected void verificationFailed(Set<String> undocumentedParameters, Set<String> missingParameters) {
                String message = "";
                if (!undocumentedParameters.isEmpty()) {
                    message += "Request parameters with the following names were not documented: " + undocumentedParameters;
                }
                if (!missingParameters.isEmpty()) {
                    if (message.length() > 0) {
                        message += ". ";
                    }
                    message += "Request parameters with the following names were not found in the request: "
                            + missingParameters;
                }
                throw new SnippetException(message);
            }
        };
    }
}