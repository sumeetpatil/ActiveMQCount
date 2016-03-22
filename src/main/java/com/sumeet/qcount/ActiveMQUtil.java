package com.sumeet.qcount;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ActiveMQUtil {
	
	ActiveMQConnection connection = null;
	Logger log = Logger.getLogger(this.getClass());
	int rowCount = 1;
	
	private ActiveMQConnection AMQConnectionOpen() throws JMSException{
		if(connection==null){
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Constants.ACTIVEMQ_CONNECTION);
			connection = (ActiveMQConnection)connectionFactory.createConnection();
			
			connection.start();
		}
		return connection;
	}
	
	
	public void ActiveMQConnectionClose() throws JMSException{
		if(connection!=null){
			connection.close();
		}
	}
	
	
	public void getAMQCount() throws JMSException, NamingException, IOException{
		Set<ActiveMQQueue> queues = AMQConnectionOpen().getDestinationSource().getQueues();
		Iterator<ActiveMQQueue> qIterator = queues.iterator(); 
		Workbook workBook = new HSSFWorkbook();
		Sheet sheet = workBook.createSheet("Queue Count");

		//set title
		Row row = sheet.createRow(0);
		row.createCell(0).setCellValue("Queue Name");
		row.createCell(1).setCellValue("Message Count");

		while(qIterator.hasNext()){
			ActiveMQQueue queue = qIterator.next();
			browseAndCreateRow(queue.getQueueName(), sheet);
		}

		FileOutputStream outputStream = new FileOutputStream("QCount.xlsx");
		workBook.write(outputStream);
		log.info("Write Done!!");
		ActiveMQConnectionClose();
	}
	
	
	private void browseAndCreateRow(String deviceId, Sheet sheet) throws JMSException{

		Session session = AMQConnectionOpen().createSession(false, Session.AUTO_ACKNOWLEDGE);

		Queue q = session.createQueue(deviceId);

		QueueBrowser queueBrowser = session.createBrowser(q);

		Enumeration e = queueBrowser.getEnumeration();
		int numMsgs = 0;

		while (e.hasMoreElements()) {
			Message message = (Message) e.nextElement();
			numMsgs++;
		}

		log.info("Queue Count for Q - "+deviceId + " is " + numMsgs);
		Row row = sheet.createRow(rowCount++);
		row.createCell(0).setCellValue(deviceId);
		row.createCell(1).setCellValue(numMsgs);		
		session.close();
	}
}
