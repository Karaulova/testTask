package autotest;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchPositionTest {
    private static WebDriver driver;

    @BeforeClass
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("http://google.com");
    }
    @Test
    public void searchPos() throws IOException, InterruptedException, AWTException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("src/main/resources/config.txt")));
        String department = bufferedReader.readLine();
        String location = bufferedReader.readLine();
        WebElement strSearchField = driver.findElement(By.id("lst-ib"));
        strSearchField.sendKeys("Netcracker");
        strSearchField.submit();
        String nCOpenPos = "Netcracker - Open Positions";
        //Ссылка на Netcracker - Open Positions находится не на первой странице,
        //пришлось использовать клик на Следующая (pnnext) по отношению к текущей странице результатов поиска
        List<WebElement> strSearchLinkFields=driver.findElements(By.linkText(nCOpenPos));
        WebElement nextButton;
        int linkCount=strSearchLinkFields.size();
        while (linkCount==0){
            nextButton = driver.findElement(By.id("pnnext"));
            nextButton.click();
            strSearchLinkFields=driver.findElements(By.linkText(nCOpenPos));
            linkCount=strSearchLinkFields.size();
        }
        strSearchLinkFields.get(0).click();

        //отображение списка департаментов
        driver.findElement(By.cssSelector("[data-id=\"jobdept\"]")).click();
        //выбор департамента
        driver.findElement(By.cssSelector("#jobdept-group [role=\"combobox\"]")).findElement(By.linkText(department)).click();
        //поиск локации
        WebElement keywordField = driver.findElement(By.id("keyword"));
        keywordField.sendKeys(location);
        //Здесь пришлось немного усыпить, так как не успевало прогрузиться с использованием локации
        Thread.sleep(2000);
        //Переход на страницу вакансии
        driver.findElement(By.cssSelector(".job.result.active .more-link.moreicon")).click();

        OutputStream out = new FileOutputStream("src/screenshot.png");
        out.write(((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES));
        out.close();

    }
}
