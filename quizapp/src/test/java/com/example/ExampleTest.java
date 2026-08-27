package com.example;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;

public class ExampleTest {

    @Test
    void quizAppFlow() {

        String appUrl = System.getenv().getOrDefault(
            "PLAYWRIGHT_BASE_URL",
            "http://localhost:8111/QuizApp/"
        );
        boolean headless = !"false".equalsIgnoreCase(
            System.getenv().getOrDefault("PLAYWRIGHT_HEADLESS", "true")
        );

        Playwright playwright = Playwright.create();

        Browser browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions()
                .setHeadless(headless)
        );

        BrowserContext context = browser.newContext();
        Page page = context.newPage();

        // =========================
        // OPEN APPLICATION
        // =========================

        page.navigate(appUrl);

        page.waitForTimeout(5000);

        System.out.println("Quiz application opened successfully");

        // =========================
        // QUIZ FLOW
        // =========================

        viewQuizzes(page);

        selectQuiz(page);

        attendQuiz(page);

        answerQuestion1(page);

        answerQuestion2(page);

        submitQuiz(page);

        browser.close();
        playwright.close();
    }


    // =====================================================
    // VIEW QUIZZES
    // =====================================================

    void viewQuizzes(Page page) {

        System.out.println("Viewing quizzes...");

        page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions()
                .setName("View Quizzes")
        ).click();

        // Click OK popup
        page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions()
                .setName("Ok")
        ).click();
    }


    // =====================================================
    // SELECT QUIZ
    // =====================================================

    void selectQuiz(Page page) {

        System.out.println("Selecting quiz...");

        page.locator(
            "#quizap__GetQuizzes__el_btn_1_0"
        ).click();
    }


    // =====================================================
    // ATTEND QUIZ
    // =====================================================

    void attendQuiz(Page page) {

        System.out.println("Starting quiz...");

        // Your recorded flow does not contain
        // a separate Attend Quiz click.
        //
        // Selecting the quiz appears to start
        // the quiz/question screen.
    }


    // =====================================================
    // ANSWER QUESTION 1
    // =====================================================

    void answerQuestion1(Page page) {

        System.out.println("Answering question 1...");

        Locator answer = page.getByRole(
            AriaRole.TEXTBOX,
            new Page.GetByRoleOptions()
                .setName("Answer")
        );

        answer.click();

        answer.fill("A");

        // Move to next question
        page.locator(
            "#quizap__GetQuestions__el_btn_4"
        ).click();
    }


    // =====================================================
    // ANSWER QUESTION 2
    // =====================================================

    void answerQuestion2(Page page) {

        System.out.println("Answering question 2...");

        Locator answer = page.getByRole(
            AriaRole.TEXTBOX,
            new Page.GetByRoleOptions()
                .setName("Answer")
        );

        answer.click();

        answer.fill("B");

        // Submit / move to next step
        page.locator(
            "#quizap__GetQuestions__el_btn_5"
        ).click();
    }


    // =====================================================
    // SUBMIT QUIZ
    // =====================================================

    void submitQuiz(Page page) {

        System.out.println("Submitting quiz...");

        page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions()
                .setName("Ok")
        ).click();
    }
}