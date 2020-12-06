package com.example.demo.config.docs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class DocsController {
    ApplicationContext applicationContext;

    @Autowired
    DocsController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @GetMapping(path = "/docs")
    ModelAndView getDocsIndex() throws IOException {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("docs");

        Pattern p = Pattern.compile(".html$");
        Resource[] resources = applicationContext.getResources("classpath:static/docs/*.html");

        List<String> docs = Arrays.stream(resources)
                .map(resource -> p.matcher(resource.getFilename()).replaceAll(""))
                .collect(Collectors.toList());
        mv.addObject("files", docs);
        return mv;
    }

    @GetMapping(path = "/docs/{docsName}")
    String getDocs(@PathVariable String docsName) {
        return String.format("/docs/%s", docsName);
    }
}
