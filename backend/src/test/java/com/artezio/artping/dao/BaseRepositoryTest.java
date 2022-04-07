package com.artezio.artping.dao;

import com.artezio.artping.entity.calendar.Calendar;
import com.artezio.artping.entity.office.Office;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.entity.user.UserEntity;

import java.util.HashSet;

public abstract class BaseRepositoryTest {

    public Calendar createCalendar() {
        Calendar calendar = new Calendar();
        calendar.setName("test");
        calendar.setActive(true);
        calendar.setEvents(new HashSet<>());
        return calendar;
    }

    public Office createOffice() {
        Office office = new Office();
        office.setName("testOffice");
        office.setCalendar(createCalendar());
        return office;
    }

    public Employee createEmployee() {
        Employee employee = new Employee();
        employee.setEmail("testEmail");
        employee.setFirstName("firstName");
        employee.setLastName("lastName");
        employee.setLogin("testLogin");
        employee.setBaseOffice(createOffice());
        employee.setUser(createUser(employee));
        return employee;
    }

    public UserEntity createUser(Employee employee) {
        UserEntity user = new UserEntity();
        user.setLogin(employee.getLogin());
        user.setPassword("123");
        user.setActive(true);
        user.setEmployee(employee);
        return user;
    }
}
