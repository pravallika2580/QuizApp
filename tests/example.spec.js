import { test, expect } from '@playwright/test';

test('user can complete a quiz', async ({ page }) => {
  const appUrl = process.env.PLAYWRIGHT_BASE_URL || 'http://localhost:8111/QuizApp/';
  await page.goto(appUrl, { waitUntil: 'networkidle', timeout: 60000 });

  await page.waitForTimeout(5000);
  const viewQuizzes = page.locator('#quizap__HomePage__el_btn_1');
  await expect(viewQuizzes).toBeVisible({ timeout: 90000 });
  await viewQuizzes.click({ force: true });
  await page.getByRole('button', { name: 'Ok' }).click({ force: true });
  await page.locator('#quizap__GetQuizzes__el_btn_1_0').click({ force: true });

  const answer = page.getByRole('textbox', { name: 'Answer' });
  await answer.fill('A');
  await page.locator('#quizap__GetQuestions__el_btn_4').click({ force: true });
  await answer.fill('B');
  await page.locator('#quizap__GetQuestions__el_btn_5').click({ force: true });
  await page.getByRole('button', { name: 'Ok' }).click({ force: true });

  await expect(page).toHaveURL(/QuizApp/);
});
