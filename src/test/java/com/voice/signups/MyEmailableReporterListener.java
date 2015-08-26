package com.voice.signups;

import java.util.List;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.reporters.EmailableReporter;
import org.testng.xml.XmlSuite;
import utilities.SendEmail;

public class MyEmailableReporterListener extends EmailableReporter implements IReporter{
	@Override
	public void generateReport(List<XmlSuite> xml, List<ISuite> suites, String outdir) {
	     super.generateReport(xml, suites, outdir);
	     SendEmail.sendEmail();
	}
	
}
