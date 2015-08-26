package com.voice.signups;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class MyListener extends TestListenerAdapter  {

    private int m_count = 0;

      @Override
      public void onTestFailure(ITestResult tr) {
        log("\nMy Listener: Fail\n");
      }

      @Override
      public void onTestSkipped(ITestResult tr) {
        log("\nMy Listener: Skipped\n");
      }

      @Override
      public void onTestSuccess(ITestResult tr) {
        log("\nMy Listener: Success\n");
      //## WANT TO PRINT HERE THE TESTCASE CLASS NAME
      }

      private void log(String string) {
        System.out.print(string);
        if (++m_count % 40 == 0) {
          System.out.println("");
        }
      }

}