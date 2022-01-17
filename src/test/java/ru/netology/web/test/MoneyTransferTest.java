package ru.netology.web.test;

import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ru.netology.web.data.DataHelper;

public class MoneyTransferTest {
    int amountToTransfer = 1000;
    int newAmountToTransferOverBalance = 12000;


    @BeforeEach
    void setup (){
        open ("http://localhost:9999");
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = loginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
    }

    @AfterEach
    void asserting (){
        val dashboardPage = new DashboardPage();
        val balance1 = dashboardPage.getFirstCardBalance();
        val balance2 = dashboardPage.getSecondCardBalance();

        if (balance1 - balance2 > 0) {
            val transferAmount = (balance1 - balance2) / 2;
            val cardInfo = DataHelper.getFirstCardInfo();
            val cardRefillPage = dashboardPage.chooseSecondCardToRefill();
            cardRefillPage.moneyTransfer(cardInfo, amountToTransfer);
        }
        if (balance2 - balance1 > 0){
            val transferAmount = (balance2 - balance1) / 2;
            val cardInfo = DataHelper.getSecondCardInfo();
            val cardRefillPage = dashboardPage.chooseFirstCardToRefill();
            cardRefillPage.moneyTransfer(cardInfo, amountToTransfer);
        }
    }

    @Test
    void shouldTransferMoneyFromFirstToSecondCard (){
        val dashboardPage = new DashboardPage();
        val balanceFirstCardBefore = dashboardPage.getFirstCardBalance();
        val balanceSecondCardBefore = dashboardPage.getSecondCardBalance();
        val cardRefillPage = dashboardPage.chooseSecondCardToRefill();
        val cardInfo = DataHelper.getFirstCardInfo();
        cardRefillPage.moneyTransfer (cardInfo, 1000);
        val balanceAfterTransactionOnRecharged = DataHelper.checkBalanceCardTransferTo(balanceSecondCardBefore, amountToTransfer);
        val balanceAfterTransaction = DataHelper.checkBalanceCardTransferFrom(balanceFirstCardBefore, amountToTransfer);
        val balanceFirstCardAfter = dashboardPage.getFirstCardBalance();
        val balanceSecondCardAfter = dashboardPage.getSecondCardBalance();
        assertEquals(balanceAfterTransactionOnRecharged, balanceSecondCardAfter);
        assertEquals(balanceAfterTransaction, balanceFirstCardAfter);

    }

    @Test
    void shouldTransferMoneyFromSecondToFirstCard (){
        val dashboardPage = new DashboardPage();
        val balanceFirstCardBefore = dashboardPage.getFirstCardBalance();
        val balanceSecondCardBefore = dashboardPage.getSecondCardBalance();
        val cardRefillPage = dashboardPage.chooseFirstCardToRefill();
        val cardInfo = DataHelper.getSecondCardInfo();
        cardRefillPage.moneyTransfer(cardInfo, 1000);
        val balanceAfterTransactionOnRecharged = DataHelper.checkBalanceCardTransferTo(balanceFirstCardBefore, amountToTransfer);
        val balanceAfterTransaction = DataHelper.checkBalanceCardTransferFrom(balanceSecondCardBefore, amountToTransfer);
        val balanceFirstCardAfter = dashboardPage.getFirstCardBalance();
        val balanceSecondCardAfter = dashboardPage.getSecondCardBalance();
        assertEquals(balanceAfterTransactionOnRecharged, balanceFirstCardAfter);
        assertEquals(balanceAfterTransaction, balanceSecondCardAfter);

    }

    @Test
    void shouldTransferMoneyFromSecondCardToFirstCardIfAmountTransferIsOverBalance(){
        val dashboardPage = new DashboardPage();
        val balanceFirstCardBefore = dashboardPage.getFirstCardBalance();
        val balanceSecondCardBefore = dashboardPage.getSecondCardBalance();
        val cardRefillPage = dashboardPage.chooseFirstCardToRefill();
        val cardInfo = DataHelper.getSecondCardInfo();
        cardRefillPage.moneyTransfer(cardInfo, newAmountToTransferOverBalance);
        val dashboardPageWithError = new DashboardPage();
        dashboardPageWithError.getNotificationVisible();


    }


}
