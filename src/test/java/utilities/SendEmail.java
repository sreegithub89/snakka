package utilities;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;


public class SendEmail
{
	public static void sendEmail()
	{    
		String to = "snakka@corp.untd.com";
		String from ="snakka@corp.untd.com";
		String host = "10.103.32.60";
		String attachmentPath=System.getProperty("user.dir")+"/target/surefire-reports/";
		//String attachmentPath="D:/NewProjects/MVNO/20150727_UOL_RET_Wireframe_v5/";
		String attachmentName="emailable-report.html";
		//String attachmentName="20150727_UOL_RET_Wireframe_v5_Page_01.png";
		System.out.println(attachmentName);
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		Session session = Session.getDefaultInstance(properties);

		try{
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject("Automation Report");
			message.setText("Voice Signup is success!! Find the attached!");
			
			
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();
			DataSource fds = new FileDataSource(attachmentPath+"\\"+attachmentName);
			messageBodyPart.setDataHandler(new DataHandler(fds));
			messageBodyPart.setFileName(fds.getName());
			
			multipart.addBodyPart(messageBodyPart);
			//String htmlText = "<H1>Hello</H1><img src=\"D:/NewProjects/MVNO/20150727_UOL_RET_Wireframe_v5/20150727_UOL_RET_Wireframe_v5_Page_01.png\">";
			//messageBodyPart.setContent(htmlText, "text/html");
			messageBodyPart.setHeader("Content-Type", "text/html");
			
			message.setContent(multipart);
			message.saveChanges();
			Transport.send(message);
			
			System.out.println("*********Sent email successfully....*********");
			/*Transport transport = session.getTransport("smtp");
			transport.connect(host, user, password)
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();	*/
			
		}
		catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}