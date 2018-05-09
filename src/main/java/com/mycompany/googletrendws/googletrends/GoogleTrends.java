/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.googletrends;

//import com.google.inject.Inject;
import com.mycompany.googletrendws.helper.LocationFactory;
import com.mycompany.googletrendws.helper.LocationHelper;
import java.io.File;
//import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 *
 * @author jupiter
 */
public class GoogleTrends {
//        @Inject
//        @Named("driver")WebDriver driver;

      WebDriver driver;

      public GoogleTrends() {

      }

      
      
      
      public void run2(String query) {
//            File csv = new File("C:\\Users\\jupiter\\Downloads\\multiTimeline.csv");
                String fileURL = LocationHelper.getDownloadDir() + "multiTimeline.csv";
            File csv = new File(fileURL);
            if (csv.exists()) {
                  System.out.println("delete old file");
                  csv.delete();
            }
            
            
            FirefoxProfile firefoxProfile = new FirefoxProfile();
              firefoxProfile.setPreference("browser.download.folderList", 2);
              firefoxProfile.setPreference("browser.download.manager.showWhenStarting", false);
              firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv");
              firefoxProfile.setPreference("browser.download.dir", LocationHelper.getDownloadDir());
              driver = new FirefoxDriver(firefoxProfile);
            
            if (driver == null) {
                  System.out.println("driver empty");
            }
            
            
            driver.get(query);
            driver.manage().window().maximize();
//            sleep(30000);
            sleep(10000);
            try {
//                  WebElement buttonGroup = driver.findElement(By.xpath("/html/body/div[2]/div[2]/md-content/div/div/div[1]/trends-widget/ng-include/widget/div/div/div/widget-actions/button"));
                  WebElement buttonGroup = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/md-content/div/div/div[1]/trends-widget/ng-include/widget/div/div/div/widget-actions/button"));
                  buttonGroup.click();
                  sleep(2000);
//                  WebElement button = driver.findElement(By.xpath("/html/body/div[2]/div[2]/md-content/div/div/div[1]/trends-widget/ng-include/widget/div/div/div/widget-actions/div/button[3]"));
                  WebElement button = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/md-content/div/div/div[1]/trends-widget/ng-include/widget/div/div/div/widget-actions/div/button[3]"));
                  button.click();
                  sleep(2000);

                  driver.quit();
            } catch (Exception e) {
                  e.printStackTrace();
                  if (driver != null)
                          driver.quit();
            } finally {
                  if (driver != null)
                          driver.quit();
            }
      }

      void sleep(long time) {
            try {
                  Thread.sleep(time);
            } catch (Exception e) {
            }
      }

      public void saveFile(String fileName) {
            try {
                  File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                  FileUtils.copyFile(scrFile, new File("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\GoogleTrendWS\\" + fileName + ".png"));
                  Thread.sleep(2000);
            } catch (Exception e) {
            }
      }

}
