package com.example.demo.app.inquiry;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.entity.Inquiry;
import com.example.demo.service.InquiryNotFoundException;
import com.example.demo.service.InquiryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;

    @Autowired
    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @GetMapping
    public String index(Model model) {
        List<Inquiry> list = inquiryService.getAll();

        // 例外確認のためあえて例外を発生させる処理
        // this.illegalUpdate(model);

        model.addAttribute("list", list);
        model.addAttribute("title", "受付一覧");
        System.out.println(list);

        return "inquiry/index";
    }

    @GetMapping("/form")
    public String form(InquiryForm inquiryForm, Model model, @ModelAttribute("complete") String complete) {
        model.addAttribute("title", "受付フォーム");
        return "inquiry/form";
    }

    @PostMapping("/form")
    public String formGoBack(InquiryForm inquiryForm, Model model) {
        model.addAttribute("title", "受付フォーム");
        return "inquiry/form";
    }

    @PostMapping("/confirm")
    public String confirm(@Validated InquiryForm inquiryForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("title", "受付フォーム");
            return "inquiry/form";
        }

        model.addAttribute("title", "確認ページ");
        return "inquiry/confirm";
    }

    @PostMapping("/complete")
    public String complete(@Validated InquiryForm inquiryForm, BindingResult result, Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "受付フォーム");
            return "inquiry/form";
        }

        Inquiry i = new Inquiry();
        i.setName(inquiryForm.getName());
        i.setEmail(inquiryForm.getEmail());
        i.setContents(inquiryForm.getContents());
        i.setCreated(LocalDateTime.now());

        inquiryService.save(i);

        redirectAttributes.addFlashAttribute("complete", inquiryForm.getName() + "様、登録ありがとうございます！");
        return "redirect:/inquiry/form";
    }

    private void illegalUpdate(Model model) {
        Inquiry i = new Inquiry();
        i.setId(4); // 存在しないID
        i.setName("name");
        i.setEmail("email");
        i.setContents("contents");

        try {
            inquiryService.update(i);
        } catch (InquiryNotFoundException e) {
            model.addAttribute("message", e);
            e.printStackTrace();
        }

        inquiryService.update(i);

    }

    // コントローラに記載するとコントローラ内のメソッドに対してキャッチ可能
    // WebMvcControllerAdviceに記載した物よりこちらの方が優先度が高いので両方に記載がある場合はこちらが実行される。
    @ExceptionHandler(InquiryNotFoundException.class)
    public String handleException(InquiryNotFoundException e, Model model) {
        model.addAttribute("message", "コントローラ内：" + e.toString());
        return "error/CustomPage";
    }
}
