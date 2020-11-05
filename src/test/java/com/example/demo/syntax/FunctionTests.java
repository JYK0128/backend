package com.example.demo.syntax;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class FunctionTests {

    @Test
    void concurrentHashMap_Test() {
        Map<String, String> myMap = new ConcurrentHashMap<>();
        myMap.computeIfAbsent("test", k -> k + " value");
        System.out.println(myMap.get("test"));
    }

    @Test
    void functional_interface_1(){
        //<param, return>
        Function<String, String> function = str->str + " with function";
        System.out.println("function: " + function.apply("test"));

        //<param> return boolean
        Predicate<String> predicate = str->str.isEmpty();
        System.out.println("predicate: " + predicate.test("test"));

        //<param> return void
        Consumer<String> consumer = str -> System.out.println("consumer: " + str);
        consumer.accept("test");

        //void param <return>
        Supplier<String> supplier = ()-> "test";
        System.out.println("supplier: " + supplier.get());
    }

    @Test
    void functional_interface_2(){
        List<String> authorities = Arrays.asList("a", "b", "c");
        testMethods(authorities.toArray(new String[authorities.size()]));
    }

    void testMethods(String... test){
        Arrays.stream(test).forEach(System.out::println);
    }

    @Test
    void restTemplate() throws URISyntaxException {
        String token = "ya29.a0AfH6SMCVp24IwenwETZJqY-2ICYnzYgSTrTqHm5N8moDE-fGEJzFd5f7RmHkr7kqTDbdI_rFXhGV7YQcfFxnl32a2l-8erKZROQqdCImdpWC2rWrHu6u-FZ0rOQGNlRdDdT-EBYAzTJTU289-0n36oruDqBymQ1V9Xh9";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        URI tokenInfoURI = new URI("https://www.googleapis.com/oauth2/v2/tokeninfo");
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(tokenInfoURI,
                HttpMethod.GET, entity, Map.class);
        HttpStatus tokenResponseStatus = tokenResponse.getStatusCode();
    }

    @Test
    void pattern_match(){
        String[] token = {
                "E9A7f7-W1JBGj3EOLLq6UYGkWxr6__xsSfAGDgo9c00AAAF1LALZOg",
                "QdrYLtbW4hvSCkYQlRFq7ODez2stx-yzzfVRgAo9cpgAAAF1LAPLgQ",
                "BVyRSIKz-ZNcbv-OLUXCuEX0TnIbQYVsAK_27Qo9dRsAAAF1LARYNg",
                "OaVAUaAUIkGC82Mps4C33oGWKrTalXuF74sRxgo9dNsAAAF1LAhQrQ",
                "_uoN1m4ee6DTzSHFKqMVnNmPegQbn9DIyPE45wo9dJgAAAF1LAkIow"
        };

        Pattern pattern = Pattern.compile("^.{43}AAAF1LA.{4}$");
        Arrays.stream(token).forEach((t) -> assertTrue(pattern.matcher(t).find()));
    }
}
