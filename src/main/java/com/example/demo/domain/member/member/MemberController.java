package com.example.demo.domain.member.member;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.RepositoryRestExceptionHandler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Collection;

@RepositoryRestController
public class MemberController extends RepositoryRestExceptionHandler {
    private final MemberRepository memberRepository;

    @Autowired
    MemberController(MessageSource messageSource,
                     MemberRepository memberRepository) {
        super(messageSource);
        this.memberRepository = memberRepository;
    }

    @RequestMapping("/member")
    Object request(HttpServletRequest request) {
        Assert.isTrue(request.getMethod().equals(RequestMethod.DELETE.toString()),
                "The method use only \"Delete\" and \"Post\"");
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/member")
    Object deleteUser(Principal principal) {
        Member member = (Member) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        memberRepository.delete(member);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/member")
    Object updateUser(PersistentEntityResourceAssembler assembler, Principal principal,
                      @RequestBody EntityModel<Member> entityModel) throws IllegalAccessException {
        Assert.notNull(principal, "principal must be not null");
        Member oldMember = (Member) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Member newMember = entityModel.getContent();

        for (Field field : Member.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object oVal = field.get(oldMember);
            Object nVal = field.get(newMember);

            if (Collection.class.isAssignableFrom(field.getType())) {
                field.set(newMember, oVal);
            } else {
                if (nVal == null) field.set(newMember, oVal);
            }
        }

        memberRepository.save(newMember);
        return new ResponseEntity(assembler.toFullResource(newMember), HttpStatus.OK);
    }

    @GetMapping("/member")
    Object readUser(PersistentEntityResourceAssembler assembler, Principal principal) {
        Assert.notNull(principal, "principal must be not null");
        Member member = (Member) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        return new ResponseEntity(assembler.toFullResource(member), HttpStatus.OK);
    }
}