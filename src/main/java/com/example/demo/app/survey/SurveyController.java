package com.example.demo.app.survey;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.entity.Survey;
import com.example.demo.service.SurveyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/survey")
public class SurveyController {

    private final SurveyService surveyService;

    @Autowired
    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Survey> list = surveyService.getAll();

        model.addAttribute("list", list);
        model.addAttribute("title", "受付一覧");
        System.out.println(list);

        return "survey/index";
    }

    @GetMapping("/form")
    public String form(SurveyForm surveyForm, Model model, @ModelAttribute("complete") String complete) {
        model.addAttribute("title", "問合せフォーム");
        return "survey/form";
    }

    @PostMapping("/form")
    public String formGoBack(SurveyForm surveyForm, Model model) {
        model.addAttribute("title", "問合せフォーム");
        return "survey/form";
    }

    @PostMapping("/confirm")
    public String confirm(@Validated SurveyForm surveyForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("title", "問合せフォーム");
            return "survey/form";
        }

        model.addAttribute("title", "確認ページ");
        return "survey/confirm";
    }

    @PostMapping("/complete")
    public String complete(@Validated SurveyForm surveyForm, BindingResult result, Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "問合せフォーム");
            return "survey/form";
        }

        Survey entity = new Survey();
        entity.setSatisfaction(surveyForm.getSatisfaction());
        entity.setAge(surveyForm.getAge());
        entity.setComment(surveyForm.getComments());
        entity.setCreated(LocalDateTime.now());

        surveyService.save(entity);

        redirectAttributes.addFlashAttribute("complete", surveyForm.getAge());
        return "redirect:/survey/form";
    }

}
