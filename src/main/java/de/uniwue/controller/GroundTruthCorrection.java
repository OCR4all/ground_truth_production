package de.uniwue.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GroundTruthCorrection {
    String message = "Welcome to Spring MVC!";
 
    @RequestMapping("/")
    public ModelAndView showMessage(
        @RequestParam(value = "name", required = false, defaultValue = "World") String name) {

        ModelAndView mv = new ModelAndView("groundtruthcorrection");
        mv.addObject("message", message);
        mv.addObject("name", name);
        return mv;
    }
}
