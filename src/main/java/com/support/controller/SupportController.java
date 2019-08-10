package com.support.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SupportController {

    //@Autowired
    //private RoomBiz roomBiz;

    @GetMapping("/home")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        //modelAndView.addObject("rooms", roomBiz.getAllRoomList());
        modelAndView.setViewName("/home.html");
        return modelAndView;
    }
}
