package com.example.demo.data.member.member;

import com.example.demo.domain.board.Message;
import com.example.demo.domain.board.Post;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ProviderType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.commons.util.AnnotationUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EntityTest {
    final ObjectMapper mapper = new ObjectMapper();
    final Member member;

    EntityTest() {
        Message message = mock(Message.class);
        Post post = mock(Post.class);
        this.member = Member.builder()
                .id((long) 1)
                .email("test@test.com")
                .nickname("test")
                .provider(ProviderType.KAKAO)

                .posts(Lists.newArrayList(post))
                .messages(Lists.newArrayList(message))
                .build();
    }

    @Nested
    @Tag("entity")
    class Example_when {
        @Test
        void Member_has_No_Null_Fields() {
            assertThat(member).hasNoNullFieldsOrProperties();
        }
    }

    @Nested
    @Tag("type")
    class Type_of {
        @Test
        void id_is_Long() {
            assertTrue(member.getId() instanceof Long);
        }

        @Test
        void email_is_String() {
            assertTrue(member.getEmail() instanceof String);
        }

        @Test
        void nickName_is_String() {
            assertTrue(member.getEmail() instanceof String);
        }

        @Test
        void Provider_is_ProviderType() {
            assertTrue(member.getProvider() instanceof ProviderType);
        }
    }

    @Nested
    @Tag("association")
    class Association_that {
        @Test
        void A_Member_has_Many_Posts() {
            assertTrue(member.getPosts() instanceof Collection);
        }

        @Test
        void A_Member_has_Many_Messages() {
            assertTrue(member.getMessages() instanceof Collection);
        }
    }

    @Nested
    @Tag("constraint")
    class Constraint_that {
        @Test
        void PK() throws NoSuchFieldException {
            List<String> pkList = Arrays.asList("id");

            List<Field> idFields = AnnotationUtils.findAnnotatedFields(Member.class, Id.class, (field -> true));
            List<String> idFieldNames = idFields.stream().map(field -> field.getName()).collect(Collectors.toList());

            System.out.println("pkList = " + pkList);
            System.out.println("idFields = " + idFieldNames);
            System.out.println("Field \"id\" is Auto Generated.");

            assertAll(
                    () -> assertTrue(idFieldNames.containsAll(pkList)),
                    () -> assertTrue(pkList.containsAll(idFieldNames)),
                    () -> assertTrue(AnnotationUtils.isAnnotated(Member.class.getDeclaredField("id"), GeneratedValue.class))
            );
        }

        @Test
        void Pattern() {
            Map<String, String> patternMap = new HashMap<>(Map.ofEntries(
                    new AbstractMap.SimpleEntry<>("email", "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
            ));
            List<String> patternList = patternMap.keySet().stream().collect(Collectors.toList());

            List<Field> patternFields = AnnotationUtils.findAnnotatedFields(Member.class, Pattern.class, field -> true);
            List<String> patternFieldNames = patternFields.stream().map(p -> p.getName()).collect(Collectors.toList());

            System.out.println("patternList = " + patternList);
            System.out.println("patternFields = " + patternFieldNames);

            assertAll(
                    () -> assertTrue(patternFieldNames.containsAll(patternList)),
                    () -> assertTrue(patternList.containsAll(patternFieldNames)),
                    () -> assertTrue(
                            patternFields.stream().allMatch(field -> {
                                String pattern = patternMap.get(field.getName());
                                String regex = field.getDeclaredAnnotation(Pattern.class).regexp();

                                System.out.println(field.getName() + " pattern = " + pattern);
                                System.out.println(field.getName() + " regex = " + regex);

                                return pattern.equals(regex);
                            }))
            );
        }

        @Test
        void Unique() throws JsonProcessingException {
            List<String[]> uniqList = Arrays.asList(
                    new String[]{"email"}, new String[]{"nickname"});

            Table table = AnnotationUtils.findAnnotation(Member.class, Table.class).get();
            List<String[]> uniqConstraints = Arrays.stream(table.uniqueConstraints()).map(c -> c.columnNames()).collect(Collectors.toList());

            List<Boolean> checkList = new ArrayList<>();
            uniqConstraints.forEach(arr -> checkList.add(
                    uniqList.stream().anyMatch(s -> Arrays.asList(s).containsAll(Arrays.asList(arr)))));

            List<Boolean> checkFields = new ArrayList<>();
            uniqList.forEach(arr -> checkFields.add(
                    uniqConstraints.stream().anyMatch(s -> Arrays.asList(s).containsAll(Arrays.asList(arr)))));

            System.out.println("uniqList = " + mapper.writeValueAsString(uniqList));
            System.out.println("uniqConstraints = " + mapper.writeValueAsString(uniqConstraints));
            assertAll(
                    () -> assertTrue(checkFields.stream().anyMatch(b -> b == true)),
                    () -> assertTrue(checkList.stream().anyMatch(b -> b == true))
            );
        }
    }
}