package com.web.controller;

import com.web.model.Person;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping
//Apiע�⣬������Ϣ ��ͨ��tag���з���
//@Api(value = "ApiController", description = "ApiController")
public class ApiController {


    @PostMapping("/addPerson")
    //��������
    @ApiOperation(notes = "�����Ա", value = "addPerson")
    public Person addPerson(
            @ApiParam(name = "name", value = "����") @RequestParam("name") String name,
            @ApiParam(name = "age", value = "����")  @RequestParam("age") Integer age) {
        Person person = new Person();
        person.setAge(age);
        person.setName(name);

        return person;
    }
}