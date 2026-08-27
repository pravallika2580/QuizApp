import { test, expect } from '@playwright/test';

test('user can complete a quiz', async ({ page }) => {
  await page.goto('/');

  await page.getByRole('button', { name: 'View Quizzes' }).click();
  await page.getByRole('button', { name: 'Ok' }).click();
  await page.locator('#quizap__GetQuizzes__el_btn_1_0').click();

  const answer = page.getByRole('textbox', { name: 'Answer' });
  await answer.fill('A');
  await page.locator('#quizap__GetQuestions__el_btn_4').click();
  await answer.fill('B');
  await page.locator('#quizap__GetQuestions__el_btn_5').click();
  await page.getByRole('button', { name: 'Ok' }).click();

  await expect(page).toHaveURL(/QuizApp/);
});
